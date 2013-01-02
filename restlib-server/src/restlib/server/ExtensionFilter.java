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


package restlib.server;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import restlib.ClientPreferences;
import restlib.ClientPreferencesWrapper;
import restlib.Request;
import restlib.RequestWrapper;
import restlib.data.ExtensionMap;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.net.Path;
import restlib.net.Uri;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * {@code RequestFilter} that can be used in an application to perform
 * connection negotiation based upon the presence of an extension in 
 * the last segment URI path.
 * 
 * <p>
 * For instance, a URI such as:
 * <pre>
 *      http://www.example.com/a/b/c.json
 * </pre>
 * would be handled by subsequent RequestFilters and Resources as:
 * <pre>
 *      http://www.example.com/a/b/c
 * </pre>
 * Also this filter overrides the {@link ClientPreferences#acceptedMediaRanges()} 
 * of the request with the media range corresponding to the extension.
 *       
 */
final class ExtensionFilter implements Function<Request,Request> {
    private static final ExtensionFilter DEFAULT = ExtensionFilter.newInstance(ExtensionMap.DEFAULT_EXTENSIONS);

    private static final Pattern EXT_PATTERN = Pattern.compile("(.*?)(\\.([^\\.]+$))?");

    /**
     * Returns an ExtensionFilter instance using the default ExtensionMap.
     */
    public static ExtensionFilter getDefaultInstance() {
        return DEFAULT;
    }

    /**
     * Returns an ExtensionFilter backed by {@code extensionMap}.
     * @param extensionMap
     * @return The ExtensionFilter.
     * @throws NullPointerException If {@code extensionMap} is null.
     */
    public static ExtensionFilter newInstance(final ExtensionMap extensionMap) {
        Preconditions.checkNotNull(extensionMap);
        return new ExtensionFilter(extensionMap);
    }

    private final ExtensionMap extensionMap;

    ExtensionFilter(final ExtensionMap extensionMap) {
        this.extensionMap = extensionMap;
    }

    /* (non-Javadoc)
     * @see restlib.RequestFilter#apply(restlib.Request)
     */
    public Request apply(final Request request) {
        final Uri uri = request.uri();
        final Path path = uri.path();
        final int pathSize = Iterables.size(path.segments());
        final String lastPathSegment = Iterables.getLast(path.segments(), "");

        
        final Matcher m = EXT_PATTERN.matcher(lastPathSegment);
        if (!m.matches()) {
            return request;
        }

        final String newLastPathSegment = Strings.nullToEmpty(m.group(1));
        final String extension = Strings.nullToEmpty(m.group(3));
        
        if (extension.isEmpty()) {
            return request;
        }
        
        final Path newPath = 
                Path.copyOf(
                        ImmutableList.<String> builder()
                            .addAll(Iterables.limit(path.segments(), pathSize - 1))
                            .add(newLastPathSegment)
                            .build());
        
        final Optional<MediaRange> contentType = this.extensionMap.getMediaRange(extension);

        if (contentType.isPresent()) {
            final Uri filteredUri =
                    Uri.builder()
                        .setScheme(uri.scheme())
                        .setAuthority(uri.authority())
                        .setPath(newPath)
                        .setQuery(uri.query())
                        .build();

            return new RequestWrapper(request) {
                private final Set<Preference<MediaRange>> acceptedMediaRanges = ImmutableSet.of(Preference.create(contentType.get(), 1));

                private final ClientPreferences preferences = new ClientPreferencesWrapper(request.preferences()) {
                    @Override
                    public Set<Preference<MediaRange>> acceptedMediaRanges() {
                        return acceptedMediaRanges;
                    }
                };

                @Override
                public ClientPreferences preferences() {
                    return this.preferences;
                }

                @Override
                public Uri uri() {
                    return filteredUri;
                }
            };
        }

        return request;
    }
}
