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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * An opaque validator for
 * differentiating between multiple representations of the same
 * resource, regardless of whether those multiple representations are
 * due to resource state changes over time, content negotiation
 * resulting in multiple representations being valid at the same time,
 * or both.
 */
@Immutable
public abstract class EntityTag {   
    /**
     * Instance of a strong entity-tag.
     */
    @Immutable
    public static class Strong extends EntityTag {
        private Strong(final String value){
            super(value);     
        }

        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Strong) {
                final Strong that = (Strong) obj;
                return this.value().equals(that.value());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.value());
        }

        @Override
        public String toString() {
            return "\"" + this.value() + "\"";
        }
    }
    
    /**
     * Instance of a weak entity-tag.
     */
    @Immutable
    public static class Weak extends EntityTag {
        private Weak(final String value){
            super(value);         
        }
        
        @Override
        public boolean equals(@Nullable final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj instanceof Weak) {
                final Weak that = (Weak) obj;
                return this.value().equals(that.value());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(_WEAK_TAG, this.value());
        }
        
        @Override
        public String toString(){
            return _WEAK_TAG + "\"" + this.value() + "\"";   
        }
    }

    private static final String _WEAK_TAG = "W/";
    
    static final Parser<EntityTag> PARSER = new Parser<EntityTag>() {
        final Parser<String> WEAK_TAG_PARSER = Parsers.stringParser(_WEAK_TAG);
        
        @Override
        public Optional<EntityTag> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .readOptional(WEAK_TAG_PARSER) // 0
                        .read(Primitives.QUOTED_STRING_PARSER) // 1
                        .tokens();
            
            final boolean isWeak = tokens.get(0).isPresent();          
            if (Optionals.isAbsent(tokens.get(1))) {
                buffer.position(startPos);
                return Optional.absent();
            } else {     
                final String etag = Optionals.toString(tokens.get(1));  
                return isWeak ? Optional.of(EntityTag.weakTag(etag)) : Optional.of(EntityTag.strongTag(etag));
            }
        }   
    };
    
    /**
     * Determines if two entity tags are equal using the strong compare algorithm defined
     * in the HTTP specification.
     * @param etag1 a non-null EntityTag.
     * @param etag2 a non-null EntityTag.
     * @throws NullPointerException if etag1 or etag2 are null.
     */
    public static boolean strongCompare(final EntityTag etag1, final EntityTag etag2) {
        Preconditions.checkNotNull(etag1);
        Preconditions.checkNotNull(etag2);
        
        if ((etag1 instanceof Weak) || (etag2 instanceof Weak)) {
            return false;
        } else {
            return etag1.equals(etag2);
        }
    }
    
    /**
     * Creates a new instance of EntityTag.Strong.
     * @param tag a non-null string including only valid etagc characters.
     * @throws NullPointerExcepction if {@code tag} is null.
     * @throws IllegalArgumentException if {@code tag} is empty or include invalid characters.
     */
    public static EntityTag strongTag(final String tag) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkArgument(!tag.isEmpty());
        Preconditions.checkArgument(
                Primitives.IS_ETAG_CHARACTER.apply(tag));
        return new EntityTag.Strong(tag);
    }
    
    /**
     * Determines if two entity tags are equal using the weak compare algorithm defined
     * in the HTTP specification.
     * @param etag1 a non-null EntityTag.
     * @param etag2 a non-null EntityTag.
     * @throws NullPointerException if etag1 or etag2 are null.
     */
    public static boolean weakCompare(final EntityTag etag1, final EntityTag etag2) {
        Preconditions.checkNotNull(etag1);
        Preconditions.checkNotNull(etag2);
        return etag1.value().equals(etag2.value());
    }
    
    /**
     * Creates a new instance of EntityTag.Weak.
     * @param tag a non-null string including only valid etagc characters.
     * @throws NullPointerExcepction if {@code tag} is null.
     * @throws IllegalArgumentException if {@code tag} is empty or include invalid characters.
     */
    public static EntityTag weakTag(final String tag) {
        Preconditions.checkNotNull(tag);
        Preconditions.checkArgument(!tag.isEmpty());
        Preconditions.checkArgument(
                Primitives.IS_ETAG_CHARACTER.apply(tag));
        return new EntityTag.Weak(tag);
    }
     
    private final String value;
    
    private EntityTag(final String value) {
        this.value = value;
    }
    
    /**
     * Returns the dequoted value of the EntityTag.
     */
    public String value() {
        return this.value;
    }
}
