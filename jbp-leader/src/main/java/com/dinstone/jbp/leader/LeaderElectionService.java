
package com.dinstone.jbp.leader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.jbp.leader.LeaderOffer.LeaderOfferComparator;

public class LeaderElectionService {

    private static final Logger LOG = LoggerFactory.getLogger(LeaderElectionService.class);

    private final String connects;

    private final int timeout;

    private final String candidate;

    private final CountDownLatch connectSingal;

    private ZooKeeper zooKeeper;

    private String offerPath = "/election";

    private LeaderOffer leaderOffer;

    private LeaderElectionAware leaderElectionAware;

    public LeaderElectionService(String connects, int timeout, String candidate) {
        if (connects == null) {
            throw new IllegalArgumentException("connects is null");
        }
        this.connects = connects;

        if (candidate == null) {
            throw new IllegalArgumentException("candidate is null");
        }
        this.candidate = candidate;

        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout less than or equal to 0");
        }
        this.timeout = timeout;

        this.connectSingal = new CountDownLatch(1);
    }

    /**
     * @param hostport
     * @param string
     */
    public LeaderElectionService(String hostport, String candidate) {
        this(hostport, 3000, candidate);
    }

    public synchronized void start() {
        LOG.debug("Starting leader election");
        clear();

        try {
            zooKeeper = new ZooKeeper(connects, timeout, new Watcher() {

                public void process(WatchedEvent event) {
                    if (KeeperState.SyncConnected == event.getState()) {
                        connectSingal.countDown();
                    }
                }

            });
            connectSingal.await();
        } catch (IOException e) {
            failed(e);
            return;
        } catch (InterruptedException e) {
            failed(e);
            return;
        }

        try {
            registOffer();

            determine();
        } catch (KeeperException e) {
            failed(e);
        } catch (InterruptedException e) {
            failed(e);
        }

    }

    public synchronized void stop() {
        clear();
    }

    private void clear() {
        if (zooKeeper != null) {
            if (leaderOffer != null) {
                try {
                    zooKeeper.delete(leaderOffer.getOffer(), -1);
                    LOG.info("Removed leader candidate {}", leaderOffer);
                } catch (InterruptedException e) {
                    failed(e);
                } catch (KeeperException e) {
                    failed(e);
                }

                if (leaderElectionAware != null) {
                    leaderElectionAware.electionClosed(leaderOffer);
                }
                leaderOffer = null;
            }

            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                failed(e);
            }
        }
    }

    private void registOffer() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(offerPath, true);
        if (stat == null) {
            try {
                zooKeeper.create(offerPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (NodeExistsException e) {
                // ignore
            }
        }

        // 注册提议
        String offerPrefix = "offer_";
        String offer = zooKeeper.create(offerPath + "/" + offerPrefix, candidate.getBytes(), Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL);
        // 生成候选提议
        Integer code = Integer.valueOf(offer.substring(offer.lastIndexOf("_") + 1));
        leaderOffer = new LeaderOffer(code, offer, candidate);
    }

    private void determine() throws KeeperException, InterruptedException {
        List<LeaderOffer> leaderOffers = getRegisteredLeaderOffers();
        for (int i = 0; i < leaderOffers.size(); i++) {
            LeaderOffer leaderOffer = leaderOffers.get(i);
            if (this.leaderOffer.getCode().equals(leaderOffer.getCode())) {
                if (i == 0) {
                    // we are leader
                    LOG.info("Becoming leader with node:{}", leaderOffer);
                    if (leaderElectionAware != null) {
                        leaderElectionAware.produceLeader(leaderOffer);
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
        LOG.info("{} not elected leader. Watching node:{}", leaderOffer, previousOffer);

        Stat stat = zooKeeper.exists(previousOffer.getOffer(), new Watcher() {

            public void process(WatchedEvent event) {
                if (event.getType().equals(Watcher.Event.EventType.NodeDeleted)) {
                    LOG.info(event.toString());
                    if (!event.getPath().equals(LeaderElectionService.this.leaderOffer.getOffer())) {
                        LOG.info("Node {} deleted. Need to run through the election process.", event.getPath());
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
            LOG.info("Becoming follower with node:{},We're watching {}", leaderOffer, previousOffer);
            if (leaderElectionAware != null) {
                leaderElectionAware.produceFollower(leaderOffer);
            }
        } else {
            LOG.info("We were behind {} but it looks like they died. Back to determination.", previousOffer);

            determine();
        }
    }

    private void failed(Exception e) {
        LOG.error("Failed in state {} - Exception:{}", e);
        if (leaderElectionAware != null) {
            leaderElectionAware.exceptionCaught(e);
        }
    }

    private List<LeaderOffer> getRegisteredLeaderOffers() throws KeeperException, InterruptedException {
        List<String> offerNames = zooKeeper.getChildren(offerPath, false);
        List<LeaderOffer> leaderOffers = new ArrayList<LeaderOffer>(offerNames.size());
        for (String offerName : offerNames) {
            Integer code = Integer.valueOf(offerName.substring(offerName.lastIndexOf("_") + 1));
            String offer = this.offerPath + "/" + offerName;
            String candidate = new String(zooKeeper.getData(offer, false, null));

            leaderOffers.add(new LeaderOffer(code, offer, candidate));
        }

        // sortting offers
        Collections.sort(leaderOffers, new LeaderOfferComparator());

        return leaderOffers;
    }

    /**
     * the offerPath to set
     * 
     * @param offerPath
     * @see LeaderElectionService#offerPath
     */
    public void setOfferPath(String offerPath) {
        if (offerPath == null) {
            throw new IllegalArgumentException("offerPath is null");
        }
        this.offerPath = offerPath;
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

}
