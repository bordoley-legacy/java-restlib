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

import restlib.impl.BeanUtils;
import restlib.net.Path;

import com.google.common.base.Preconditions;

public abstract class Route {
    public static final Route NONE = new Route() {
        public boolean equals(final Object obj) {
            return this == obj;
        }
        
        @Override
        public Map<String, String> getParameters(final Path path) {
            throw new IllegalArgumentException("Path does not match route");
        }
        
        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean match(final Path path) {
            return false;
        }

        @Override
        public Path objectToPath(final Object obj) {
            return Path.of();
        }
        
        @Override
        public void populateObject(final Path path, final Object obj) {
            // Do nothing
        }

        @Override
        public String toString() {
            return "";
        }
    };
    
    static Route compose(final Route first, final Route second) {
        return new ComposableRoute(first, second); 
    }
    
    public static Route parse(final String route) {
        final Path routeObj = Path.parse(route);
        Preconditions.checkArgument(routeObj.isUriPath());
        return new RailsRoute(routeObj.canonicalize());
    }
    
    public static Route startsWith(final String route) {
       final Route parsed = Route.parse(route);
       if (parsed instanceof RailsRoute) {
          return compose(parsed, Route.parse(parsed.toString() + "/*")); 
       } else {
           throw new IllegalArgumentException();
       }
    }
    
    Route(){}
    
    public final Route exclude(final Route excluded) {
        return new ExcludingRoute(this, excluded);
    }
    
    /**
     * Returns a {@code java.util.Map} including all parameters encoded in
     * the path segment of {@code uri}.
     * @throws NullPointerException If {@code uri} is {@code null}.
     * @throws IllegalArgumentException if {@code uri} does not match this route.
     */
    public abstract Map<String, String> getParameters(final Path path);
    
    /**
     * Used to determine if this Route pattern matches the path in {@code uri}.
     * @param uri The {@code Uri} to match against. 
     * @return True if this {@code Route} matches the URI path. Otherwise false.
     */
    public boolean match(final Path path) {
        try {
            getParameters(path);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * @param base
     * @param obj
     * @return
     */
    public abstract Path objectToPath(final Object obj);
    
    /**
     * Attempts to populate the object {@code obj} via reflection
     * with the parameters in the Uri Path using Java bean setXXX functions.
     * 
     * @param uri
     * @param obj
     * @throws NullPointerException If either {@code uri} or {@code obj} are null.
     */
    public void populateObject(final Path path, final Object obj) {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(obj);
        BeanUtils.populateObject(getParameters(path), obj);
    }
}
