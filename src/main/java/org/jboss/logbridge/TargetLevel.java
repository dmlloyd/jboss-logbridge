package org.jboss.logbridge;

public final class TargetLevel extends org.apache.log4j.Level {

    private static final long serialVersionUID = 4615086128477313299L;

    /**
     * Instantiate a Level object.
     */
    public TargetLevel(final int level, final String levelStr, final int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }
}
