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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FactoryFinder {

    private static final Logger LOG = LoggerFactory.getLogger(FactoryFinder.class);

    /**
     * Attempt to load a class using the class loader supplied. If that fails
     * and fall back is enabled, the current (i.e. bootstrap) class loader is
     * tried. If the class loader supplied is <code>null</code>, first try using
     * the context class loader followed by the current (i.e. bootstrap) class
     * loader.
     */
    static private Class<?> getProviderClass(String className, ClassLoader cl, boolean doFallback)
            throws ClassNotFoundException {
        try {
            if (cl == null) {
                cl = ClassLoaderSupport.getContextClassLoader();
                if (cl == null) {
                    throw new ClassNotFoundException();
                } else {
                    return cl.loadClass(className);
                }
            } else {
                return cl.loadClass(className);
            }
        } catch (ClassNotFoundException e1) {
            if (doFallback) {
                // Use current class loader - should always be bootstrap CL
                return Class.forName(className, true, FactoryFinder.class.getClassLoader());
            } else {
                throw e1;
            }
        }
    }

    /**
     * Create an instance of a class. Delegates to method
     * <code>getProviderClass()</code> in order to load the class.
     * 
     * @param className
     *        Name of the concrete class corresponding to the service provider
     * @param cl
     *        ClassLoader to use to load the class, null means to use the
     *        bootstrap ClassLoader
     * @param doFallback
     *        True if the current ClassLoader should be tried as a fallback if
     *        the class is not found using cl
     */
    static Object newInstance(String className, ClassLoader cl, boolean doFallback) throws ConfigurationException {
        try {
            Class<?> providerClass = getProviderClass(className, cl, doFallback);
            Object instance = providerClass.newInstance();

            LOG.debug("created new instance of " + providerClass + " using ClassLoader: " + cl);
            return instance;
        } catch (ClassNotFoundException x) {
            throw new ConfigurationException("Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new ConfigurationException("Provider " + className + " could not be instantiated", x);
        }
    }

    /**
     * Finds the implementation Class object in the specified order. Main entry
     * point.
     * 
     * @return Class object of factory, never null
     * @param factoryId
     *        Name of the factory to find, same as a property name
     * @param fallbackClassName
     *        Implementation class name, if nothing else is found. Use null to
     *        mean no fallback. Package private so this code can be shared.
     */
    static Object find(String factoryId, String fallbackClassName) throws ConfigurationException {
        LOG.debug("find factoryId [{}]", factoryId);

        // Try Jar Service Provider Mechanism
        Object provider = findJarServiceProvider(factoryId);
        if (provider != null) {
            return provider;
        }

        // check fallback class
        if (fallbackClassName == null) {
            throw new ConfigurationException("Provider for " + factoryId + " cannot be found");
        }

        LOG.debug("loaded from fallback value: {}", fallbackClassName);
        return newInstance(fallbackClassName, null, true);
    }

    /*
     * Try to find provider using Jar Service Provider Mechanism
     * 
     * @return instance of provider class if found or null
     */
    private static Object findJarServiceProvider(String factoryId) throws ConfigurationException {
        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;

        // First try the Context ClassLoader
        ClassLoader cl = ClassLoaderSupport.getContextClassLoader();
        if (cl != null) {
            is = ClassLoaderSupport.getResourceAsStream(cl, serviceId);

            // If no provider found then try the current ClassLoader
            if (is == null) {
                cl = FactoryFinder.class.getClassLoader();
                is = ClassLoaderSupport.getResourceAsStream(cl, serviceId);
            }
        } else {
            // No Context ClassLoader, try the current ClassLoader
            cl = FactoryFinder.class.getClassLoader();
            is = ClassLoaderSupport.getResourceAsStream(cl, serviceId);
        }

        if (is == null) {
            // No provider found
            return null;
        }

        LOG.debug("found jar resource [{}], using ClassLoader [{}]", serviceId, cl);

        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is));
        }

        String factoryClassName = null;
        try {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            factoryClassName = rd.readLine();
            rd.close();
        } catch (IOException x) {
            // No provider found
            return null;
        }

        if (factoryClassName != null && !"".equals(factoryClassName)) {
            LOG.debug("found provider [{}] in resource", factoryClassName);

            // Note: here we do not want to fall back to the current
            // ClassLoader because we want to avoid the case where the
            // resource file was found using one ClassLoader and the
            // provider class was instantiated using a different one.
            return newInstance(factoryClassName, cl, false);
        }

        // No provider found
        return null;
    }

    static class ConfigurationException extends Exception {

        /**  */
        private static final long serialVersionUID = 1L;

        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ConfigurationException(String message) {
            super(message);
        }

    }

}