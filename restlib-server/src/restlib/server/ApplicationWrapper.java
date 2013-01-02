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

import restlib.Request;
import restlib.Response;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * An Application which forwards all its method calls to another Application.
 * Subclasses should override one or more methods to modify the behavior of the
 * backing Application as desired per the decorator pattern.
 */
public class ApplicationWrapper implements Application {
    private final Application delegate;
    
    protected ApplicationWrapper(final Application delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }
    
    public Function<Request, Request> requestFilter() {
        return this.delegate.requestFilter();
    }

    public Function<Response, Response> responseFilter() {
        return this.delegate.responseFilter();
    }
    
    public Resource getResource(final Request request) {
        return this.delegate.getResource(request);
    }
}
