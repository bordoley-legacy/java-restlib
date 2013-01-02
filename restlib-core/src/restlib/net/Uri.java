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


package restlib.net;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Ascii;

/**
 * A representation of an Uniform Resource Identifier as defined in 
 * <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC3986</a>.
 */
@Immutable
public final class Uri extends IRI {      
    /**
     * Returns a new {@code UriBuilder} instance.
     */
    public static UriBuilder builder() {
        return new UriBuilder();
    }     
    
    /**
     * Returns a new {@code TagBuilder} instance.
     */
    public static TagBuilder tagBuilder() {
        return new TagBuilder();
    }  
    
    /**
     * Parses a Uri from its String representation.
     * @throws NullPointerException if {@code input} is null.
     * @throws IllegalArgumentException if {@code input} is not parseable.
     */
    public static Uri parse(final CharSequence input) {
        return (Uri) parse(input, builder());
    }
    
    /**
     * Returns the results of performing the reference resolution 
     * of {@code relative} against {@code base} per the rules defined in
     * <a href="http://tools.ietf.org/html/rfc3986#section-5">section 5 of RFC3986</a>.
     * @param base an absolute Uri.
     * @param relative a non-null Uri.
     * @throws NullPointerException if base or relative are null.
     * @throws IllegalArgumentException if base is not an absolute Uri.
     */
    public static Uri relativeReference(final Uri base, final Uri relative) {
        return (Uri) relativeReference(base, relative, builder());
    }
    
    Uri (final UriBuilder builder) {
        super(builder);
    }
    
    public Uri canonicalize() {
        return (Uri) canonicalize(builder());
    }
    
    Uri normalize() {
        if (this.scheme().isEmpty()) {
            return this;
        }

        return Uri.builder()
                    .setScheme(Ascii.toLowerCase(this.scheme()))
                    .setUserinfo(UriEncoding.UTF8_DECODE_USER_INFO.apply(this.userinfo()).toString())
                    .setHost(Ascii.toLowerCase(this.host()))
                    .setPort(this.port())
                    .setPath(path().toUriPath().removeDotSegments())
                    .setQuery(UriEncoding.UTF8_DECODE_QUERY.apply(this.query()).toString())
                    .setFragment(UriEncoding.UTF8_DECODE_FRAGMENT.apply(this.fragment()).toString())
                    .build();                 
    }
}
