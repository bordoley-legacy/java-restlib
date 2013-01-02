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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import restlib.Request;
import restlib.data.MediaRange;
import restlib.data.MediaRanges;
import restlib.data.Preference;
import restlib.net.Uri;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class ExtensionFilterTest {
    private static final Iterable<MediaRange> availableMediaRanges =
            ImmutableList.<MediaRange> builder()
                .add(MediaRanges.APPLICATION_JSON)
                .add(MediaRanges.APPLICATION_ATOM)
                .add(MediaRanges.APPLICATION_XML)
                .build();
    
    private static final Map<Uri,Uri> URI_WITH_EXTENSION =
            ImmutableMap.<Uri,Uri> builder()
                .put(Uri.parse("http://www.example.org/a.json"), 
                        Uri.parse("http://www.example.org/a"))
                .put(Uri.parse("http://www.example.org/.json"),
                         Uri.parse("http://www.example.org/"))    
                .put(Uri.parse("http://www.example.org/a/b/c.json"),
                        Uri.parse("http://www.example.org/a/b/c"))
                .put(Uri.parse("http://www.example.org/a/b/c.d.json"),
                        Uri.parse("http://www.example.org/a/b/c.d"))          
                .put(Uri.parse("http://www.example.org/a/b/c.json?d=e&f=g"),
                        Uri.parse("http://www.example.org/a/b/c?d=e&f=g"))            
                .build();
    
    private static final Iterable<Uri> URI_WITHOUT_EXTENSION =
            ImmutableList.<Uri> builder()
                .add(Uri.parse("http://www.example.org/a.ext"))
                .add(Uri.parse("http://www.exapmle.ort/a.json/"))
                .add(Uri.parse("http://www.exapmle.ort/a."))
                .add(Uri.parse("http://www.exapmle.ort/a./"))
                .build();


    @Test(expected = NullPointerException.class)
    public void newInstance_nullExtensionMap() {
        ExtensionFilter.newInstance(null);
    }

    @Test
    public void apply_uriWithExtension() {      
        for (final Uri uri : URI_WITH_EXTENSION.keySet()) {
            final Request request = Request.builder().setUri(uri).build();
            final Request filteredRequest = ExtensionFilter.getDefaultInstance().apply(request);
            
            assertTrue(uri.toString(), filteredRequest.uri().equals(URI_WITH_EXTENSION.get(uri)));
            assertTrue(
                    Preference.<MediaRange> bestMatch(
                            filteredRequest.preferences().acceptedMediaRanges(), 
                            availableMediaRanges).get().equals(MediaRanges.APPLICATION_JSON));
        }
    }
    
    @Test
    public void apply_uriWithOutExtension() {      
        for (final Uri uri : URI_WITHOUT_EXTENSION) {
            final Request request = Request.builder().setUri(uri).build();
            final Request filteredRequest = ExtensionFilter.getDefaultInstance().apply(request);
            
            assertTrue(request.equals(filteredRequest));
        }
    }
}
