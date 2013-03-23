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

public class CacheElement {

    /**
     * 默认不过期
     */
    public static int Default_EXPIRE_TIME = 0;

    private String key;

    private Object value;

    private int expire;

    public CacheElement(String key, Object value, int expire) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }

        this.key = key;
        this.value = value;
        this.expire = expire;
    }

    public CacheElement(String key, Object value) {
        this(key, value, Default_EXPIRE_TIME);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public int getExpire() {
        return expire;
    }

}
