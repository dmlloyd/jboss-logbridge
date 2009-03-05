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
