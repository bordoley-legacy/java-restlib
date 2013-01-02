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

import com.google.common.base.Preconditions;

public final class BlogEntryBuilder {
    String content = "";
    long created = System.currentTimeMillis();
    String id = "";
    long updated = System.currentTimeMillis();
    
    public static BlogEntryBuilder newInstance() {
        return new BlogEntryBuilder();
    }

    private BlogEntryBuilder(){
    }
    
    public BlogEntry build() {
        return new BlogEntryImpl (this);
    }
    
    public BlogEntryBuilder setContent(final String content) {
        Preconditions.checkNotNull(content);
        this.content = content;
        return this;
    }
    
    public BlogEntryBuilder setCreated(final long created) {
        this.created = created;
        return this;
    }
    
    public BlogEntryBuilder setId(final String id) {
        Preconditions.checkNotNull(id);
        this.id = id;
        return this;
    }

    public BlogEntryBuilder setUpdated(final long updated) {
        this.updated = updated;
        return this;
    }
}
