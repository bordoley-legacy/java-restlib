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


package restlib.bio.multipart;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

public final class BodyPartInputStream extends FilterInputStream { 
    private static final byte[] FIELD_NAME_BOUNDARY = ":".getBytes(Charsets.US_ASCII);
    private static final byte[] HEADER_BOUNDARY = "\r\n\r\n".getBytes(Charsets.US_ASCII);
    private static final byte[] NEW_LINE_BOUNDARY = "\r\n".getBytes(Charsets.US_ASCII);
    
    static BodyPartInputStream wrap(final InputStream is) throws IOException {
        final BoundaryInputStream bis = BoundaryInputStream.wrap(is, HEADER_BOUNDARY);
        final ImmutableList.Builder<Entry<String, String>> builder =
                ImmutableList.builder();
        
        while (!bis.boundaryFound() ) {            
            final BoundaryInputStream headerLineStream = 
                    BoundaryInputStream.wrap(bis, NEW_LINE_BOUNDARY);
            
            final BoundaryInputStream fieldNameStream =
                    BoundaryInputStream.wrap(headerLineStream, FIELD_NAME_BOUNDARY);
            
            final String fieldName = 
                    CharStreams.toString(
                            new InputStreamReader(fieldNameStream, Charsets.US_ASCII));
            
            if (fieldName.isEmpty()) { break;}
            
            final String fieldBody = 
                    CharStreams.toString(
                            new InputStreamReader(
                                    fieldNameStream.getRemainingStream(), 
                                    Charsets.US_ASCII));
            builder.add(
                    new SimpleImmutableEntry<String, String>(fieldName, fieldBody));
        }  
        return new BodyPartInputStream(builder.build(), bis.getRemainingStream());
    }
    
    private final List<Entry<String, String>> headers;
    
    private BodyPartInputStream (
            final List<Entry<String, String>> headers,
            final InputStream is) throws IOException {
        super(is);
        
        this.headers = headers;
    }

    public Iterable<Entry<String, String>> headers() {
        return this.headers;
    }
}
