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
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import restlib.net.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class RailsRouteTest {
    public static final String globRoute = "/a/*b/c/:d/*e";
    public static final String glogPattern = "/a/(.)";
    public static final String parametersRoute = "/a/:b/c/:d";
    
    public static final String pathRoute = "/a/b/c/d";
    
 
    @Test
    public void getParameters() {
        final ImmutableMap<String, String> tests =
                ImmutableMap.<String, String> builder()
                    .put("/a/:b/*c/:g/:h/i", "/a/b/c/d/e/f/g/h/i")
                    .put("/a/:b/c/:d", "/a/b/c/d")
                    .put("/a/:b/c/:d/", "/a/b/c/d/")
                    .put("/a/*b", "/a/b/c/d/e/f/g/h/i")
                    .build();
        
        for (final Map.Entry<String, String> test : tests.entrySet()) {
            final Route route = Route.parse(test.getKey());          
            final Path path = Path.parse(test.getValue());
            
            System.out.println(route.getParameters(path));
        }
    }
    
    @Test
    public void match() {
        final ImmutableMap<String, String> tests =
                ImmutableMap.<String, String> builder()
                    .put("/a/:b/*c/:g/:h/i", "/a/b/c/d/e/f/g/h/i")
                    .put("/a/:b/c/:d", "/a/b/c/d")
                    .put("/a/:b/c/:d/", "/a/b/c/d/")
                    .put("/a/*b", "/a/b/c/d/e/f/g/h/i")
                    .put("/a/b/c/d", "/a/b/c/d")
                    //.put("/a/b/c/*d", "/a/b/c")
                    .build();
        
        for (final Map.Entry<String, String> test : tests.entrySet()) {
            final Route route = Route.parse(test.getKey());          
            final Path path = Path.parse(test.getValue());
            
            assertTrue(route + " " + path, route.match(path));
        }
    }
    
    @Test
    public void parse_withIllegalRoutes() {
        final Iterable<String> routes =
                ImmutableList.of(
                        "/a/*b/:c/:d/*c/e",
                        "/a/*b/*d",
                        "/a/*b/c/*b",
                        "/a/*b/c/:b");
        
        for(final String route : routes)
            try{
                Route.parse(route);
                fail(route);
            } catch (final IllegalArgumentException e) {}
     }
    
    @Test
    public void parse_withValidRoutes() {
        final Iterable<String> routes =
                ImmutableList.of(
                        "/a/*b/:c/:d/e/*f",
                        "/a/c/d/e",
                        "/a/*b/:c/:d");
        
        for(final String route : routes)
            Route.parse(route);
     }
}
