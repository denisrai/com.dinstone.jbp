
package com.dinstone.leader;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;

public class ZookeeperTest {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // String hostport =
        // "172.21.31.32:2181,172.21.31.33:2181,172.21.31.34:2181";
        // ZooKeeper zooKeeper = new ZooKeeper(hostport, 300000, null);
        // String path = "/test";
        // zooKeeper.delete(path, -1);
        int count = 3;

        CountDownLatch cdl = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            Thread t = new Thread(new MainProcess(i, cdl), "MainProcess-" + i);
            t.start();
        }

        cdl.await();
        // zooKeeper.close();
    }

    static class MainProcess implements Runnable {

        private Integer index;

        private CountDownLatch cdl;

        public MainProcess(int i, CountDownLatch cdl) {
            this.index = i;
            this.cdl = cdl;
        }

        public void run() {
            try {
                // // String hostport =
                // // "172.21.31.32:2181,172.21.31.33:2181,172.21.31.34:2181";
                // Watcher defWatcher = new Watcher() {
                //
                // @Override
                // public void process(WatchedEvent event) {
                // System.out.println("=====>Default Watch Event: " + event);
                // }
                //
                // };
                //
                String hostport = "172.17.20.210:2181";
                // zooKeeper = new ZooKeeper(hostport, 30000, defWatcher);

                LeaderElectionService le = new LeaderElectionService(hostport, 3000, "C-" + index);
                le.setLeaderElectionAware(new LeaderElectionAware() {

                    public void onLeader(LeaderOffer leaderOffer) {
                        System.out.println("I'm leader " + leaderOffer);
                    }

                    public void onFollower(LeaderOffer leaderOffer) {
                        System.out.println("I'm follower " + leaderOffer);
                    }

                    public void onClosed(LeaderOffer leaderOffer) {
                        System.out.println("electionClosed " + leaderOffer);
                    }

                    public void exceptionCaught(Throwable cause) {
                        System.out.println("exceptionCaught " + cause);
                    }
                });

                le.start();

                Thread.sleep((++index) * 10000);

                le.stop();

                // zooKeeper.close(); // 关闭实例
            } catch (Exception e) {
                e.printStackTrace();
            }

            cdl.countDown();
        }
    }
}
