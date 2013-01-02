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

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public abstract class ContentRange {
    private static final class ContentByteRange extends ContentRange { 
        private final ByteRangeSpec range;
        private final long size;
        
        ContentByteRange(final ByteRangeSpec range, final long size) {
            this.range = range;
            this.size = size;
        }
        
        public int hashCode() {
            return Objects.hashCode(this.range, this.size);
        }
        
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof ContentByteRange) {
                final ContentByteRange that = (ContentByteRange) obj;
                return this.range.equals(that.range) &&
                        (this.size == that.size);
            } 
            return false;
        }
        
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(RangeUnit.BYTES).append(" ").append(this.range);     
            
            if (this.size < 0) {
                builder.append("*");
            } else {
                builder.append(size);
            }
            
            return builder.toString();
        }
    }
    
    public static ContentRange byteRange(long firstBytePos, long lastBytePos, long size) {
        final ByteRangeSpec.Range range = (ByteRangeSpec.Range) ByteRangeSpec.range(firstBytePos, lastBytePos);
        Preconditions.checkArgument(size > range.lastBytePos() || size < 0);
        
        return new ContentByteRange(range, size);
    }
    
    private ContentRange(){};
    
    @Override
    public abstract boolean equals(Object obj);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
}
