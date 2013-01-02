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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import restlib.impl.BeanUtils;
import restlib.net.Path;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

final class RailsRoute extends Route {    
    private static boolean isGlob(final String segment) {
        return segment.startsWith("*");
    }
    
    private static boolean isParameter(final String segment) {
        return segment.startsWith(":");
    }
    
    private static String key(final String segment) {
        if (isParameter(segment)) {
            Preconditions.checkArgument(segment.length() > 1);
        }
        return segment.substring(1, segment.length());
    }
    
    private final Path route;
    
    RailsRoute(final Path route) {
        this.route = checkRoute(route);
    }

    private Path checkRoute(final Path route) {
        Preconditions.checkNotNull(route);
        
        final Set<String> parameters = Sets.newHashSet();

        boolean nextSegmentGlobAllowed = true;        
        for (final String segment : route.segments()) {
            if (segment.isEmpty()) { 
                continue; 
            } else if (isParameter(segment)) {
                final String key = key(segment);
                Preconditions.checkArgument(!key.isEmpty(), route);
                Preconditions.checkArgument(!parameters.contains(key), route);            
                parameters.add(key);
            } else if (isGlob(segment)) {
                final String key = key(segment);
                if (!key.isEmpty()) {
                    Preconditions.checkArgument(!parameters.contains(key), route);            
                    parameters.add(key);
                }
                
                // Only one glob segment is allowed between each concrete segment
                Preconditions.checkArgument(nextSegmentGlobAllowed, route);
                nextSegmentGlobAllowed = false;         
            } else {
                // concrete segment, reset glob allowed
                nextSegmentGlobAllowed = true;
            }  
        }
        
        return route;
    }
    
    @Override
    public Map<String, String> getParameters(final Path path) {
        Preconditions.checkNotNull(path); 
        final ImmutableMap.Builder<String,String> builder = ImmutableMap.builder();
        
        final Iterator<String> pathSegments = path.canonicalize().segments().iterator();
        final Iterator<String> routeSegments = route.segments().iterator();
        
        while(routeSegments.hasNext() && pathSegments.hasNext()) {
            final String routeSegment = routeSegments.next();
            final String pathSegment = pathSegments.next();
            
            if (isParameter(routeSegment)) {
                builder.put(key(routeSegment), pathSegment);
            } else if (isGlob(routeSegment)) {
                String stopSegment = "";   
                int parameterCount = 0;
                
                while(routeSegments.hasNext()) {
                    final String nextRouteSegment = routeSegments.next();
                    
                    if (!isParameter(nextRouteSegment)) {
                        stopSegment = nextRouteSegment;
                        break;
                    }
                    
                    parameterCount++;
                }            
                
                final List<String> globSegments = 
                        Lists.newArrayList();              
                globSegments.add(".");
                
                while(pathSegments.hasNext()) {
                    final String segment = pathSegments.next();
                    if (segment.equals(stopSegment)) { break; }                 
                    globSegments.add(segment);
                }
                
                // Only add if the key is not empty
                final String key = key(routeSegment);
                if (!key.isEmpty()) {
                    builder.put(key, 
                        Path.copyOf(globSegments.subList(0, globSegments.size() - parameterCount)).toString());              
                }
            } else {
                Preconditions.checkArgument(routeSegment.equals(pathSegment));
            }
        }
        
        Preconditions.checkArgument(!pathSegments.hasNext());  
        return builder.build();
    }
    
    @Override
    public Path objectToPath(final Object object) {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        
        for (final String segment : route.segments()) {
            if (isParameter(segment) || isGlob(segment)) {
                final String key = key(segment);
                try {
                    builder.add(BeanUtils.getPropertyAsString(object, key));
                } catch (final IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                } catch (final InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                } catch (final NoSuchMethodException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                builder.add(segment);
            }
        }
        return Path.copyOf(builder.build());
    }
    
    @Override
    public String toString() {
        return route.toString();
    }
}
