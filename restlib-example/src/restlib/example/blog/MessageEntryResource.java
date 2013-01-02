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


package restlib.example.blog;

import restlib.Request;
import restlib.Response;
import restlib.data.ExtensionMap;
import restlib.data.MediaRange;
import restlib.data.Status;
import restlib.example.blog.dao.BlogEntry;
import restlib.example.blog.dao.BlogEntryBuilder;
import restlib.example.blog.dao.BlogEntryHelpers;
import restlib.example.blog.dao.BlogStore;
import restlib.example.blog.serializable.MessageEntry;
import restlib.server.FutureResponses;
import restlib.server.Route;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class MessageEntryResource extends AtomEntryResource<MessageEntry>{
    public static final class Builder {
        private Iterable<MediaRange> acceptedMediaRanges = ImmutableList.of();
        private BlogStore blogStore;
        private ExtensionMap extensionMap = ExtensionMap.DEFAULT_EXTENSIONS;
        private Route route = Route.NONE;
        private Iterable<MediaRange> supportedMediaRanges = ImmutableList.of();     
        
        public MessageEntryResource build() {
            return new MessageEntryResource(this);
        }
        
        public Builder setAcceptedMediaRanges(final Iterable<MediaRange> mediaRanges) {
            Preconditions.checkNotNull(mediaRanges);
            this.acceptedMediaRanges = ImmutableList.copyOf(mediaRanges);
            return this;
        }
        
        public Builder setBlogStore(final BlogStore blogStore) {
            Preconditions.checkNotNull(blogStore);
            this.blogStore = blogStore;
            return this;
        }
        
        public Builder setExtensionMap(final ExtensionMap extensionMap) {
            Preconditions.checkNotNull(extensionMap);
            this.extensionMap = extensionMap;
            return this;
        }

        public Builder setRoute(final Route route) {
            Preconditions.checkNotNull(route);
            this.route = route;
            return this;
        }

        public Builder setSupportedMediaRanges(final Iterable<MediaRange> mediaRanges) {
            Preconditions.checkNotNull(mediaRanges);
            this.supportedMediaRanges = ImmutableList.copyOf(mediaRanges);
            return this;
        }   
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private final Iterable<MediaRange> acceptedMediaRanges;
    private final BlogStore blogStore;
    private final ExtensionMap extensionMap;
    private final Route route;
    private final Iterable<MediaRange> supportedMediaRanges;
    
    private MessageEntryResource(final Builder builder) {
        super(MessageEntry.class);
        this.acceptedMediaRanges = builder.acceptedMediaRanges;
        this.blogStore = builder.blogStore;
        this.extensionMap = builder.extensionMap;
        this.route = builder.route;
        this.supportedMediaRanges = builder.supportedMediaRanges;
    }

    @Override
    public Iterable<MediaRange> acceptedMediaRanges() {
        return acceptedMediaRanges;
    }

    @Override
    protected ExtensionMap extensionMap() {
        return extensionMap;
    }
    
    @Override
    protected ListenableFuture<Response> delete(final Request request) {
        final BlogEntryBuilder builder = BlogEntryBuilder.newInstance();
        this.route().populateObject(request.uri().path(), builder);
        final BlogEntry requestedBlogEntry = builder.build();
        
        if (this.blogStore.getEntry(requestedBlogEntry) != null) {
            this.blogStore.deleteEntry(requestedBlogEntry);
            return FutureResponses.SUCCESS_NO_CONTENT;
        }
        
        return FutureResponses.CLIENT_ERROR_NOT_FOUND;
    }

    @Override
    protected ListenableFuture<Response> get(final Request request) {
        final BlogEntryBuilder builder = BlogEntryBuilder.newInstance();
        this.route().populateObject(request.uri().path(), builder);
        final BlogEntry requestedBlogEntry = builder.build();

        final BlogEntry entry = this.blogStore.getEntry(requestedBlogEntry);
        
        if (entry != null) {
            final MessageEntry message =
                    BlogEntryHelpers.toMessageEntry(
                            entry, 
                            this.getId(request.uri(), entry.getCreated()), 
                            this.getLinks(request.uri()));
        
            return Futures.immediateFuture(
                        Response.builder()
                            .setStatus(Status.SUCCESS_OK)
                            .setEntity(message).build());
        }

        return FutureResponses.CLIENT_ERROR_NOT_FOUND;
    }

    @Override
    protected ListenableFuture<Response> put(final Request request, final MessageEntry message) {
        final String id = this.route().getParameters(request.uri().path()).get("id");        
        final BlogEntry requestedBlogEntry = BlogEntryHelpers.fromMessageEntry(message, id);           
        
        if (this.blogStore.getEntry(requestedBlogEntry) != null) {
            this.blogStore.updateEntry(requestedBlogEntry);
            final BlogEntry entry = this.blogStore.getEntry(requestedBlogEntry);
            final MessageEntry messageEntry = 
                    BlogEntryHelpers.toMessageEntry(
                            entry,
                            this.getId(request.uri(), entry.getCreated()),
                            this.getLinks(request.uri()));

            return Futures.immediateFuture(
                    Response.builder()
                        .setStatus(Status.SUCCESS_OK)
                        .setEntity(messageEntry)
                        .build());
        }
        
        return FutureResponses.CLIENT_ERROR_NOT_FOUND;
    }

    @Override
    public Route route() {
        return route;
    }

    @Override
    public Iterable<MediaRange> supportedMediaRanges() {
        return supportedMediaRanges;
    }
}
