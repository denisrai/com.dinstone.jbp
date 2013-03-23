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

package com.dinstone.cache;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dinstone.cache.memcached.MemcachedService;
import com.dinstone.cache.memcached.MemcachedServiceFactory;

public class CacheServiceFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetInstance() {
        CacheServiceFactory csf = CacheServiceFactory.getInstance();
        Assert.assertEquals(csf.getClass(), MemcachedServiceFactory.class);
    }

    @Test
    public void testCreateCacheService() {
        CacheServiceFactory csf = CacheServiceFactory.getInstance();
        Assert.assertEquals(csf.getClass(), MemcachedServiceFactory.class);

        CacheService cs = csf.createCacheService();
        Assert.assertTrue(cs instanceof MemcachedService);
    }

    @Test
    public void testGetInstanceBoolean() {
        try {
            CacheServiceFactory.getInstance(false);
        } catch (RuntimeException e) {
            Assert.assertTrue(e instanceof IllegalStateException);
        }
    }

}
