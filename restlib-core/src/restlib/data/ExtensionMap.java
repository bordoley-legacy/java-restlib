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


import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;

public final class ExtensionMap {
    public static ExtensionMap DEFAULT_EXTENSIONS =
                    builder()
                        .put(MediaRanges.APPLICATION_ATOM, "atom")
                        .put(MediaRanges.APPLICATION_JSON, "json")
                        .put(MediaRanges.APPLICATION_XML, "xml")
                        .put(MediaRanges.TEXT_PLAIN, "txt")
                        .put(MediaRanges.TEXT_HTML, "html")
                        .build();
    
    public static ExtensionMapBuilder builder() {
        return new ExtensionMapBuilder();
    }
    
    private final BiMap<MediaRange, String> mediaRangeExtMap;
    
    ExtensionMap(final BiMap<MediaRange, String> mediaRangeExtMap) {
        this.mediaRangeExtMap = mediaRangeExtMap;
    }
    
    public Optional<String> getExtension(final MediaRange mediaRange) {
        Preconditions.checkNotNull(mediaRange);
        return Optional.fromNullable(mediaRangeExtMap.get(mediaRange));
    }
    
    public Optional<MediaRange> getMediaRange(final String ext) {
        Preconditions.checkNotNull(ext);
        return Optional.fromNullable(mediaRangeExtMap.inverse().get(ext));
    }
}