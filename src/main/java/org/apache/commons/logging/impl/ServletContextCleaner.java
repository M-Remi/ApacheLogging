/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.LogFactory;

/**
 * This class is capable of receiving notifications about the undeployment of
 * a webapp, and responds by ensuring that commons-logging releases all
 * memory associated with the undeployed webapp.
 * <p>
 * In general, the WeakHashtable support added in commons-logging release 1.1
 * ensures that logging classes do not hold references that prevent an
 * undeployed webapp's memory from being garbage-collected even when multiple
 * copies of commons-logging are deployed via multiple class loaders (a
 * situation that earlier versions had problems with). However there are
 * some rare cases where the WeakHashtable approach does not work; in these
 * situations specifying this class as a listener for the web application will
 * ensure that all references held by commons-logging are fully released.
 * <p>
 * To use this class, configure the webapp deployment descriptor to call
 * this class on webapp undeploy; the contextDestroyed method will tell
 * every accessible LogFactory class that the entry in its map for the
 * current webapp's context class loader should be cleared.
 *
 * @since 1.1
 */
public class ServletContextCleaner implements ServletContextListener {

    private static final Class<?>[] RELEASE_SIGNATURE = { ClassLoader.class };

    /**
     * Constructs a new instance.
     */
    public ServletContextCleaner() {
        // empty
    }

    /**
     * Invoked when a webapp is undeployed, this tells the LogFactory
     * class to release any logging information related to the current
     * contextClassloader.
     */
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

    }

    /**
     * Invoked when a webapp is deployed. Nothing needs to be done here.
     */
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        // do nothing
    }
}
