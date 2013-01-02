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

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;

import restlib.Request;
import restlib.Response;

/**
 * A Resource which forwards all its method calls to another Resource.
 * Subclasses should override one or more methods to modify the behavior of the
 * backing Resource as desired per the decorator pattern.
 */
public class ResourceWrapper implements Resource {
    private final Resource delegate;
    
    /**
     * @param delegate
     * @throws NullPointerException
     */
    protected ResourceWrapper(final Resource delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }
    
    public ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
        return this.delegate.acceptMessage(request, message);
    }

    public ListenableFuture<Response> handle(final Request request) {
        return this.delegate.handle(request);
    }

    public Route route() {
        return this.delegate.route();
    }
}
