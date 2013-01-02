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


package restlib.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import restlib.test.AbstractValueObjectTest;

import com.google.common.collect.ImmutableList;

public final class CacheDirectiveTest extends AbstractValueObjectTest<CacheDirective> {   
    @Test (expected = IllegalArgumentException.class)
    public void maxAge_withNegativeAge() {
        CacheDirective.maxAge(-1);
    }
    
    @Test
    public void maxAge_withValidAge() {
        final CacheDirective directive = CacheDirective.maxAge(10);
        assertEquals("max-age=10", directive.toString());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void maxStale_withNegativeAge() {
        CacheDirective.maxAge(-1);
    }
    
    @Test
    public void maxStale_withValidAge() {
        final CacheDirective directive = CacheDirective.maxStale(10);
        assertEquals("max-stale=10", directive.toString());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void minFresh_withNegativeTime() {
        CacheDirective.minFresh(-10);
    }
    
    @Test
    public void minFresh_withValidAge() {
        final CacheDirective directive = CacheDirective.minFresh(10);
        assertEquals("min-fresh=10", directive.toString());
    }
    
    @Test (expected = NullPointerException.class)
    public void noCache_withNull() {
        CacheDirective.noCache(null);
    }
    
    @Test
    public void noCache_withEmpty() {
        final CacheDirective directive = 
                CacheDirective.noCache(ImmutableList.<Header> of());
        assertEquals(CacheDirective.NO_CACHE, directive);
    }
    
    @Test
    public void noCache_withHeaders() {
        final CacheDirective directive = 
                CacheDirective.noCache(
                        ImmutableList.<Header> of(HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION));
        assertEquals("no-cache=\"Accept, Authorization\"", directive.toString());
    }
    
    @Test
    public void privateInfo_withEmpty() {
        final CacheDirective directive = 
                CacheDirective.privateInfo(ImmutableList.<Header> of());
        assertEquals(CacheDirective.PRIVATE, directive);
    }
    
    @Test
    public void privateInfo_withHeaders() {
        final CacheDirective directive = 
                CacheDirective.privateInfo(
                        ImmutableList.<Header> of(HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION));
        assertEquals("private=\"Accept, Authorization\"", directive.toString());
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void sharedMaxAge_withNegativeMaxAge() {
        CacheDirective.sharedMaxAge(-10);
    }
    
    @Test
    public void sharedMaxAge_withValidAge() {
        final CacheDirective directive = CacheDirective.sharedMaxAge(10);
        assertEquals("s-maxage=10", directive.toString());
    }

    @Override
    protected List<CacheDirective> newTestSet() {
        return ImmutableList.of(
                CacheDirective.NONE, 
                
                // NamedCacheDirective
                CacheDirective.MAX_STALE,
                CacheDirective.NO_CACHE,
                
                // DeltaTimeCacheDirectives
                CacheDirective.sharedMaxAge(10),
                CacheDirective.sharedMaxAge(15),
                CacheDirective.minFresh(15),
                CacheDirective.minFresh(5),
                
                // FieldsCacheDirective
                CacheDirective.privateInfo(
                        ImmutableList.<Header> of(HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION)),
                CacheDirective.privateInfo(
                        ImmutableList.<Header> of(HttpHeaders.ACCEPT)),
                CacheDirective.noCache(
                        ImmutableList.<Header> of(HttpHeaders.ACCEPT)),         
                CacheDirective.noCache(
                        ImmutableList.<Header> of(HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION)),
                        
                // NameValueCacheDirective     
                CacheDirective.create("test1", "cool"),
                CacheDirective.create("test2", "cool"),  
                CacheDirective.create("test1", "test another line"),
                CacheDirective.create("test2", "test another line"));
    }
}
 