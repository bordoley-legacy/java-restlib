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

import java.util.Map;

import restlib.net.Path;

final class ExcludingRoute extends Route {
    private final Route delegate;
    private final Route excluded;
    
    ExcludingRoute(final Route delegate, final Route excluded) {
        this.delegate = delegate;
        this.excluded = excluded;
    }

    @Override
    public Map<String, String> getParameters(final Path path) {
        if (excluded.match(path)) {
            throw new IllegalArgumentException();
        }
        
        return delegate.getParameters(path);
    }

    @Override
    public Path objectToPath(final Object obj) {
        return delegate.objectToPath(obj);
    }
}
