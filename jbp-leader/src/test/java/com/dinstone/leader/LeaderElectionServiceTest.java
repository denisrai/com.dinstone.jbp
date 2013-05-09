/*
 * Copyright (C) 2012~2013 dinstone<dinstone@163.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dinstone.leader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author guojf
 * @version 1.0.0.2013-3-29
 */
public class LeaderElectionServiceTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.dinstone.leader.LeaderElectionService#start()}
     * .
     * 
     * @throws InterruptedException
     */
    @Test
    public void testStart() throws InterruptedException {
        // String hostport = "127.0.0.1:2181";
        String hostport = "172.17.20.210:2181";
        // zooKeeper = new ZooKeeper(hostport, 30000, defWatcher);

        LeaderElectionService le = new LeaderElectionService(hostport, 3000);
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

        Thread.sleep(30000);

        le.stop();
    }

    /**
     * Test method for {@link com.dinstone.leader.LeaderElectionService#stop()}.
     */
    @Test
    public void testStop() {
    }

}
