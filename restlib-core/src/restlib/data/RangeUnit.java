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

import restlib.impl.Registry;

import com.google.common.base.Ascii;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Structural unit used in a Range request or response to describe how
 * a representation is broken into subranges.
 */
@Immutable
public final class RangeUnit {
    private static final Registry<RangeUnit> _REGISTERED = new Registry<RangeUnit>();
    
    /**
     * The range-unit used to indicate that a representation is broken 
     * into byte subranges.
     */
    public static final RangeUnit BYTES = register(create("bytes"));
    
    /**
     * The range-unit sent by servers that do not accept any kind of range request. 
     * May be sent by a resource to advise the client not to attempt a range request.
     */
    public static final RangeUnit ACCEPT_NONE = register(create("none"));

    static final RangeUnit create(final String in){
        Preconditions.checkNotNull(in);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(in));
        final RangeUnit rangeUnit = new RangeUnit(Ascii.toLowerCase(in));
        return _REGISTERED.getIfPresent(rangeUnit);
    }
    
    private static final RangeUnit register(final RangeUnit rangeUnit) {
        return _REGISTERED.register(rangeUnit);
    }
    
    private final String rangeUnit;
    
    private RangeUnit(final String rangeUnit) {
        this.rangeUnit = rangeUnit;
    }
    
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof RangeUnit) {
            final RangeUnit that = (RangeUnit) obj;
            return this.rangeUnit.equals(that.rangeUnit);  
        }
        return false;
    }
    
    public int hashCode() {
        return Objects.hashCode(this.rangeUnit);
    }
    
    public String toString() {
        return this.rangeUnit;
    }
}
