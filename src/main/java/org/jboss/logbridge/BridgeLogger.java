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

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;

public class BridgeLogger extends Logger {

    protected BridgeLogger(String s) {
        super(s);
    }

    @SuppressWarnings({ "NonConstantLogger" })
    private java.util.logging.Logger jdkLogger;

    public void setLevel(final Level level) {
        super.setLevel(level);
        syncLevels();
    }

    @SuppressWarnings({ "deprecation" })
    @Deprecated
    public void setPriority(final Priority priority) {
        super.setPriority(priority);
        syncLevels();
    }

    private void syncLevels() {
        synchronized (this) {
            if (jdkLogger == null) {
                jdkLogger = java.util.logging.Logger.getLogger(name);
            }
            final BridgeHierarchy bridgeHierarchy = (BridgeHierarchy) repository;
            if (bridgeHierarchy == null) {
                // not fully initialized yet; bail out
                return;
            }
            final LevelMapper mapper = bridgeHierarchy.getLogBridgeHandler().getLevelMapper();
            final Level ourLevel = getLevel();
            if (ourLevel == null) {
                jdkLogger.setLevel(null);
            } else {
                jdkLogger.setLevel(mapper.getSourceLevelForTargetLevel(ourLevel));
            }
        }
    }
}
