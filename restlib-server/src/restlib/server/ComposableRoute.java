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

final class ComposableRoute extends Route {
    private final Route first;
    private final Route second;

    ComposableRoute(final Route first, final Route second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Map<String, String> getParameters(final Path path) {
        try {
            return first.getParameters(path);
        } catch (final IllegalArgumentException e) {
        }

        return second.getParameters(path);
    }

    @Override
    public boolean match(final Path path) {
        return first.match(path) ||
                second.match(path);
    }

    @Override
    public Path objectToPath(final Object obj) {
        try {
            return first.objectToPath(obj);
        } catch (final IllegalArgumentException e) {
        }

        return second.objectToPath(obj);
    }

    @Override
    public void populateObject(Path path, Object obj) {
        try {
            first.populateObject(path, obj);
            return;
        } catch (final IllegalArgumentException e) {
        }

        second.populateObject(path, obj);
    }
}
