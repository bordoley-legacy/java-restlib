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

import java.nio.CharBuffer;
import java.util.List;

import restlib.impl.CommonParsers;
import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public abstract class ByteRangeSpec {
    static final class Range extends ByteRangeSpec {
        private final long firstBytePos;
        private final long lastBytePos;
        
        private Range(final long firstBytePos, final long lastBytePos) {
            this.firstBytePos = firstBytePos;
            this.lastBytePos = lastBytePos;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Range){
                final Range that = (Range) obj;
                return (this.firstBytePos == that.firstBytePos) &&
                            (this.lastBytePos == that.lastBytePos);
            }
            return false;
        }
        
        public long firstBytePos() {
            return firstBytePos;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.firstBytePos, this.lastBytePos);
        }
        
        public long lastBytePos() {
            return lastBytePos;
        }
        
        
        @Override
        public String toString() {
            return  firstBytePos + "-" + lastBytePos;
        }
    }
    
    private static final class StartingAt extends ByteRangeSpec {
        private final long bytePos;
        
        StartingAt(final long bytePos) {
            this.bytePos = bytePos;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof StartingAt) {
                final StartingAt that = (StartingAt) obj;
                return this.bytePos == that.bytePos;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(bytePos);
        }

        @Override
        public String toString() {
            return bytePos + "-";
        }      
    }
    
    private static final class Suffix extends ByteRangeSpec {
        private final long numBytes;
        
        Suffix(final long numBytes) {
            this.numBytes = numBytes;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Suffix) {
                final Suffix that = (Suffix) obj;
                return this.numBytes == that.numBytes;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.numBytes);
        }

        @Override
        public String toString() {
            return "-" + this.numBytes;
        }        
    }  
    
    static final Parser<ByteRangeSpec> PARSER = new Parser<ByteRangeSpec>() {
        @Override
        public Optional<ByteRangeSpec> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .readOptional(Parsers.INTEGER_PARSER) // 0
                        .read(Parsers.charParser('-')) // 1
                        .read(Parsers.INTEGER_PARSER) // 2
                        .tokens(); 
            if (Optionals.isAbsent(tokens.get(0))) {
                if (Optionals.isAbsent(tokens.get(2))) {
                    buffer.position(startPos);
                    return Optional.absent();
                } else {
                    final long suffix = CommonParsers.parseUnsignedLong(tokens.get(2).get().toString());
                    return Optional.of(ByteRangeSpec.suffix(suffix));  
                }
            } else {
                final long firstPos = CommonParsers.parseUnsignedLong(tokens.get(0).get().toString());
                if (Optionals.isAbsent(tokens.get(2))) {
                    return Optional.of(ByteRangeSpec.startingAt(firstPos));
                } else {
                    final long lastPos = CommonParsers.parseUnsignedLong(tokens.get(2).get().toString());
                    return Optional.of(ByteRangeSpec.range(firstPos, lastPos));
                }
            }
        }        
    };
    
    public static ByteRangeSpec range(long firstBytePos, long lastBytePos) {
        Preconditions.checkArgument(firstBytePos >= 0);
        Preconditions.checkArgument(lastBytePos >= 0);
        Preconditions.checkArgument(lastBytePos >= firstBytePos);
        
        return new Range(firstBytePos, lastBytePos);
    } 
    
    public static ByteRangeSpec startingAt(long bytePos) {
        Preconditions.checkArgument(bytePos >= 0);  
        return new StartingAt(bytePos);
    }
    
    public static ByteRangeSpec suffix(long numBytes) {
        Preconditions.checkArgument(numBytes >= 0);  
        return new Suffix(numBytes);
    }
    
    private ByteRangeSpec() {}
    
    @Override
    public abstract boolean equals(Object obj);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
}
