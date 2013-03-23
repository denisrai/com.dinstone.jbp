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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.cache.FactoryFinder.ConfigurationException;

/**
 * cache service create factory
 * 
 * @author guojf
 */
public abstract class CacheServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CacheServiceFactory.class);

    private static final String FACTORY_ID = "com.dinstone.cache.CacheServiceFactory";

    private static final String DEFAULT_FACTORY = "com.dinstone.cache.NoneCacheServiceFactory";

    /** 服务超时时间,默认5秒 */
    protected long timeout = 5000;

    /**
     * 创建新的CacheService实例
     * 
     * @return
     */
    public abstract CacheService createCacheService();

    /**
     * 获得一个新的CacheServiceFactory实例。该静态方法通过如下方法创建实例：<br/>
     * 首先使用Service API在
     * jar包中查找文件META-INF/services/com.dinstone.cache.CacheServiceFactory,
     * 探测CacheServiceFactory工厂类并实例化
     * ,如果失败则使用默认的CacheServiceFactory：NoneCacheServiceFactory.
     * 
     * @return
     */
    public static CacheServiceFactory getInstance() {
        return getInstance(true);
    }

    /**
     * 获得一个新的CacheServiceFactory实例。该静态方法通过如下方法创建实例：<br/>
     * 首先使用Service API在
     * jar包中查找文件META-INF/services/com.dinstone.cache.CacheServiceFactory,
     * 探测CacheServiceFactory工厂类并实例化
     * ,如果失败则根据defaultFactory是否使用默认的CacheServiceFactory：NoneCacheServiceFactory.
     * 
     * @param defaultFactory
     *        为真则使用默认工厂
     * @return
     */
    public static CacheServiceFactory getInstance(boolean defaultFactory) {
        try {
            return (CacheServiceFactory) FactoryFinder.find(FACTORY_ID, defaultFactory ? DEFAULT_FACTORY : null);
        } catch (ConfigurationException e) {
            LOG.error("create CacheServiceFactory error:", e);
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 设置缓存服务的超时时间
     * 
     * @param timeout
     *        单位为毫秒
     */
    public void setTimeout(long timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }
}
