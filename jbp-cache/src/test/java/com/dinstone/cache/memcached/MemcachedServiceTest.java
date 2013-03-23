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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dinstone.cache.CacheElement;
import com.dinstone.cache.CacheService;

public class MemcachedServiceTest {

    private static CacheService cacheService;

    @BeforeClass
    public static void setUp() {
        MemcachedServiceFactory factory = new MemcachedServiceFactory();
        cacheService = factory.createCacheService();

        CacheElement.Default_EXPIRE_TIME = 5;
    }

    @Test
    public void testPut() {
        Object v1 = cacheService.get("key1");
        Assert.assertNull(v1);

        boolean b = cacheService.put(new CacheElement("key1", "value1"));
        Assert.assertTrue(b);

        b = cacheService.put(new CacheElement("key2", "value2", 1));
        Assert.assertTrue(b);

        b = cacheService.put(new CacheElement("key3", "value3"));
        Assert.assertTrue(b);
    }

    @Test
    public void testGet() {
        Object v1 = cacheService.get("key1");
        Assert.assertNotNull(v1);
        Assert.assertEquals("value1", v1);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        Object v2 = cacheService.get("key2");
        Assert.assertNull(v2);

        Object v3 = cacheService.get("key3");
        Assert.assertNotNull(v3);
        Assert.assertEquals("value3", v3);
    }

    @Test
    public void testRemove() {
        boolean b = cacheService.remove("key3");
        Assert.assertTrue(b);
    }

    @Test
    public void testReplace() {
        boolean b = cacheService.replace(new CacheElement("key3", "dddd"));
        Assert.assertFalse(b);

        b = cacheService.replace(new CacheElement("key1", "v1"));
        Assert.assertTrue(b);

        Object v1 = cacheService.get("key1");
        Assert.assertNotNull(v1);
        Assert.assertEquals("v1", v1);
    }

}
