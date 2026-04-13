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

package org.apache.commons.logging;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ServiceLoader;

/**
 * Factory for creating {@link Log} instances, with discovery and
 * configuration features similar to that employed by standard Java APIs
 * such as JAXP.
 * <p>
 * <strong>IMPLEMENTATION NOTE</strong> - This implementation is
 * based on the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs) found in Apache Xerces.
 * </p>
 */
public abstract class LogFactory {
    // Implementation note re AccessController usage
    //
    // It is important to keep code invoked via an AccessController to small
    // auditable blocks. Such code must carefully evaluate all user input
    // (parameters, system properties, configuration file contents, etc). As an
    // example, a Log implementation should not write to its log file
    // with an AccessController anywhere in the call stack, otherwise an
    // insecure application could configure the log implementation to write
    // to a protected file using the privileges granted to JCL rather than
    // to the calling application.
    //
    // Under no circumstance should a non-private method return data that is
    // retrieved via an AccessController. That would allow an insecure application
    // to invoke that method and obtain data that it is not permitted to have.
    //
    // Invoking user-supplied code with an AccessController set is not a major
    // issue (for example, invoking the constructor of the class specified by
    // HASHTABLE_IMPLEMENTATION_PROPERTY). That class will be in a different
    // trust domain, and therefore must have permissions to do whatever it
    // is trying to do regardless of the permissions granted to JCL. There is
    // a slight issue in that untrusted code may point that environment variable
    // to another trusted library, in which case the code runs if both that
    // library and JCL have the necessary permissions even when the untrusted
    // caller does not. That's a pretty hard route to exploit though.

    /**
     * The name ({@code priority}) of the key in the configuration file used to
     * specify the priority of that particular configuration file. The associated value
     * is a floating-point number; higher values take priority over lower values.
     */
    /**
     * A reference to the class loader that loaded this class. This is the
     * same as LogFactory.class.getClassLoader(). However computing this
     * value isn't quite as simple as that, as we potentially need to use
     * AccessControllers etc. It's more efficient to compute it once and
     * cache it here.
     */


    /**
     * Maximum number of {@link ServiceLoader} errors to ignore, while
     * looking for an implementation.
     */
    private static final int MAX_BROKEN_SERVICES = 3;

    /**
     * The previously constructed {@code LogFactory} instances, keyed by
     * the {@code ClassLoader} with which it was created.
     */
    protected static Hashtable<ClassLoader, LogFactory> factories;

    /**
     * Previously constructed {@code LogFactory} instance as in the
     * {@code factories} map, but for the case where
     * {@code getClassLoader} returns {@code null}.
     * This can happen when:
     * <ul>
     * <li>using JDK1.1 and the calling code is loaded via the system
     *  class loader (very common)</li>
     * <li>using JDK1.2+ and the calling code is loaded via the boot
     *  class loader (only likely for embedded systems work).</li>
     * </ul>
     * Note that {@code factories} is a <em>Hashtable</em> (not a HashMap),
     * and hash tables don't allow null as a key.
     *
     * @deprecated Since 1.1.2
     */
    @Deprecated
    protected static volatile LogFactory nullClassLoaderFactory;


    /**
     * Remember this factory, so later calls to LogFactory.getCachedFactory
     * can return the previously created object (together with all its
     * cached Log objects).
     *
     * @param classLoader should be the current context class loader. Note that
     *  this can be null under some circumstances; this is ok.
     * @param factory should be the factory to cache. This should never be null.
     */


    /**
     * Creates a LogFactory object or a LogConfigurationException object.
     *
     * @param factoryClassName Factory class.
     * @param classLoader      used to load the specified factory class. This is expected to be either the TCCL or the class loader which loaded this class.
     *                         Note that the class loader which loaded this class might be "null" (for example, the boot loader) for embedded systems.
     * @return either a LogFactory object or a LogConfigurationException object.
     * @since 1.1
     */


    /**
     * Creates the hash table which will be used to store a map of
     * (context class loader -> logfactory-object). Version 1.2+ of Java
     * supports "weak references", allowing a custom Hashtable class
     * to be used which uses only weak references to its keys. Using weak
     * references can fix memory leaks on webapp unload in some cases (though
     * not all). Version 1.1 of Java does not support weak references, so we
     * must dynamically determine which we are using. And just for fun, this
     * code also supports the ability for a system property to specify an
     * arbitrary Hashtable implementation name.
     * <p>
     * Note that the correct way to ensure no memory leaks occur is to ensure
     * that LogFactory.release(contextClassLoader) is called whenever a
     * webapp is undeployed.
     * </p>
     */


    /**
     * Gets the thread context class loader if available; otherwise return null.
     * <p>
     * Most/all code should call getContextClassLoaderInternal rather than
     * calling this method directly.
     * </p>
     * <p>
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * </p>
     * <p>
     * Note that no internal logging is done within this method because
     * this method is called every time LogFactory.getLogger() is called,
     * and we don't want too much output generated here.
     * </p>
     *
     *
     *  cannot be identified.
     * @return the thread's context class loader or {@code null} if the Java security
     *  policy forbids access to the context class loader from one of the classes
     *  in the current call stack.
     * @since 1.1
     */


    /**
     * Gets a cached log factory (keyed by contextClassLoader)
     *
     * @param contextClassLoader is the context class loader associated
     * with the current thread. This allows separate LogFactory objects
     * per component within a container, provided each component has
     * a distinct context class loader set. This parameter may be null
     * in JDK1.1, and in embedded systems where jcl-using code is
     * placed in the bootclasspath.
     *
     * @return the factory associated with the specified class loader if
     *  one has previously been created, or null if this is the first time
     *  we have seen this particular class loader.
     */


    /**
     * Safely get access to the class loader for the specified class.
     * <p>
     * Theoretically, calling getClassLoader can throw a security exception,
     * and so should be done under an AccessController in order to provide
     * maximum flexibility. However in practice people don't appear to use
     * security policies that forbid getClassLoader calls. So for the moment
     * all code is written to call this method rather than Class.getClassLoader,
     * so that we could put AccessController stuff in this method without any
     * disruption later if we need to.
     * </p>
     * <p>
     * Even when using an AccessController, however, this method can still
     * throw SecurityException. Commons Logging basically relies on the
     * ability to access class loaders. A policy that forbids all
     * class loader access will also prevent commons-logging from working:
     * currently this method will throw an exception preventing the entire app
     * from starting up. Maybe it would be good to detect this situation and
     * just disable all commons-logging? Not high priority though - as stated
     * above, security policies that prevent class loader access aren't common.
     * </p>
     * <p>
     * Note that returning an object fetched via an AccessController would
     * technically be a security flaw anyway; untrusted code that has access
     * to a trusted JCL library could use it to fetch the class loader for
     * a class even when forbidden to do so directly.
     * </p>
     *
     * @param clazz Class.
     * @return a ClassLoader.
     * @since 1.1
     */


    /**
     * Gets a user-provided configuration file.
     * <p>
     * The classpath of the specified classLoader (usually the context class loader)
     * is searched for properties files of the specified name. If none is found,
     * null is returned. If more than one is found, then the file with the greatest
     * value for its PRIORITY property is returned. If multiple files have the
     * same PRIORITY value then the first in the classpath is returned.
     * </p>
     * <p>
     * This differs from the 1.0.x releases; those always use the first one found.
     * However as the priority is a new field, this change is backwards compatible.
     * </p>
     * <p>
     * The purpose of the priority field is to allow a webserver administrator to
     * override logging settings in all webapps by placing a commons-logging.properties
     * file in a shared classpath location with a priority > 0; this overrides any
     * commons-logging.properties files without priorities which are in the
     * webapps. Webapps can also use explicit priorities to override a configuration
     * file in the shared classpath if needed.
     * </p>
     */


    /**
     * Gets the current context class loader.
     * <p>
     * In versions prior to 1.1, this method did not use an AccessController.
     * In version 1.1, an AccessController wrapper was incorrectly added to
     * this method, causing a minor security flaw.
     * </p>
     * <p>
     * In version 1.1.1 this change was reverted; this method no longer uses
     * an AccessController. User code wishing to obtain the context class loader
     * must invoke this method via AccessController.doPrivileged if it needs
     * support for that.
     * </p>
     *
     * @return the context class loader associated with the current thread,
     *  or null if security doesn't allow it.
     *
     *  attempting to get the context class loader.
     */

    /**
     * Given a file name, return an enumeration of URLs pointing to
     * all the occurrences of that file name in the classpath.
     * <p>
     * This is just like ClassLoader.getResources except that the
     * operation is done under an AccessController so that this method will
     * succeed when this jarfile is privileged but the caller is not.
     * This method must therefore remain private to avoid security issues.
     * </p>
     * <p>
     * If no instances are found, an Enumeration is returned whose
     * hasMoreElements method returns false (ie an "empty" enumeration).
     * If resources could not be listed for some reason, null is returned.
     * </p>
     */

    /**
     * Checks whether the supplied Throwable is one that needs to be
     * re-thrown and ignores all others.
     *
     * The following errors are re-thrown:
     * <ul>
     *   <li>ThreadDeath</li>
     *   <li>VirtualMachineError</li>
     * </ul>
     *
     * @param t the Throwable to check
     */
    protected static void handleThrowable(final Throwable t) {

        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        // All other instances of Throwable will be silently ignored
    }

    /**
     * Determines whether the given class actually implements {@code LogFactory}.
     * Diagnostic information is also logged.
     * <p>
     * <strong>Usage:</strong> to diagnose whether a class loader conflict is the cause
     * of incompatibility. The test used is whether the class is assignable from
     * the {@code LogFactory} class loaded by the class's class loader.
     *
     * @param logFactoryClass {@code Class} which may implement {@code LogFactory}
     * @return true if the {@code logFactoryClass} does extend
     * {@code LogFactory} when that class is loaded via the same
     * class loader that loaded the {@code logFactoryClass}.
     * </p>
     */


    /**
     * Tests whether the user wants internal diagnostic output. If so,
     * returns an appropriate writer object. Users can enable diagnostic
     *
     * a file name, or the special values STDOUT or STDERR.
     */




    /**
     * Tests whether the user enabled internal logging.
     * <p>
     * By the way, sorry for the incorrect grammar, but calling this method
     * areDiagnosticsEnabled just isn't Java beans style.
     * </p>
     *
     * @return true if calls to logDiagnostic will have any effect.
     * @since 1.1
     */


    /**
     * Generates useful diagnostics regarding the class loader tree for
     * the specified class.
     * <p>
     * As an example, if the specified class was loaded via a webapp's
     * class loader, then you may get the following output:
     * </p>
     * <pre>
     * Class com.acme.Foo was loaded via class loader 11111
     * ClassLoader tree: 11111 -> 22222 (SYSTEM) -> 33333 -> BOOT
     * </pre>
     * <p>
     * This method returns immediately if isDiagnosticsEnabled()
     * returns false.
     * </p>
     *
     * @param clazz is the class whose class loader + tree are to be
     * output.
     */


    /**
     * Writes the specified message to the internal logging destination.
     * <p>
     * Note that this method is private; concrete subclasses of this class
     * should not call it because the diagnosticPrefix string this
     * method puts in front of all its messages is LogFactory@....,
     * while subclasses should put SomeSubClass@...
     * </p>
     * <p>
     * Subclasses should instead compute their own prefix, then call
     * logRawDiagnostic. Note that calling isDiagnosticsEnabled is
     * fine for subclasses.
     * </p>
     * <p>
     * Note that it is safe to call this method before initDiagnostics
     * is called; any output will just be ignored (as isDiagnosticsEnabled
     * will return false).
     * </p>
     *
     * @param msg is the diagnostic message to be output.
     */

}
