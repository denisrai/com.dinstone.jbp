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

import java.util.Collection;
import java.util.Map;

public class NoneCacheService extends AbstractCacheService {

    public Object get(String key) {
        return null;
    }

    public Map<String, Object> getMulti(String[] keys) {
        return null;
    }

    public Map<String, Object> getMulti(Collection<String> keys) {
        return null;
    }

    public boolean put(CacheElement element) {
        return false;
    }

    public boolean remove(String key) {
        return false;
    }

    public boolean replace(CacheElement element) {
        return false;
    }

}