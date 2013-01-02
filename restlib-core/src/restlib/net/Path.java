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

import java.util.List;
import java.util.ListIterator;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A representation of a <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC3986</a> path.
 */
@Immutable
public final class Path extends ForwardingList<String> {
    private static final Path EMPTY = new Path(ImmutableList.<String> of());    
    private static final Path FORWARD_SLASH_PATH = Path.copyOf(ImmutableList.of("",""));
    private static final Predicate<String> IS_EMPTY = Predicates.equalTo("");
    private static final Joiner PATH_JOINER = Joiner.on('/');   
    private static final Splitter PATH_SPLITTER = Splitter.on('/');
    
    /**
     * Return a new {@code Path} instance containing the given segments.
     * @throws NullPointerException if segments is null;
     * @throws IllegalArgumentException if any segment in {@code segments} includes
     * the path separator character ('/').
     */
    public static Path copyOf(final Iterable<String> segments) {
        Preconditions.checkNotNull(segments);
        if (Iterables.isEmpty(segments)) {
            return EMPTY;
        }
        
        for (final String segment : segments) {
            Preconditions.checkArgument(!segment.contains("/"));
        }
        return new Path(ImmutableList.copyOf(segments));
    }
    
    static Path merge(final IRI base, final IRI relative) {
        Preconditions.checkNotNull(base);
        Preconditions.checkNotNull(relative);
        Preconditions.checkArgument(base.isAbsolute(), "Base IRI must be an absolute IRI");

        if (!base.authority().isEmpty() && base.path().segments.isEmpty()) {
            return Path.copyOf(
                        ImmutableList.<String> builder()
                            .add("").addAll(relative.path().segments).build());        
        } else {
            return Path.copyOf(
                    ImmutableList.<String> builder()
                                .addAll(base.path().segments.subList(
                                            0, base.path().segments.size() - 1))
                                .addAll(relative.path().segments)
                                .build());
        }
    }
    
    public static Path of() {
        return EMPTY;
    }
    
    public static Path of(final String s1) {
        return Path.copyOf(ImmutableList.of(s1));
    }
    
    public static Path of(final String s1, final String s2) {
        return Path.copyOf(ImmutableList.of(s1, s2));
    }
    
    public static Path of(
            final String s1, final String s2, final String s3) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6,
            final String s7) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6, s7));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6,
            final String s7, final String s8) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6, s7, s8));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6,
            final String s7, final String s8,
            final String s9) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6, s7, s8, s9));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6,
            final String s7, final String s8,
            final String s9, final String s10) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10));
    }

    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6,
            final String s7, final String s8,
            final String s9, final String s10,
            final String s11) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11));
    }
    
    public static Path of(
            final String s1, final String s2, 
            final String s3, final String s4,
            final String s5, final String s6,
            final String s7, final String s8,
            final String s9, final String s10,
            final String s11, final String s12,
            final String...others) {
        return Path.copyOf(ImmutableList.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, others));
    }
    
    /**
     * Parses an {@code Path} from its {@code String} representation.
     * @throws NullPointerException if {@code path} is null.
     * @throws IllegalArgumentException if {@code path} is not parseable.
     */
    public static Path parse(final String path) {
        Preconditions.checkNotNull(path);
        if (path.isEmpty()) {
            return EMPTY;
        }   
        return new Path(ImmutableList.copyOf(PATH_SPLITTER.split(path))); 
    }
    
    private final List<String> segments;
    
    private Path(final List<String> segments) {
        this.segments = segments;
    }
    
    /**
     * Canonicalizes URI path segments by removing any empty path segments,
     * normalizing the empty path (and equivalents) to "/", and removing
     * any trailing empty path segments. 
     * @param segments The path segments to canonicalize.
     * @return The canonicalized path segments
     */
    public Path canonicalize() {
        final Path canonical = this.doCanonicalize();
        if (this.equals(canonical)) {
            return this;
        } else {
            return canonical;
        }
    }
    
    @Override
    protected List<String> delegate() {
        return segments;
    }
     
    private Path doCanonicalize() {
        if (segments.isEmpty()) {
            return FORWARD_SLASH_PATH;
        }

        if (segments.size() == 1) {
            return segments.get(0).isEmpty() ? FORWARD_SLASH_PATH : this;
        } else if (segments.size() == 2) {
            if (Iterables.all(segments, IS_EMPTY)) {
                return FORWARD_SLASH_PATH;
            } else if (segments.get(1).isEmpty()) {
                return Path.copyOf(segments.subList(0, 1));
            } else {
                return this;
            }           
        } else {
            final List<String> buffer = Lists.newLinkedList(segments);
            
            // Remove empty any empty segments between the first and last segment
            final ListIterator<String> itr = buffer.subList(1, buffer.size() - 1).listIterator();
            while (itr.hasNext()) {
                if (itr.next().isEmpty()) {
                    itr.remove();
                }
            }
            
            if (buffer.equals(FORWARD_SLASH_PATH.segments)) {
                return FORWARD_SLASH_PATH;
            }
            
            if (buffer.get(buffer.size() -1).isEmpty()) {
                buffer.remove(buffer.size() - 1);
            }
            return Path.copyOf(buffer);
        }      
    }

    Path doRemoveDotSegments() { 
        final List<String> buffer = Lists.newLinkedList(segments);
        final ListIterator<String> itr = buffer.listIterator();
        
        while(itr.hasNext()) {
            final String segment = itr.next();
            if (segment.equals(".")) {
                itr.remove();
                
                if (!itr.hasNext()) {
                    itr.add("");
                }
            } else if (segment.equals("..") ) {
                itr.remove();
                
                if (!itr.hasNext()) {
                    itr.add("");
                    itr.previous();
                }
                
                if (itr.hasPrevious()) {
                    final String prevSegment = itr.previous();
                    if (!prevSegment.isEmpty()) {
                        itr.remove();
                    }
                }
            }
        }
        
        return Path.copyOf(buffer);
    }
    
    /**
     * Returns true if all segments in this Path are valid IRI isegments.
     */
    public boolean isIRIPath() {
        return Iterables.all(segments, IRIPredicates.IS_ISEGMENT);
    }
    
    boolean isNormalized() {
        return !segments.contains("..") && !segments.contains(".");
    }
    
    boolean isPathAbEmpty() {
        if (segments.isEmpty()) {
            return true;
        } else {
            return segments.get(0).isEmpty();          
        } 
    }
    
    boolean isPathAbsolute() {
        if (segments.isEmpty()) {
            return false;
        } else if (segments.size() <= 2) {
            return segments.get(0).isEmpty();
        } else {
            return segments.get(0).isEmpty() && (!segments.get(1).isEmpty());
        }
    }
    
    boolean isPathNoScheme() {
        return segments.isEmpty() ? true : !segments.get(0).contains(":");
    }
    
    /**
     * Returns true if all segments in this Path are valid Uri segments.
     */
    public boolean isUriPath() {
        return Iterables.all(segments, IRIPredicates.IS_SEGMENT);
    }
    
    Path removeDotSegments() {
        if (segments.isEmpty()) {
            return this;
        } 
        
        final Path noDotSegments = doRemoveDotSegments();
        if (this.equals(noDotSegments)) {
            return this;
        } else {
            return noDotSegments;
        }
    }

    /**
     * Returns the segments composing this Path. The returned {@code Iterable} is unmodifiable.
     */
    public Iterable<String> segments() {
        return this.segments;
    }
    
    boolean startsWithDoubleSlash() {
        // A double slash path looks like ["", "", *]    
        return (segments.size() > 2) && Iterables.all(segments.subList(0, 2), IS_EMPTY);
    }
    
    boolean startsWithSlash() {
        return (segments.size() > 0) && segments.get(0).equals("");
    }
    
    /**
     * Returns a representation of this Path whose segments are valid 
     * IRI isegments.
     */
    public Path toIRIPath() {
        return Path.copyOf(
                Iterables.transform(segments, 
                        Functions.compose(
                                UriEncoding.UTF8_ENCODE_PATH_ISEGMENT,
                                UriEncoding.UTF8_DECODE_PATH_ISEGMENT)));
    }

    @Override
    public String toString() {
        return PATH_JOINER.join(this.segments);
    }

    /**
     * Returns a representation of this Path whose segments are valid 
     * Uri segments.
     */
    public Path toUriPath() {
        return Path.copyOf(
                Iterables.transform(segments, 
                        Functions.compose(
                            UriEncoding.UTF8_ENCODE_PATH_SEGMENT,
                            UriEncoding.UTF8_DECODE_PATH_SEGMENT)));
    }   
}
