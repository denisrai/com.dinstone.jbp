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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Locator;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.FailureMode;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationFactory;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.ops.OperationQueueFactory;
import net.spy.memcached.transcoders.SerializingTranscoder;
import net.spy.memcached.transcoders.Transcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.cache.CacheService;
import com.dinstone.cache.CacheServiceFactory;
import com.dinstone.cache.ClassLoaderSupport;

public class MemcachedServiceFactory extends CacheServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MemcachedServiceFactory.class);

    private ConnectionFactoryBuilder connectionFactoryBuilder;

    private String servers;

    private AliveKeeper keeper;

    public MemcachedServiceFactory() {
        this(loadConfig());
    }

    public MemcachedServiceFactory(Properties config) {
        connectionFactoryBuilder = new ConnectionFactoryBuilder();

        if (config == null) {
            config = new Properties();
        }
        init(config);
    }

    @Override
    public CacheService createCacheService() {
        ConnectionFactory conFactory = connectionFactoryBuilder.build();
        List<InetSocketAddress> addresses = AddrUtil.getAddresses(servers);
        try {
            MemcachedClient client = new MemcachedClient(conFactory, addresses);
            keeper.addMemcachedClient(client);
            long optTimeout = conFactory.getOperationTimeout();
            optTimeout = Math.min(optTimeout, timeout);
            LOG.debug("cache service timeout is {}ms", optTimeout);
            return new MemcachedService(client, optTimeout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init(Properties config) {
        // keepAliveInterval
        String interval = config.getProperty("keepAliveInterval");
        try {
            keeper = new AliveKeeper(Integer.parseInt(interval));
        } catch (Exception e) {
            keeper = new AliveKeeper();
        }
        keeper.start();

        // servers
        setServers(config.getProperty("servers"));

        // protocol
        String protocol = config.getProperty("protocol");
        try {
            setProtocol(Protocol.valueOf(protocol));
        } catch (Exception e) {
        }

        // transcoder
        SerializingTranscoder transcoder = new SerializingTranscoder();
        String ct = config.getProperty("compressionThreshold");
        try {
            transcoder.setCompressionThreshold(Integer.parseInt(ct));
        } catch (Exception e) {
        }
        setTranscoder(transcoder);

        // opTimeout
        String opt = config.getProperty("opTimeout");
        try {
            setOpTimeout(Long.parseLong(opt));
        } catch (Exception e) {
        }

        // timeoutExceptionThreshold
        String tet = config.getProperty("timeoutExceptionThreshold");
        try {
            setTimeoutExceptionThreshold(Integer.parseInt(tet));
        } catch (Exception e) {
        }

        // hashAlg
        String hashAlg = config.getProperty("hashAlg");
        try {
            setHashAlg(DefaultHashAlgorithm.valueOf(hashAlg));
        } catch (Exception e) {
        }

        // locatorType
        String locatorType = config.getProperty("locatorType");
        try {
            setLocatorType(Locator.valueOf(locatorType));
        } catch (Exception e) {
        }

        // failureMode
        String failureMode = config.getProperty("failureMode");
        try {
            setFailureMode(FailureMode.valueOf(failureMode));
        } catch (Exception e) {
        }

        // useNagleAlgorithm
        String useNagleAlgorithm = config.getProperty("useNagleAlgorithm");
        try {
            setUseNagleAlgorithm(Boolean.parseBoolean(useNagleAlgorithm));
        } catch (Exception e) {
        }

        setDaemon(true);
    }

    private static Properties loadConfig() {
        Properties config = new Properties();
        ClassLoader cl = ClassLoaderSupport.getContextClassLoader();
        String resource = "cache-service.properties";

        LOG.info("loading config file from classpath : {}", resource);
        InputStream rs = ClassLoaderSupport.getResourceAsStream(cl, resource);
        try {
            if (rs != null) {
                LOG.info("loading config from properties file");
                config.load(rs);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    protected void setServers(final String newServers) {
        if (newServers == null || newServers.length() == 0) {
            throw new IllegalArgumentException("servers can't be null or empty");
        }
        this.servers = newServers;
    }

    protected void setAuthDescriptor(final AuthDescriptor to) {
        connectionFactoryBuilder.setAuthDescriptor(to);
    }

    protected void setDaemon(final boolean d) {
        connectionFactoryBuilder.setDaemon(d);
    }

    protected void setFailureMode(final FailureMode fm) {
        connectionFactoryBuilder.setFailureMode(fm);
    }

    protected void setHashAlg(final HashAlgorithm to) {
        connectionFactoryBuilder.setHashAlg(to);
    }

    protected void setInitialObservers(final Collection<ConnectionObserver> obs) {
        connectionFactoryBuilder.setInitialObservers(obs);
    }

    protected void setLocatorType(final Locator l) {
        connectionFactoryBuilder.setLocatorType(l);
    }

    protected void setMaxReconnectDelay(final long to) {
        connectionFactoryBuilder.setMaxReconnectDelay(to);
    }

    protected void setOpFact(final OperationFactory f) {
        connectionFactoryBuilder.setOpFact(f);
    }

    protected void setOpQueueFactory(final OperationQueueFactory q) {
        connectionFactoryBuilder.setOpQueueFactory(q);
    }

    protected void setOpQueueMaxBlockTime(final long t) {
        connectionFactoryBuilder.setOpQueueMaxBlockTime(t);
    }

    protected void setOpTimeout(final long t) {
        connectionFactoryBuilder.setOpTimeout(t);
    }

    protected void setProtocol(final Protocol prot) {
        connectionFactoryBuilder.setProtocol(prot);
    }

    protected void setReadBufferSize(final int to) {
        connectionFactoryBuilder.setReadBufferSize(to);
    }

    protected void setReadOpQueueFactory(final OperationQueueFactory q) {
        connectionFactoryBuilder.setReadOpQueueFactory(q);
    }

    protected void setShouldOptimize(final boolean o) {
        connectionFactoryBuilder.setShouldOptimize(o);
    }

    protected void setTimeoutExceptionThreshold(final int to) {
        connectionFactoryBuilder.setTimeoutExceptionThreshold(to);
    }

    protected void setTranscoder(final Transcoder<Object> t) {
        connectionFactoryBuilder.setTranscoder(t);
    }

    protected void setUseNagleAlgorithm(final boolean to) {
        connectionFactoryBuilder.setUseNagleAlgorithm(to);
    }

    protected void setWriteOpQueueFactory(final OperationQueueFactory q) {
        connectionFactoryBuilder.setWriteOpQueueFactory(q);
    }

}
