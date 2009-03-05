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

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Level;

import org.jboss.logging.log4j.JDKLevel;

/**
 *
 */
public final class LevelMapper {
    private final SortedMap<TargetLevelKey, SourceLevelKey> targetToSource = new TreeMap<TargetLevelKey, SourceLevelKey>();
    private final SortedMap<SourceLevelKey, TargetLevelKey> sourceToTarget = new TreeMap<SourceLevelKey, TargetLevelKey>();

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LevelMapper.class);

    public LevelMapper() {
        // Populate mapping with standard log levels
        registerMapping(JDKLevel.SEVERE, new SourceLevelKey(java.util.logging.Level.SEVERE));
        registerMapping(JDKLevel.WARNING, new SourceLevelKey(java.util.logging.Level.WARNING));
        registerMapping(JDKLevel.INFO, new SourceLevelKey(java.util.logging.Level.INFO));
        registerMapping(JDKLevel.CONFIG, new SourceLevelKey(java.util.logging.Level.CONFIG));
        registerMapping(JDKLevel.FINE, new SourceLevelKey(java.util.logging.Level.FINE));
        registerMapping(JDKLevel.FINER, new SourceLevelKey(java.util.logging.Level.FINER));
        registerMapping(JDKLevel.FINEST, new SourceLevelKey(java.util.logging.Level.FINEST));

        registerMapping(Level.FATAL, new SourceLevelKey(org.jboss.logmanager.Level.FATAL));
        registerMapping(Level.ERROR, new SourceLevelKey(org.jboss.logmanager.Level.ERROR));
        registerMapping(Level.WARN, new SourceLevelKey(org.jboss.logmanager.Level.WARN));
        registerMapping(Level.INFO, new SourceLevelKey(org.jboss.logmanager.Level.INFO));
        registerMapping(Level.DEBUG, new SourceLevelKey(org.jboss.logmanager.Level.DEBUG));
        registerMapping(Level.TRACE, new SourceLevelKey(org.jboss.logmanager.Level.TRACE));
    }

    public synchronized Level registerMapping(final int targetLevelValue, final int targetSyslogLevel, final SourceLevelKey sourceKey) {
        final String name = sourceKey.getLevel().getName();
        final Level targetLevel = new TargetLevel(targetLevelValue, name, targetSyslogLevel);
        return registerMapping(targetLevel, sourceKey);
    }

    public synchronized Level registerMapping(final Level targetLevel, final SourceLevelKey sourceKey) {
        final String name = targetLevel.toString();
        final TargetLevelKey targetKey = new TargetLevelKey(targetLevel);
        if (targetToSource.containsKey(targetKey) || sourceToTarget.containsKey(sourceKey)) {
            throw new IllegalArgumentException("Cannot register log level '" + name + "' (already exists)");
        }
        targetToSource.put(targetKey, sourceKey);
        sourceToTarget.put(sourceKey, targetKey);
        log.debug("Registered new log level '" + name + "' with value " + targetLevel.toInt());
        return targetLevel;
    }

    public synchronized java.util.logging.Level getSourceLevelForTargetLevel(final Level targetLevel) {
        TargetLevelKey targetKey = new TargetLevelKey(targetLevel);
        if (targetToSource.containsKey(targetKey)) {
            final java.util.logging.Level level = targetToSource.get(targetKey).getLevel();
            if (level != null) {
                return level;
            }
        }
        // No mapping is found - just use INFO in that case (better to be safe than sorry)
        return org.jboss.logmanager.Level.INFO;
    }

    public synchronized Level getTargetLevelForSourceLevel(final java.util.logging.Level sourceLevel) {
        SourceLevelKey sourceKey = new SourceLevelKey(sourceLevel);
        if (sourceToTarget.containsKey(sourceKey)) {
            final Level targetLevel = sourceToTarget.get(sourceKey).getLevel();
            if (targetLevel != null) {
                return targetLevel;
            }
        }
        // We know nothing about this level so add an entry
        // But make it a weak reference so that we don't hold on to their classloader
        final SortedMap<SourceLevelKey, TargetLevelKey> headMap = sourceToTarget.headMap(sourceKey);
        final SortedMap<SourceLevelKey, TargetLevelKey> tailMap = sourceToTarget.tailMap(sourceKey);
        if (headMap.isEmpty()) {
            final SourceLevelKey firstKey = sourceToTarget.firstKey();
            final TargetLevelKey firstValue = sourceToTarget.get(firstKey);
            final Level firstLevel = firstValue.getLevel();
            return registerMapping(firstLevel.toInt() + 10000, firstLevel.getSyslogEquivalent(), sourceKey);
        } else if (tailMap.isEmpty()) {
            final SourceLevelKey lastKey = sourceToTarget.lastKey();
            final TargetLevelKey lastValue = sourceToTarget.get(lastKey);
            final Level lastLevel = lastValue.getLevel();
            return registerMapping(lastLevel.toInt() / 2, lastLevel.getSyslogEquivalent(), sourceKey);
        } else {
            final SourceLevelKey headKey = headMap.lastKey();
            final SourceLevelKey tailKey = tailMap.firstKey();
            final TargetLevelKey headValue = headMap.get(headKey);
            final TargetLevelKey tailValue = tailMap.get(tailKey);
            final Level headLevel = headValue.getLevel();
            final Level tailLevel = tailValue.getLevel();
            return registerMapping(
                (headLevel.toInt() + tailLevel.toInt()) / 2,
                (headLevel.getSyslogEquivalent() + tailLevel.getSyslogEquivalent()) / 2,
                sourceKey
            );
        }
    }
}
