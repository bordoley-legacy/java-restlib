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

package restlib.impl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * An ASCII CharSequence which support case-insensitive equality operations.
 */
@Immutable
public final class CaseInsensitiveString implements CharSequence {
    private static final CaseInsensitiveString EMPTY = 
                            new CaseInsensitiveString("");

    /**
     * Wraps the given String {@code in} in a CaseInsensitiveString.
     * @param in a non-null ASCII String.
     * @throws NullPointerException if {@code in} is null.
     * @throws IllegalArgumentException if {@code in} is not an ASCII only String.
     */
    public static CaseInsensitiveString wrap(final String in) {
        Preconditions.checkNotNull(in);
        Preconditions.checkArgument(CharMatcher.ASCII.matchesAllOf(in));
        if (in.isEmpty()) {
            return EMPTY;
        }
        return new CaseInsensitiveString(in);
    }

    private final String in;
    private final String lowercase;

    private CaseInsensitiveString(final String in) {
        this.in = in;
        this.lowercase = Ascii.toLowerCase(in);
    }

    @Override
    public char charAt(int index) {
        return this.in.charAt(index);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof CaseInsensitiveString) {
            final CaseInsensitiveString that = (CaseInsensitiveString) obj;
            return this.lowercase.equals(that.lowercase);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(lowercase);
    }

    /**
     * Returns true if this CaseInsensitveString is empty.
     */
    boolean isEmpty() {
        return this.lowercase.isEmpty();
    }

    @Override
    public int length() {
        return this.in.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return CaseInsensitiveString.wrap(this.in.substring(start,end));
    }

    @Override
    public String toString() {
        return this.in;
    }
}
