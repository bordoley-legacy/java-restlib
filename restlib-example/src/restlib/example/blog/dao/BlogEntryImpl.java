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

final class BlogEntryImpl implements BlogEntry {
    private final String id;
    private final long created;
    private final long updated;
    private final String content;

    BlogEntryImpl(final BlogEntryBuilder builder){
        this.id = builder.id;
        this.created = builder.created;
        this.updated = builder.updated;
        this.content = builder.content;
    }
    
    @Override
    public String getId(){
        return id;
    }
    
    @Override
    public long getCreated(){
        return created;
    }
    
    @Override
    public long getUpdated(){
        return updated;
    }
    
    @Override
    public String getContent(){
        return content;
    }
}
