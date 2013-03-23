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

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

public class MemcachedServiceFactoryBean implements FactoryBean {

    private Properties configuration;

    public MemcachedServiceFactoryBean(Properties configuration) {
        this.configuration = configuration;
    }

    public Object getObject() throws Exception {
        MemcachedServiceFactory factory = new MemcachedServiceFactory(configuration);
        return factory.createCacheService();
    }

    public Class<?> getObjectType() {
        return MemcachedService.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
