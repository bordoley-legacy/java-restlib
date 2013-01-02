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
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Object representation of an HTTP User-Agent string.
 */
@Immutable
public final class UserAgent {    
    private static final Predicate<Object> IS_PRODUCT_OR_COMMENT = 
            Predicates.or(
                    Predicates.instanceOf(Product.class),
                    Predicates.instanceOf(Comment.class));  

    private static final Parser<UserAgent> PARSER = new Parser<UserAgent>() {
        final Parser<Object> PRODCUT_OR_COMMENT_PARSER =
                Parsers.firstAvailableParser(Product.PARSER, Comment.PARSER);
        
        @Override
        public Optional<UserAgent> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
  
            final int startPos = buffer.position();
            final List<Optional<Object>> tokens = 
                    Tokenizer.create(buffer)
                        .read(Product.PARSER) // 0
                        .readWhileAvailable(
                                Primitives.WHITE_SPACE_PARSER, 
                                PRODCUT_OR_COMMENT_PARSER)
                        .tokens();
            if (Optionals.isAbsent(tokens.get(0))) {
                buffer.position(startPos);
                return Optional.absent();
            }           
            return Optional.of(
                    UserAgent.copyOf(
                            Iterables.filter(
                                    Optional.presentInstances(tokens),
                                    IS_PRODUCT_OR_COMMENT)));
        }      
    };
    
    private static UserAgent copyOf(final Iterable<Object> productsAndComments) {
        Preconditions.checkArgument(Iterables.all(productsAndComments, IS_PRODUCT_OR_COMMENT));
        return new UserAgent(ImmutableList.copyOf(productsAndComments));
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(final Product product) {
        return copyOf(ImmutableList.<Object> of(product));
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(final Product product, final Object productOrComment) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, productOrComment);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(final Product product, final Object poc1, final Object poc2) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, 
            final Object poc2, final Object poc3) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3, poc4);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3, poc4, poc5);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3, poc4, poc5, poc6);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6, final Object poc7) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3, poc4, poc5, poc6, poc7);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6, final Object poc7, final Object poc8) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3, poc4, poc5, poc6, poc7, poc8);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6, final Object poc7, final Object poc8,
            final Object poc9) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(product, poc1, poc2, poc3, poc4, poc5, poc6, poc7, poc8, poc9);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6, final Object poc7, final Object poc8,
            final Object poc9, final Object poc10) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(
                        product, poc1, poc2, poc3, poc4, poc5, poc6, poc7, poc8, poc9, poc10);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6, final Object poc7, final Object poc8,
            final Object poc9, final Object poc10, final Object poc11) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(
                        product, poc1, poc2, poc3, poc4, poc5, poc6, poc7, poc8, poc9, poc10, poc11);
        return copyOf(productsAndComments);
    }
    
    /**
     * Returns a UserAgent containing the given elements, in order.
     * @throws NullPointerException  if any element is null.
     * @throws IllegalArgumentException if any element is not an instance of {@link Product} or {@link Comment}.
     */
    static UserAgent of(
            final Product product, final Object poc1, final Object poc2,
            final Object poc3, final Object poc4, final Object poc5,
            final Object poc6, final Object poc7, final Object poc8,
            final Object poc9, final Object poc10, final Object poc11,
            final Object...others) {
        final ImmutableList<Object> productsAndComments = 
                ImmutableList.<Object> of(
                        product, poc1, poc2, poc3, poc4, poc5, poc6, poc7, poc8, poc9, poc10, poc11, others);
        return copyOf(productsAndComments);
    }
    
    /**
     * Parses a UserAgent from its String representation.
     * @throws NullPointerException if {@code userAgent} is null.
     * @throws IllegalArgumentException if {@code userAgent} is not parseable.
     */
    public static UserAgent parse(final CharSequence userAgent) {
        return Parsers.parseWithParser(userAgent, PARSER);
    }

    private final ImmutableList<Object> productsAndComments;
    
    private UserAgent(final ImmutableList<Object> productsAndComments) {
        this.productsAndComments = productsAndComments;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof UserAgent) {
            final UserAgent that = (UserAgent) obj;
            return this.productsAndComments.equals(that.productsAndComments);
        } 
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.productsAndComments);
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        
        for (final Object productOrComment : this.productsAndComments) {
            builder.append(productOrComment).append(" ");
        }
        
        builder.setLength(builder.length() -1);        
        return builder.toString();
    }
}
