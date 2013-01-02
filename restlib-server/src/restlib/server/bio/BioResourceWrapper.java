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


package restlib.server.bio;

import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.server.ResourceWrapper;

public class BioResourceWrapper<T> extends ResourceWrapper implements BioResource<T> {  
    private final BioResource<T> delegate;
    
    protected BioResourceWrapper(final BioResource<T> delegate) {
        super(delegate);
        this.delegate = delegate; // NPE check done by call to super();
    }

    public InputStreamDeserializer<T> getRequestEntityDeserializer(final Request request) {
        return this.delegate.getRequestEntityDeserializer(request);
    }

    public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
       return this.delegate.getResponseEntitySerializer(request, response);
    }
}
