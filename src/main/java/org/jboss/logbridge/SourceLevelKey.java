package org.jboss.logbridge;

import java.lang.ref.WeakReference;

import java.util.logging.Level;

public final class SourceLevelKey implements Comparable<SourceLevelKey> {
    private final WeakReference<Level> julLevelRef;
    private final int numLevel;
    private final String name;

    public SourceLevelKey(final Level julLevel) {
        julLevelRef = new WeakReference<Level>(julLevel);
        numLevel = julLevel.intValue();
        name = julLevel.getName();
    }

    public Level getLevel() {
        return julLevelRef.get();
    }

    public int compareTo(final SourceLevelKey other) {
        return numLevel != other.numLevel ?
            numLevel - other.numLevel :
            name.compareTo(other.name);
    }

    public boolean equals(final Object o) {
        return o instanceof SourceLevelKey ? compareTo((SourceLevelKey)o) == 0 : false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException("hashCode not implemented");
    }
}
