/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License"));
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

import static restlib.data.MediaRange.parse;
import static restlib.data.MediaRange.register;

public final class MediaRanges {
    public static final MediaRange APPLICATION_ANY = 
            register(parse("application/*"));
    public static final MediaRange APPLICATION_ATOM = 
            register(parse("application/atom+xml"));
    public static final MediaRange APPLICATION_ATOM_ENTRY = 
            register(parse("application/atom+xml;type=entry"));
    public static final MediaRange APPLICATION_ATOM_FEED = 
            register(parse("application/atom+xml;type=feed"));
    public static final MediaRange APPLICATION_JSON = 
            register(parse("application/json"));
    public static final MediaRange APPLICATION_JSON_ENTRY = 
            register(parse("application/json;type=entry"));
    public static final MediaRange APPLICATION_JSON_FEED = 
            register(parse("application/json;type=feed"));
    public static final MediaRange APPLICATION_OCTET_STREAM = 
            register(parse("application/octet-stream"));
    public static final MediaRange APPLICATION_OPEN_SEARCH_DESCRIPTION_XML =
            register(parse("application/opensearchdescription+xml"));
    public static final MediaRange APPLICATION_WWW_FORM = 
            register(parse("application/x-www-form-urlencoded"));
    public static final MediaRange APPLICATION_XML = 
            register(parse("application/xml"));
    public static final MediaRange GOOGLE_PROTOCOL_BUFFER = 
            register(parse("application/x-protobuf"));
    public static final MediaRange MULTIPART_FORM_DATA = 
            register(parse("multipart/form-data"));
    public static final MediaRange MULTIPART_RELATED = 
            register(parse("multipart/related"));
    public static final MediaRange TEXT_ANY = 
            register(parse("text/*"));
    public static final MediaRange TEXT_HTML = 
            register(parse("text/html"));
    public static final MediaRange TEXT_HTML_ENTRY = 
            register(parse("text/html;type=entry"));
    public static final MediaRange TEXT_HTML_FEED = 
            register(parse("text/html;type=feed"));
    public static final MediaRange TEXT_PLAIN = 
            register(parse("text/plain"));    
    private MediaRanges(){}
}
