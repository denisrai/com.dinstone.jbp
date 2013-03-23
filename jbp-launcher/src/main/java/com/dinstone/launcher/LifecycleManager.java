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

package com.dinstone.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AccessControlException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LifecycleManager {

    private static final Logger LOG = Logger.getLogger(LifecycleManager.class.getName());

    /**
     * The shutdown command string we are looking for.
     */
    private String shutdown = "SHUTDOWN";

    private int port = 5555;

    private boolean await = true;

    private Object activator;

    public LifecycleManager(Object activator, Configuration config) {
        this.activator = activator;

        String portPro = config.getProperty("listen.port");
        try {
            port = Integer.parseInt(portPro);
        } catch (Exception e) {
        }

        String shutdown = config.getProperty("listen.cmdm");
        if (shutdown != null && shutdown.length() > 0) {
            this.shutdown = shutdown;
        }
    }

    private void await() {
        // Set up a server socket to wait on
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "LifecycleManager.await: create[" + port + "]: ", e);
            System.exit(1);
        }

        // Loop waiting for a connection and a valid command
        while (true) {
            // Wait for the next connection
            Socket socket = null;
            InputStream stream = null;
            try {
                socket = serverSocket.accept();
                socket.setSoTimeout(2000); // two seconds
                InetAddress radd = socket.getInetAddress();
                LOG.log(Level.INFO, "Have an closing request at " + radd);
                stream = socket.getInputStream();
            } catch (AccessControlException ace) {
                LOG.log(Level.WARNING, "LifecycleManager.accept security exception: " + ace.getMessage(), ace);
                continue;
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "LifecycleManager.await: accept: ", e);
                System.exit(1);
            }

            // Read a set of characters from the socket
            StringBuilder command = new StringBuilder();
            int expected = shutdown.length(); // Cut off to avoid DoS attack
            while (expected > 0) {
                int ch = -1;
                try {
                    ch = stream.read();
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "LifecycleManager.await: read: ", e);
                    ch = -1;
                }
                if (ch < 32) {// Control character or EOF terminates loop
                    break;
                }
                command.append((char) ch);
                expected--;
            }

            // Close the socket now that we are done with it
            try {
                socket.close();
            } catch (IOException e) {
            }

            // Match against our command string
            if (command.toString().equals(shutdown)) {
                break;
            } else {
                LOG.log(Level.WARNING, "LifecycleManager.await: Invalid command '" + command.toString() + "' received");
            }
        }

        // Close the server socket and return
        try {
            serverSocket.close();
        } catch (IOException e) {
            ;
        }

    }

    private void anotify() {
        int retry = 3;
        int index = 1;
        while (index <= retry) {
            try {
                String hostAddress = InetAddress.getByName("localhost").getHostAddress();
                Socket socket = new Socket(hostAddress, port);
                OutputStream stream = socket.getOutputStream();
                for (int i = 0; i < shutdown.length(); i++) {
                    stream.write(shutdown.charAt(i));
                }
                stream.flush();
                stream.close();
                socket.close();
                break;
            } catch (ConnectException e) {
                if (index == retry) {
                    LOG.log(Level.SEVERE, "LifecycleManager.stop: ", e);
                    break;
                }

                LOG.log(Level.INFO, "LifecycleManager.stop retry {0}", index);
                try {
                    Thread.sleep(retry * 1000);
                } catch (InterruptedException e1) {
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "LifecycleManager.stop: ", e);
                break;
            }

            index++;
        }

    }

    public void start() {
        try {
            long startTime = System.currentTimeMillis();

            Method method = activator.getClass().getMethod("start", (Class[]) null);
            method.invoke(activator, (Object[]) null);

            long total = System.currentTimeMillis() - startTime;
            LOG.log(Level.INFO, "Activator startup in {0} ms", total / 1000000);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Activator start error", e);
        }

        if (await) {
            await();
            clear();
        }
    }

    public void stop() {
        anotify();
        clear();
    }

    private void clear() {
        try {
            Method method = activator.getClass().getMethod("stop", (Class[]) null);
            method.invoke(activator, (Object[]) null);

            LOG.log(Level.INFO, "Activator is stopped");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Activator start error", e);
        }
    }

    /**
     * the await to set
     * 
     * @param await
     * @see LifecycleManager#await
     */
    public void setAwait(boolean await) {
        this.await = await;
    }

}
