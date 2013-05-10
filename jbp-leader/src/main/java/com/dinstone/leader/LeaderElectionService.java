
package com.dinstone.leader;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.leader.LeaderOffer.LeaderOfferComparator;

public class LeaderElectionService {

    private static final Logger LOG = LoggerFactory.getLogger(LeaderElectionService.class);

    private String quorumServers;

    private int sessionTimeout;

    private String offerRootPath;

    private String candidate;

    private ZooKeeper zooKeeper;

    private LeaderOffer leaderOffer;

    private LeaderElectionAware leaderElectionAware;

    private int index = 1;

    private boolean started = false;

    /**
     * 
     */
    public LeaderElectionService(String quorumServers, int sessionTimeout, String offerRootPath, String candidate) {
        if (quorumServers == null) {
            throw new IllegalArgumentException("quorumServers is null");
        }
        this.quorumServers = quorumServers;

        this.sessionTimeout = sessionTimeout;

        if (offerRootPath == null) {
            offerRootPath = "/election";
        }
        this.offerRootPath = offerRootPath;

        if (candidate == null) {
            candidate = ManagementFactory.getRuntimeMXBean().getName();
        }
        this.candidate = candidate;
    }

    public LeaderElectionService(String quorumServers, int sessionTimeout, String offerRootPath) {
        this(quorumServers, sessionTimeout, offerRootPath, null);
    }

    /**
     * @param hostport
     * @param i
     */
    public LeaderElectionService(String quorumServers, int sessionTimeout) {
        this(quorumServers, sessionTimeout, null, null);
    }

    public synchronized void start() {
        if (!started) {
            elect();
            started = true;
        }
    }

    public synchronized void stop() {
        if (started) {
            clear();
            started = false;
        }
    }

    /**
     * the leaderElectionAware to set
     * 
     * @param leaderElectionAware
     * @see LeaderElectionService#leaderElectionAware
     */
    public void setLeaderElectionAware(LeaderElectionAware leaderElectionAware) {
        this.leaderElectionAware = leaderElectionAware;
    }

    /**
     *
     */
    private void elect() {
        Runnable target = new Runnable() {

            public void run() {
                doElect();
            }
        };

        Thread thread = new Thread(target, "LeaderElection-T" + (index++));
        thread.setDaemon(true);
        thread.start();
    }

    /**
    *
    */
    private synchronized void doElect() {
        clear();

        try {
            connection();

            regist();

            determine();
        } catch (KeeperException e) {
            failed(e);
        } catch (InterruptedException e) {
            failed(e);
        }
    }

    private void clear() {
        if (zooKeeper != null) {
            if (leaderOffer != null && zooKeeper.getState().isAlive()) {
                try {
                    zooKeeper.delete(leaderOffer.getOffer(), -1);
                    LOG.debug("Removed leader candidate {}", leaderOffer);
                } catch (InterruptedException e) {
                    failed(e);
                } catch (KeeperException e) {
                    failed(e);
                }

                if (leaderElectionAware != null) {
                    leaderElectionAware.onClosed(leaderOffer);
                }
                leaderOffer = null;
            }

            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                failed(e);
            } finally {
                zooKeeper = null;
            }
        }
    }

    private void connection() throws InterruptedException, KeeperException {
        if (this.zooKeeper == null || !zooKeeper.getState().isAlive()) {
            final CountDownLatch connectSingal = new CountDownLatch(1);
            try {
                this.zooKeeper = new ZooKeeper(quorumServers, sessionTimeout, new Watcher() {

                    public void process(WatchedEvent event) {
                        LOG.debug("Received zookeeper event, type={}, state={}", event.getType(), event.getState());
                        if (KeeperState.SyncConnected == event.getState()) {
                            connectSingal.countDown();
                        } else if (KeeperState.Expired == event.getState()) {
                            LOG.debug("Session is expired, need to redo the election process");
                            elect();
                        }
                    }
                });
            } catch (IOException e) {
                throw KeeperException.create(Code.CONNECTIONLOSS);
            }
            connectSingal.await();
        }
    }

    private void regist() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(offerRootPath, true);
        if (stat == null) {
            try {
                zooKeeper.create(offerRootPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (NodeExistsException e) {
                // ignore
            }
        }

        // regist offer
        String offerPrefix = "offer_";
        String offerPath = offerRootPath + "/" + offerPrefix;
        byte[] cdata = candidate.getBytes(Charset.forName("utf-8"));
        String offer = zooKeeper.create(offerPath, cdata, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // create leader offer
        Integer code = Integer.valueOf(offer.substring(offer.lastIndexOf("_") + 1));
        leaderOffer = new LeaderOffer(code, offer, candidate);
    }

    private void determine() throws KeeperException, InterruptedException {
        List<LeaderOffer> leaderOffers = getRegisteredOffers();
        for (int i = 0; i < leaderOffers.size(); i++) {
            LeaderOffer leaderOffer = leaderOffers.get(i);
            if (this.leaderOffer.getCode().equals(leaderOffer.getCode())) {
                if (i == 0) {
                    // we are leader
                    LOG.info("Becoming leader with node {}", leaderOffer);
                    if (leaderElectionAware != null) {
                        leaderElectionAware.onLeader(leaderOffer);
                    }
                } else {
                    // find previous leader's offer and watch it
                    LeaderOffer previousOffer = leaderOffers.get(i - 1);
                    watchCandidate(previousOffer);
                }

                break;
            }
        }

    }

    private void watchCandidate(LeaderOffer previousOffer) throws KeeperException, InterruptedException {
        LOG.info("{} not elected leader. Watching node {}", leaderOffer, previousOffer);

        Stat stat = zooKeeper.exists(previousOffer.getOffer(), new Watcher() {

            public void process(WatchedEvent event) {
                if (event.getType().equals(Watcher.Event.EventType.NodeDeleted)) {
                    if (!event.getPath().equals(LeaderElectionService.this.leaderOffer.getOffer())) {
                        LOG.debug("Node {} deleted. Need to run through the election process.", event.getPath());
                        try {
                            determine();
                        } catch (KeeperException e) {
                            failed(e);
                        } catch (InterruptedException e) {
                            failed(e);
                        }
                    }
                }
            }

        });

        if (stat != null) {
            LOG.info("Becoming follower with node {}, We're watching {}", leaderOffer, previousOffer);
            if (leaderElectionAware != null) {
                leaderElectionAware.onFollower(leaderOffer);
            }
        } else {
            LOG.debug("We were behind {} but it looks like died. Back to determination.", previousOffer);

            determine();
        }
    }

    private void failed(Exception e) {
        if (leaderElectionAware != null) {
            leaderElectionAware.exceptionCaught(e);
        }
    }

    private List<LeaderOffer> getRegisteredOffers() throws KeeperException, InterruptedException {
        List<String> offerNames = zooKeeper.getChildren(offerRootPath, false);
        List<LeaderOffer> leaderOffers = new ArrayList<LeaderOffer>(offerNames.size());
        for (String offerName : offerNames) {
            Integer code = Integer.valueOf(offerName.substring(offerName.lastIndexOf("_") + 1));
            String offer = this.offerRootPath + "/" + offerName;
            String candidate = new String(zooKeeper.getData(offer, false, null), Charset.forName("utf-8"));

            leaderOffers.add(new LeaderOffer(code, offer, candidate));
        }

        // sortting offers
        Collections.sort(leaderOffers, new LeaderOfferComparator());

        return leaderOffers;
    }

}
