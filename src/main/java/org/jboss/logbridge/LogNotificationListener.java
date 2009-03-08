/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.logbridge;

import javax.management.NotificationListener;
import javax.management.Notification;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MalformedObjectNameException;

import org.apache.log4j.Logger;

public final class LogNotificationListener implements NotificationListener {
    private LogBridgeHandler logBridgeHandler;
    private MBeanServer mBeanServer;
    private ObjectName loggingMBeanName;

    private static final Logger log = Logger.getLogger(LogNotificationListener.class);

    public LogBridgeHandler getLogBridgeHandler() {
        return logBridgeHandler;
    }

    public void setLogBridgeHandler(final LogBridgeHandler logBridgeHandler) {
        this.logBridgeHandler = logBridgeHandler;
    }

    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    public void setMBeanServer(final MBeanServer mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    public String getLoggingMBeanName() {
        return loggingMBeanName.toString();
    }

    public void setLoggingMBeanName(final String loggingMBeanName) throws MalformedObjectNameException {
        this.loggingMBeanName = new ObjectName(loggingMBeanName);
    }

    public void handleNotification(final Notification notification, final Object handback) {
        logBridgeHandler.updateLoggers();
    }

    public void start() throws InstanceNotFoundException {
        log.info("Adding notification listener for logging mbean \"" + loggingMBeanName + "\" to server " + mBeanServer);
        mBeanServer.addNotificationListener(loggingMBeanName, this, null, null);
        logBridgeHandler.updateLoggers();
    }

    public void stop() throws ListenerNotFoundException, InstanceNotFoundException {
        mBeanServer.removeNotificationListener(loggingMBeanName, this);
    }
}
