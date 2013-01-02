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

import java.net.IDN;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Ascii;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * A representation of an International Resource Identifier as defined in 
 * <a href="http://www.ietf.org/rfc/rfc3987.txt">RFC3987</a>.
 */
@Immutable
public class IRI {    
    /**
     * Returns a new {@code IRIBuilder} instance.
     */
    public static IRIBuilder builder() {
        return new IRIBuilder();
    }
    
    /**
     * Parses an IRI from its String representation.
     * @throws NullPointerException if {@code input} is null.
     * @throws IllegalArgumentException if {@code input} is not parseable.
     */
    public static IRI parse(final CharSequence input) {
        return parse(input, builder());
    }
        
    static IRI parse(final CharSequence iri, final IRIBuilder builder) {
        Preconditions.checkNotNull(iri);

        final List<String> parts = NetParserImpl.parseIRI(iri);

        final String scheme = parts.get(0);
        final String userinfo = parts.get(1);
        final String host = parts.get(2);
        final String port = parts.get(3);
        final String path = parts.get(4);
        final String query = parts.get(5);
        final String fragment = parts.get(6);

        if (!port.isEmpty()) {
            builder.setPort(Integer.parseInt(port));
        }
        
        return builder
                    .setScheme(scheme)
                    .setUserinfo(userinfo)
                    .setHost(host)       
                    .setPath(path)
                    .setQuery(query)
                    .setFragment(fragment)                   
                    .build();
    }
    
    /**
     * Returns the results of performing the reference resolution 
     * of {@code relative} against {@code base} per the rules defined in
     * <a href="http://tools.ietf.org/html/rfc3986#section-5">section 5 of RFC3986</a>.
     * @param base an absolute IRI.
     * @param relative a non-null IRI.
     * @throws NullPointerException if base or relative are null.
     * @throws IllegalArgumentException if base is not an absolute IRI.
     */
    public static IRI relativeReference(final IRI base, final IRI relative) {
        return relativeReference(base, relative, builder());
    }
    
    static IRI relativeReference(final IRI base, final IRI relative, final IRIBuilder builder) {
        Preconditions.checkNotNull(base);
        Preconditions.checkNotNull(relative);
        Preconditions.checkNotNull(builder);
        Preconditions.checkArgument(base.isAbsolute(), "Base IRI must be an absolute IRI");
        
        if (!relative.scheme.isEmpty()) {
            builder.setScheme(relative.scheme)
                .setUserinfo(relative.userinfo)
                .setHost(relative.host)
                .setPort(relative.port)
                .setPath(relative.path.removeDotSegments())
                .setQuery(relative.query);
        } else {
            builder.setScheme(base.scheme);

            if (!relative.authority().isEmpty()) {
                builder.setHost(relative.host)
                        .setPort(relative.port)
                        .setUserinfo(relative.userinfo)
                        .setPath(relative.path.removeDotSegments())
                        .setQuery(relative.query);
            } else {
                builder.setHost(base.host).setPort(base.port).setUserinfo(base.userinfo);

                if (relative.path.isEmpty()) {
                    builder.setPath(base.path);

                    if (!relative.query.equals("")) {
                        builder.setQuery(relative.query);
                    } else {
                        builder.setQuery(base.query);
                    }
                } else {
                    if (relative.path.startsWithSlash()) {
                        builder.setPath(relative.path.removeDotSegments());
                    } else {
                        builder.setPath(Path.merge(base, relative).removeDotSegments());
                    }
                   
                    builder.setQuery(relative.query);
                }
            }
        }
        builder.setFragment(relative.fragment);
        return builder.build();
    }

    private final String fragment;
    private final String host;
    private final Path path;
    private final Optional<Integer> port;
    private final String query;
    private final String scheme;
    private final String userinfo;

    IRI(final IRIBuilder builder) {
        this.fragment = builder.fragment;
        this.host = builder.host;
        this.path = builder.path;
        this.port = builder.port;
        this.query = builder.query;
        this.scheme = builder.scheme;
        this.userinfo = builder.userinfo;
    }
    
    /**
     * Returns the authority component of the IRI or the empty string if absent.
     */
    public final String authority() {
        final StringBuilder builder = new StringBuilder();
        if (!this.userinfo.isEmpty()) {
            builder.append(this.userinfo).append("@");
        }
        
        builder.append(host());
        
        if (port.isPresent()) {
            builder.append(":" + this.port.get());
        }

        return builder.toString();
    }
    
    /**
     * Returns the canonical version of this IRI.
     * 
     * <p> Note if the scheme component of this IRI is empty, this method returns the same IRI.
     */
    public IRI canonicalize() {     
        return canonicalize(builder());
    }
    
    IRI canonicalize(final IRIBuilder builder) {  
        Preconditions.checkNotNull(builder);
        if (this.scheme.isEmpty()) {
            return this;
        }

        final IRI normalized = this.normalize();    
        final Path path = normalized.path.canonicalize();
             
        return builder
                    .setScheme(normalized.scheme)
                    .setUserinfo(normalized.userinfo)
                    .setHost(normalized.host)
                    .setPort(normalized.port)
                    .setPath(path)
                    .setQuery(normalized.query)
                    .setFragment(normalized.fragment)
                    .build(); 
    }
    
    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof IRI) {
            final IRI that = (IRI) obj;
            return this.fragment.equals(that.fragment) &&
                    this.host.equals(that.host) && 
                    this.path.equals(that.path) && 
                    this.port.equals(that.port) &&
                    this.query.equals(that.query) && 
                    this.scheme.equals(that.scheme) && 
                    this.userinfo.equals(that.userinfo);
        }
        return false;
    }
    
    /**
     * Returns the fragment component of the IRI or the empty string if absent.
     */
    public final String fragment() {
        return this.fragment;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hashCode(
                this.fragment, 
                this.host, 
                this.path, 
                this.port, 
                this.query, 
                this.scheme, 
                this.userinfo);
    }
    
    /**
     * Returns the host component of the IRI or the empty string if absent.
     */
    public final String host() {
        return this.host;
    }
    
    /**
     * Returns true if the IRI instance is an absolute IRI.
     */
    public final boolean isAbsolute() {
        return !this.scheme.isEmpty() && fragment.isEmpty();
    }
    
    final boolean isValid() {             
        // Validate the authority component
        if (host.isEmpty() && (!userinfo.isEmpty() || port.isPresent())) {
            return false;
        }
        
        final String authority = authority();

        // http://tools.ietf.org/html/rfc3986#section-3.3
        // If a URI contains an authority component, then the path component
        // must either be empty or begin with a slash ("/") character. 
        if (!authority.isEmpty() && !path.isPathAbEmpty()) {
            return false;
        } 
        
        // http://tools.ietf.org/html/rfc3986#section-3.3
        // If a URI does not contain an authority component, 
        // then the path cannot begin with two slash characters ("//").  
        else if (authority.isEmpty() && path.startsWithDoubleSlash()) {
            return false;
        } 
        
        // http://tools.ietf.org/html/rfc3986#section-4.2
        // A path segment that contains a colon character (e.g., "this:that")
        // cannot be used as the first segment of a relative-path reference, as
        // it would be mistaken for a scheme name.
        else if(scheme.isEmpty() && authority.isEmpty() && !path.isPathNoScheme()) {
            return false;       
        } else {
            return true;
        }
    }  
    
    /**
     * Returns the normalized form of this IRI.
     * 
     * <p>Normalization includes:
     * <ul>
     * <li> lower casing the scheme,
     * <li> unescaping unnecessarily escaped code points in the path, query and fragment parts.
     * </ul>
     */
    IRI normalize() {
        if (this.scheme.isEmpty()) {
            return this;
        }
             
        return builder()
                    .setScheme(Ascii.toLowerCase(this.scheme))
                    .setUserinfo(UriEncoding.UTF8_DECODE_IUSER_INFO.apply(this.userinfo).toString())
                    .setHost(
                            IDN.toUnicode(Ascii.toLowerCase(IDN.toASCII(this.host))))
                    .setPort(this.port)
                    .setPath(path.toIRIPath().removeDotSegments())
                    .setQuery(UriEncoding.UTF8_DECODE_IQUERY.apply(this.query).toString())
                    .setFragment(UriEncoding.UTF8_DECODE_IFRAGMENT.apply(this.fragment).toString())
                    .build();            
    }
    
    /**
     * Returns the path component of the IRI.
     */
    public final Path path() {
        return path;
    }
    
    /**
     * Returns the port component of the IRI if present.
     */
    public Optional<Integer> port() {
        return this.port;
    }
    
    /**
     * Returns the query component of the IRI or the empty string if absent.
     */
    public String query() {
        return this.query;
    };
    
    /**
     * Returns the scheme component of the IRI or the empty string if absent.
     */
    public final String scheme() {
        return this.scheme;
    }
    
    /**
     * Returns an instance of this IRI encoded as an IRI. 
     * This method is useful for decoding instances of URI into internationalized IRIs.
     */
    public final IRI toIRI() {
        if(this instanceof Uri) {            
            return builder()
                    .setScheme(this.scheme)
                    .setUserinfo(UriEncoding.UTF8_DECODE_IUSER_INFO.apply(this.userinfo).toString())
                    .setHost(IDN.toUnicode(this.host))
                    .setPort(this.port)
                    .setPath(path().toIRIPath())
                    .setQuery(UriEncoding.UTF8_DECODE_IQUERY.apply(this.query).toString())
                    .setFragment(UriEncoding.UTF8_DECODE_IFRAGMENT.apply(this.fragment).toString())
                    .build(); 
        } else {
            return this;
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        final String authority = authority();
        
        if (!this.scheme.isEmpty()) {
            builder.append(this.scheme).append(":");
        }
        
        if (!authority.isEmpty()) {
            builder.append("//").append(authority);
        }
        
        builder.append(this.path());       
        
        if (!this.query.isEmpty()) {
            builder.append("?").append(this.query);
        }

        if (!this.fragment.isEmpty()) {
            builder.append("#").append(this.fragment);
        }
        
        return builder.toString();
    }
    
    /**
     * Returns an instance of this IRI encoded as a URI.
     */
    public final Uri toUri() {
        if (this instanceof Uri) {
            return (Uri) this;
        } else {
            return Uri.builder()
                    .setScheme(this.scheme)
                    .setUserinfo(
                            UriEncoding.UTF8_ENCODE_USER_INFO.apply(this.userinfo))                  
                    .setHost(IDN.toASCII(this.host))
                    .setPort(this.port)
                    .setPath(path().toUriPath())
                    .setQuery(UriEncoding.UTF8_ENCODE_QUERY.apply(this.query))
                    .setFragment(UriEncoding.UTF8_ENCODE_FRAGMENT.apply(this.fragment))
                    .build();                
        }
    }
    
    /**
     * Returns the userinfo component of the IRI or the empty string if absent.
     */
    public final String userinfo() {
        return this.userinfo;
    }
}
