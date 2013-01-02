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


package restlib.example.blog.dao;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public final class BlogStore {
    private Map<String, BlogEntry> entries = Maps.newConcurrentMap();
    
    public void deleteEntry(final BlogEntry entry) {
        Preconditions.checkNotNull(entry);
        if (this.entries.containsKey(entry.getId())) {
            this.entries.remove(entry.getId());
        }
    }
    
    public BlogEntry getEntry(final BlogEntry entry) {
        Preconditions.checkNotNull(entry);
        if (this.entries.containsKey(entry.getId())) {
            return this.entries.get(entry.getId());
        }
        return null;
    }
    
    public Iterable<BlogEntry> getAllEntries() {
        return this.entries.values();
    }
    
    public void updateEntry(final BlogEntry entry){
        Preconditions.checkNotNull(entry);
        this.entries.put(entry.getId(), entry);
    }
    
    public BlogEntry createEntry(final BlogEntry entry) {
        Preconditions.checkNotNull(entry);
        final String key = Double.toHexString(Math.random());
        final BlogEntry newEntry = 
            BlogEntryBuilder.newInstance()
                .setId(key)
                .setCreated(System.currentTimeMillis())
                .setUpdated(System.currentTimeMillis())
                .setContent(entry.getContent())
                .build();
        this.entries.put(key, newEntry);
        return newEntry;
    }
}
