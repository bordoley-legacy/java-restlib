/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.data;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

/**
 * An immutable representation of machine time.
 * <p> Using instances of DateTime is preferable to using raw instances of {@link long} or 
 * {@link Long} for several reasons:
 * <ul>
 * <li> Instances of DateTime must implement toString() in a locale neutral manner in terms of GMT. </li>
 * <li> Future revisions of this API will be updated to support wrapping the JSR-310 Instant class, 
 * allowing for improved integration with Java 8 time classes once they become available.</li> 
 * </ul> 
 */
@Immutable
public abstract class DateTime implements Comparable<DateTime> {
    private final long date;

    /**
     * Protected constructor used by subclasses. 
     * @param date the number of milliseconds from 1970-01-01T00:00:00Z
     */
    protected DateTime(final long date) {
        this.date = date;
    }
    

    @Override
    public final int compareTo(final DateTime that) {
        return Longs.compare(this.time(), that.time());
    }
    
    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DateTime) {
            final DateTime that = (DateTime) obj;
            return this.date == that.date;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(date);
    }

    /**
     * Returns a representation of this DateTime as the number of milliseconds 
     * from the epoch of 1970-01-01T00:00:00Z.
     */
    public final long time() {
        return this.date;
    }

    /**
     * Subclasses must provide an implementation of toString() that represents
     * the DateTime absolutely in terms of GMT.
     */
    @Override
    public abstract String toString();
}
