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


package restlib.ext.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;

import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.net.IRI;
import restlib.net.Uri;
import restlib.serializable.atom.AtomDate;

final class Deserializers {     
    public static final JsonDeserializer<Language> LANGUAGE_DESERIALIZER = 
            new JsonDeserializer<Language>() {
                @Override
                public Language deserialize(final JsonParser jp, final DeserializationContext ctx) 
                        throws IOException, JsonProcessingException {
                    try {
                        return Language.create(jp.getText());
                    } catch (final IllegalArgumentException e) {
                        throw new JsonMappingException("Failed to parse uri: " + jp.getText(), e);
                    }
                }};
                
    public static final JsonDeserializer<MediaRange> MEDIA_RANGE_DESERIALIZER = 
            new JsonDeserializer<MediaRange>() {
                @Override
                public MediaRange deserialize(final JsonParser jp, final DeserializationContext ctx) 
                        throws IOException, JsonProcessingException {
                    try {
                        return MediaRange.parse(jp.getText());
                    } catch (final IllegalArgumentException e) {
                        throw new JsonMappingException("Failed to parse uri: " + jp.getText(), e);
                    }
                }};
                
    public static final JsonDeserializer<IRI> IRI_DESERIALIZER = 
            new JsonDeserializer<IRI>() {
                @Override
                public IRI deserialize(final JsonParser jp, final DeserializationContext ctx) 
                        throws IOException, JsonProcessingException {
                    try {
                        return IRI.parse(jp.getText());
                    } catch (final IllegalArgumentException e) {
                        throw new JsonMappingException("Failed to parse uri: " + jp.getText(), e);
                    }
                }};

    public static final JsonDeserializer<Uri> URI_DESERIALIZER = 
            new JsonDeserializer<Uri>() {
                @Override
                public Uri deserialize(final JsonParser jp, final DeserializationContext ctx) 
                        throws IOException, JsonProcessingException {
                    try {
                        return Uri.parse(jp.getText());
                    } catch (final IllegalArgumentException e) {
                        throw new JsonMappingException("Failed to parse uri: " + jp.getText(), e);
                    }
                }};
                
    public static final JsonDeserializer<AtomDate> ATOM_DATE_DESERIALIZER =
            new JsonDeserializer<AtomDate>() {
                @Override
                public AtomDate deserialize(final JsonParser jp, final DeserializationContext ctx) 
                        throws IOException, JsonProcessingException {
                    try {
                        return AtomDate.parse(jp.getText());
                    } catch (final IllegalArgumentException e) {
                        throw new JsonMappingException("Failed to parse uri: " + jp.getText(), e);
                    }
                }};
                
    private Deserializers(){}           
}
