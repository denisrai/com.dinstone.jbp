/*
 * Copyright (C) 2012~2013 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dinstone.cache.memcached;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.spy.memcached.MemcachedClient;

class AliveKeeper {

    /** 默认间隔:180000ms */
    private static final int DEFAULT_INTERVAL = 180000;

    private Queue<MemcachedClient> clients;

    private Thread keepAliveRunner;

    private boolean started;

    public AliveKeeper() {
        this(DEFAULT_INTERVAL);
    }

    public AliveKeeper(int interval) {
        clients = new ConcurrentLinkedQueue<MemcachedClient>();

        if (interval > 0) {
            keepAliveRunner = new Thread(new KeepAliveRunner(this, interval), "MemcachedClient-AliveKeeper");
            keepAliveRunner.setDaemon(true);
        }
    }

    public void start() {
        if (!started && keepAliveRunner != null) {
            keepAliveRunner.start();
        }

        started = true;
    }

    public void stop() {
        if (started && keepAliveRunner != null) {
            keepAliveRunner.interrupt();
        }
        started = false;
    }

    public void addMemcachedClient(MemcachedClient client) {
        if (client != null) {
            clients.add(client);
        }
    }

    public void removeMemcachedClient(MemcachedClient client) {
        if (client != null) {
            clients.remove(client);
        }
    }

    public void setMemcachedClients(List<MemcachedClient> memcachedClients) {
        for (MemcachedClient client : memcachedClients) {
            addMemcachedClient(client);
        }
    }

    private void active() {
        for (MemcachedClient client : clients) {
            try {
                client.getVersions();
            } catch (Exception e) {
                // will handle InterruptedException ?
            }
        }
    }

    private static class KeepAliveRunner implements Runnable {

        private AliveKeeper aliveKeeper;

        private int interval;

        public KeepAliveRunner(AliveKeeper aliveKeeper, int interval) {
            this.aliveKeeper = aliveKeeper;
            this.interval = interval;
        }

        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(interval);
                    aliveKeeper.active();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

}
