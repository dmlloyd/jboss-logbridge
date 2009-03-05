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

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public final class BridgeHierarchy extends Hierarchy {

    private final LogBridgeHandler logBridgeHandler;
    private final Factory factory = new Factory();

    public BridgeHierarchy(Logger logger, final LogBridgeHandler handler) {
        super(logger);
        logBridgeHandler = handler;
    }

    public Logger getRootLogger() {
        return super.getRootLogger();
    }

    public Logger getLogger(final String s, final LoggerFactory factory) {
        return super.getLogger(s, factory);
    }

    public Logger getLogger(final String s) {
        return super.getLogger(s, factory);
    }

    LogBridgeHandler getLogBridgeHandler() {
        return logBridgeHandler;
    }

    private class Factory implements LoggerFactory {

        public Logger makeNewLoggerInstance(final String s) {
            return new BridgeLogger(s);
        }
    }
}
