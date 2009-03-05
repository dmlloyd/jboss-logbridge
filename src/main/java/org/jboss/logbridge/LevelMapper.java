package org.jboss.logbridge;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Level;

/**
 *
 */
public final class LevelMapper {
    private final SortedMap<TargetLevelKey, SourceLevelKey> targetToSource = new TreeMap<TargetLevelKey, SourceLevelKey>();
    private final SortedMap<SourceLevelKey, TargetLevelKey> sourceToTarget = new TreeMap<SourceLevelKey, TargetLevelKey>();

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LevelMapper.class);

    public LevelMapper() {
        // Populate mapping with standard log levels
        registerMapping(40000, 3, new SourceLevelKey(java.util.logging.Level.SEVERE));
        registerMapping(30000, 4, new SourceLevelKey(java.util.logging.Level.WARNING));
        registerMapping(20000, 5, new SourceLevelKey(java.util.logging.Level.INFO));
        registerMapping(15000, 6, new SourceLevelKey(java.util.logging.Level.CONFIG));
        registerMapping(10000, 7, new SourceLevelKey(java.util.logging.Level.FINE));
        registerMapping(7500, 7, new SourceLevelKey(java.util.logging.Level.FINER));
        registerMapping(5000, 7, new SourceLevelKey(java.util.logging.Level.FINEST));
    }

    public synchronized Level registerMapping(final int targetLevelValue, final int targetSyslogLevel, final SourceLevelKey sourceKey) {
        final String name = sourceKey.getLevel().getName();
        final Level targetLevel = new TargetLevel(targetLevelValue, name, targetSyslogLevel);
        final TargetLevelKey targetKey = new TargetLevelKey(targetLevel);
        if (targetToSource.containsKey(targetKey) || sourceToTarget.containsKey(sourceKey)) {
            throw new IllegalArgumentException("Cannot register log level '" + name + "' (already exists)");
        }
        targetToSource.put(targetKey, sourceKey);
        sourceToTarget.put(sourceKey, targetKey);
        log.debug("Registered new log level '" + name + "' with value " + targetLevelValue);
        return targetLevel;
    }

    public synchronized Level getTargetLevelForSourceLevel(final java.util.logging.Level sourceLevel) {
        SourceLevelKey sourceKey = new SourceLevelKey(sourceLevel);
        if (sourceToTarget.containsKey(sourceKey)) {
            final Level targetLevel = (sourceToTarget.get(sourceKey)).getLevel();
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
