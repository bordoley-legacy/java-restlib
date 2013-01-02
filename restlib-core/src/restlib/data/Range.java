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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.impl.Tokenizer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Client requested sub-ranges of response representation data.
 */
@Immutable
public abstract class Range {
    /**
     * A range composed of a set of byte-ranges.
     */
    public static final class Bytes extends Range {
        private static final Joiner COMMA_JOINER = Joiner.on(", ");
        
        private final ImmutableList<ByteRangeSpec> byteRangeSet;
        
        private Bytes(final ImmutableList<ByteRangeSpec> byteRangeSet) {
            this.byteRangeSet = byteRangeSet;
        }
        
        /** 
         * Returns a list of byte-ranges that compose this Range.
         */
        public List<ByteRangeSpec> byteRanges() {
            return this.byteRangeSet;
        }
        
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Bytes){
                final Bytes that = (Bytes) obj;
                return this.byteRangeSet.equals(that.byteRangeSet);  
            } 
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.byteRangeSet);
        }

        @Override
        public String toString() {
            return RangeUnit.BYTES + "=" + COMMA_JOINER.join(byteRangeSet);
        }    
        
        @Override
        public RangeUnit unit() {
            return RangeUnit.BYTES;
        }
    }
    
    /**
     * A range for undefined range-units.
     */
    public static final class Other extends Range {
        private final String chars;
        private final RangeUnit rangeUnit;
        
        private Other(final RangeUnit rangeUnit, final String chars) {
            this.rangeUnit = rangeUnit;
            this.chars = chars;
        }

        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Other) {
                final Other that = (Other)obj;
                return this.rangeUnit.equals(that.rangeUnit) &&
                        this.chars.equals(that.chars);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.rangeUnit, this.chars);
        }
        
        /**
         * Returns the characters in the range value. 
         */
        public String range() {
            return this.chars;
        }

        @Override
        public String toString() {
            return this.rangeUnit + "=" + this.chars;
        }
        
        @Override
        public RangeUnit unit() {
            return this.rangeUnit;
        }
    }
    
    private static final CharMatcher ASCII_NOT_NULL = CharMatcher.ASCII.and(CharMatcher.is((char)0).negate());
    
    static final Parser<Range> PARSER = new Parser<Range>() {
        final Parser<String> ASCII_NOT_NULL_PARSER = Parsers.whileMatchesParser(ASCII_NOT_NULL);
        final Parser<Character> EQUALS_CHAR_PARSER = Parsers.charParser('=');
        
        @Override
        public Optional<Range> parse(final CharBuffer buffer) { 
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();         
            final Tokenizer tokenizer =        
                    Tokenizer.create(buffer)
                        .read(Primitives.TOKEN_PARSER) // 0
                        .read(EQUALS_CHAR_PARSER); // 1
            final List<Optional<Object>> tokens = tokenizer.tokens();
            
            if (Optionals.isAbsent(tokens.get(0))) {
                return Optional.absent();
            }
            
            final RangeUnit rangeUnit = RangeUnit.create(Optionals.toString(tokens.get(0)));
            if (rangeUnit.equals(RangeUnit.BYTES)) {       
                return readByteRangesSpecifiers(buffer);
            } 
            
            tokenizer.read(ASCII_NOT_NULL_PARSER); // 2
            if (Optionals.isAbsent(tokens.get(2))) {
                buffer.position(startPos);
                return Optional.absent();
            } else {
                final String rangeValue = Optionals.toString(tokens.get(2));
                return Optional.<Range> of(Range.otherRange(rangeUnit, rangeValue));    
            }
        }

        private Optional<Range> readByteRangesSpecifiers(final CharBuffer buffer) {   
            Preconditions.checkNotNull(buffer);
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(ByteRangeSpec.PARSER)
                        .readWhileAvailable(
                                Primitives.OWS_COMMA_OWS_PARSER, ByteRangeSpec.PARSER)
                        .tokens();
            if (Optionals.isAbsent(tokens.get(0))) {
                return Optional.absent();
            } else {         
                return Optional.<Range> of(
                    Range.byteRange(
                            Iterables.filter(Optional.presentInstances(tokens), ByteRangeSpec.class)));                 
        
            }
        }
    }; 
    
    /**
     * Creates a new {@code Range.Bytes} instance.
     * @param byteRangeSet a list of ByteRanges to include in the byte-range.
     * @throws NullPointerException if byteRangeSet is null or any element within it is null.
     */
    public static Bytes byteRange(final Iterable<? extends ByteRangeSpec> byteRangeSet) {
        return new Bytes(ImmutableList.copyOf(byteRangeSet));
    }
    
    /**
     * Creates a new {@code Range.Other} instance.
     * @param rangeUnit any RangeUnit except {@link RangeUnit.BYTES}.
     * @param range a string containing valid other-range-resp-spec characters.
     * @throws NullPointerException if either {@code rangeUnit} or {@code range} are null.
     * @throws IllegalArgumentException if {@code RangUnit} equals {@link RangeUnit.BYTES}.
     */
    public static Other otherRange(final RangeUnit rangeUnit, final String range) {
        Preconditions.checkNotNull(rangeUnit);
        Preconditions.checkNotNull(range);
        Preconditions.checkArgument(!rangeUnit.equals(RangeUnit.BYTES));
        Preconditions.checkArgument(!rangeUnit.equals(RangeUnit.ACCEPT_NONE));
        Preconditions.checkArgument(!range.isEmpty());
        Preconditions.checkArgument(ASCII_NOT_NULL.matchesAllOf(range));
        return new Other(rangeUnit, range);    
    }

    private Range(){}
    
    /**
     * Return the {@link RangeUnit} of this Range.
     * @return
     */
    public abstract RangeUnit unit();
}