package org.jboss.logbridge;

import org.apache.log4j.Level;

public final class TargetLevelKey implements Comparable<TargetLevelKey> {
    private final Level level;
    private final int numLevel;
    private final String name;

    public TargetLevelKey(final Level level) {
        this.level = level;
        numLevel = level.toInt();
        name = level.toString();
    }

    public Level getLevel() {
        return level;
    }

    public int compareTo(final TargetLevelKey other) {
        return numLevel != other.numLevel ?
                numLevel - other.numLevel :
                name.compareTo(other.name);
    }

    public boolean equals(final Object o) {
        return o instanceof TargetLevelKey ? compareTo((TargetLevelKey)o) == 0 : false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException("hashCode not implemented");
    }
}
