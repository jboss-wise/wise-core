/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.core.test;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Wise test base class. Subclass can use the methods in this class to deploy and undeploy a web service war in JBossAS
 *
 * @author ema@redhat.com
 * @author alessio.soldano@jboss.com
 *
 */
public class WiseTest {

    private static final String TEST_WS_ARCHIVE_DIR = "test-ws-archive";

    private static final String SYSPROP_JBOSS_BIND_ADDRESS = "jboss.bind.address";

    private static final String SYSPROP_JBOSS_HTTP_PORT = "jboss.http.port";

    private static final String SYSPROP_TEST_RESOURCES_DIRECTORY = "test.resources.directory";

    private static final String testResourcesDir = System.getProperty(SYSPROP_TEST_RESOURCES_DIRECTORY);

    /**
     * Get the jboss webservice server side hostname and port
     *
     * @return http://server-hostname:port
     */

    public static String getServerHostAndPort() {
        final String host = System.getProperty(SYSPROP_JBOSS_BIND_ADDRESS, "localhost");
        final String port = System.getProperty(SYSPROP_JBOSS_HTTP_PORT, "8080");
        final StringBuilder sb = new StringBuilder("http://");
        sb.append(toIPv6URLFormat(host)).append(":").append(port);
        return sb.toString();
    }

    private static String toIPv6URLFormat(final String host) {
        try {
            if (host.startsWith(":")) {
                throw new IllegalArgumentException(
                        "JBossWS test suite requires IPv6 addresses to be wrapped with [] brackets. Expected format is: ["
                                + host + "]");
            }
            if (host.startsWith("[")) {
                if (System.getProperty("java.net.preferIPv4Stack") == null) {
                    throw new IllegalStateException(
                            "always provide java.net.preferIPv4Stack JVM property when using IPv6 address format");
                }
                if (System.getProperty("java.net.preferIPv6Addresses") == null) {
                    throw new IllegalStateException(
                            "always provide java.net.preferIPv6Addresses JVM property when using IPv6 address format");
                }
            }
            final boolean isIPv6Address = InetAddress.getByName(host) instanceof Inet6Address;
            final boolean isIPv6Formatted = isIPv6Address && host.startsWith("[");
            return isIPv6Address && !isIPv6Formatted ? "[" + host + "]" : host;
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTestResourcesDir() {
        return testResourcesDir;
    }

}
