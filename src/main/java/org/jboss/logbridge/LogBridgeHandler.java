/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Filter;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 */
public final class LogBridgeHandler extends Handler {

    private final LevelMapper levelMapper = new LevelMapper();
    @SuppressWarnings({ "NonConstantLogger" })
    private final java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");

    public void setFilter(final Filter newFilter) throws SecurityException {
    }

    public void setLevel(final Level newLevel) throws SecurityException {
    }

    public boolean isLoggable(final LogRecord record) {
        return true;
    }

    public void publish(final LogRecord record) {
        Logger targetLogger = Logger.getLogger(record.getLoggerName());
        final Priority targetLevel = levelMapper.getTargetLevelForSourceLevel(record.getLevel());
        final String text = record.getMessage();
        targetLogger.log(record.getLoggerName(), targetLevel, text, record.getThrown());
    }

    public void flush() {
    }

    public void close() throws SecurityException {
    }

    public void start() {
        rootLogger.addHandler(this);
    }

    public void stop() {
        rootLogger.removeHandler(this);
    }
}
