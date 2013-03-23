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
import org.junit.Test;

import com.dinstone.cache.CacheElement;
import com.dinstone.cache.CacheService;
import com.dinstone.cache.CacheServiceFactory;

public class CacheServiceFactoryTest {

    @Test
    public void testGetInstance01() {
        CacheServiceFactory csf = CacheServiceFactory.getInstance();
        Assert.assertEquals(csf.getClass(), MemcachedServiceFactory.class);
    }

    @Test
    public void testGetInstance02() throws Exception {
        CacheServiceFactory csf = CacheServiceFactory.getInstance();
        Assert.assertEquals(csf.getClass(), MemcachedServiceFactory.class);

        CacheService cs = csf.createCacheService();
        String key = "keys";
        String value = "ddddd";
        cs.put(new CacheElement(key, value));
        Object v = cs.get(key);
        Assert.assertEquals(value, v);
    }

}
