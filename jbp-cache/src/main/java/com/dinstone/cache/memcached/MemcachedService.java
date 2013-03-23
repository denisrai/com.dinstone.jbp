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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.cache.AbstractCacheService;
import com.dinstone.cache.CacheElement;

/**
 * 基于 Memcached 的分布式缓存服务实现
 * 
 * @author guojf
 */
public class MemcachedService extends AbstractCacheService {

    private static final Logger LOG = LoggerFactory.getLogger(MemcachedService.class);

    private MemcachedClient memClient;

    public MemcachedService(MemcachedClient memClient, long timeout) {
        super(timeout);
        LOG.debug("Memcache service timeout is {}", timeout);

        if (memClient == null) {
            throw new IllegalArgumentException("null memClient");
        }
        this.memClient = memClient;

    }

    public Object get(String key) {
        Object obj = null;
        Future<Object> f = memClient.asyncGet(key);
        try {
            obj = f.get(timeout, timeUnit);
        } catch (Exception e) {
            LOG.debug("get error", e);
            f.cancel(false);
        }
        return obj;
    }

    public boolean put(CacheElement element) {
        Future<Boolean> f = memClient.add(element.getKey(), element.getExpire(), element.getValue());
        return getBooleanValue(f);
    }

    public boolean remove(String key) {
        Future<Boolean> f = memClient.delete(key);
        return getBooleanValue(f);
    }

    public boolean replace(CacheElement element) {
        Future<Boolean> f = memClient.replace(element.getKey(), element.getExpire(), element.getValue());
        return getBooleanValue(f);
    }

    private boolean getBooleanValue(Future<Boolean> f) {
        try {
            return f.get(timeout, timeUnit);
        } catch (Exception e) {
            LOG.debug("getBooleanValue error", e);
            f.cancel(false);
        }
        return false;
    }

    public Map<String, Object> getMulti(String[] keys) {
        Map<String, Object> map = null;
        Future<Map<String, Object>> f = memClient.asyncGetBulk(keys);
        try {
            map = f.get(timeout, timeUnit);
        } catch (Exception e) {
            LOG.debug("getMulti error", e);
            f.cancel(false);
        }
        return map;
    }

    public Map<String, Object> getMulti(Collection<String> keys) {
        Map<String, Object> map = null;
        Future<Map<String, Object>> f = memClient.asyncGetBulk(keys);
        try {
            map = f.get(timeout, timeUnit);
        } catch (Exception e) {
            LOG.debug("getMulti error", e);
            f.cancel(false);
        }
        return map;
    }

}
