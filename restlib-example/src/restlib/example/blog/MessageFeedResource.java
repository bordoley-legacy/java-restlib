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
import restlib.example.blog.dao.BlogEntryHelpers;
import restlib.example.blog.dao.BlogStore;
import restlib.example.blog.serializable.MessageEntry;
import restlib.example.blog.serializable.MessageFeed;
import restlib.net.Path;
import restlib.net.Uri;
import restlib.server.Route;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class MessageFeedResource extends AtomFeedResource<MessageFeed, MessageEntry>{
    public static final class Builder {
        private Iterable<MediaRange> acceptedMediaRanges = ImmutableList.of();
        private MessageEntryResource atomEntryResource;
        private BlogStore blogStore;
        private ExtensionMap extensionMap = ExtensionMap.DEFAULT_EXTENSIONS;
        private Route route = Route.NONE;
        private Iterable<MediaRange> supportedMediaRanges = ImmutableList.of();
        
        private Builder(){}
        
        public MessageFeedResource build() {
            return new MessageFeedResource(this);
        }
        
        public Builder setAcceptedMediaRanges(final Iterable<MediaRange> mediaRanges) {
            Preconditions.checkNotNull(mediaRanges);
            this.acceptedMediaRanges = ImmutableList.copyOf(mediaRanges);
            return this;
        }
        
        public Builder setAtomEntryResource(final MessageEntryResource atomEntryResource) {
            Preconditions.checkNotNull(atomEntryResource);
            this.atomEntryResource = atomEntryResource;
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
            this.supportedMediaRanges= ImmutableList.copyOf(mediaRanges);
            return this;
        }       
    }
    
    public static Builder builder() {
        return new Builder();
    }

    private final Iterable<MediaRange> acceptedMediaRanges;
    private final MessageEntryResource atomEntryResource;
    private final BlogStore blogStore;
    private final ExtensionMap extensionMap;
    private final Route route;
    private final Iterable<MediaRange> supportedMediaRanges;
    
    private MessageFeedResource(final Builder builder) {
        super(MessageFeed.class);
        this.acceptedMediaRanges = builder.acceptedMediaRanges;
        this.atomEntryResource = builder.atomEntryResource;
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
    protected MessageEntryResource atomEntryResource() {
        return atomEntryResource;
    }

    @Override
    protected ExtensionMap extensionMap() {
        return extensionMap;
    }

    private Uri getEntryUri(final Request request, final BlogEntry blogEntry) {
        final Path entryPath = this.atomEntryResource().route().objectToPath(blogEntry);
        return Uri.builder()
                .setScheme(request.uri().scheme())
                .setAuthority(request.uri().authority())
                .setPath(entryPath)
                .build();
    }

    @Override
    protected ListenableFuture<Response> get(final Request request) {
        final Iterable<BlogEntry> entries = this.blogStore.getAllEntries();

        final MessageFeed.Builder builder = MessageFeed.builder()
                .addLinks(this.getLinks(request.uri()))
                .setId(this.getId(request.uri(), System.currentTimeMillis()))
                .setTitle("Test Feed");

        for (final BlogEntry entry : entries) {
            final Uri entryUri = getEntryUri(request, entry);

            builder.addEntry(
                    BlogEntryHelpers.toMessageEntry(
                            entry, 
                            this.atomEntryResource().getId(entryUri, entry.getCreated()),
                            this.atomEntryResource().getLinks(entryUri)));
        }

        return Futures.immediateFuture(
                    Response.builder()
                        .setStatus(Status.SUCCESS_OK)
                        .setEntity(builder.build()).build());
    }
    
    @Override
    protected ListenableFuture<Response> post(final Request request,
            final MessageFeed message) {
        final MessageFeed.Builder feedBuilder = MessageFeed.builder();

        for (final MessageEntry entry : message.getEntries()) {
            final BlogEntry blogEntry = BlogEntryHelpers.fromMessageEntry(entry, "");
            final BlogEntry newEntry = this.blogStore.createEntry(blogEntry);
            final Uri entryUri = getEntryUri(request, newEntry);       
            
            feedBuilder.addEntry(
                    BlogEntryHelpers.toMessageEntry(
                            newEntry, 
                            entryUri,
                            this.atomEntryResource().getLinks(entryUri)));
        }
        
        return Futures.immediateFuture(
                    Response.builder()
                        .setStatus(Status.SUCCESS_CREATED)
                        .setEntity(feedBuilder.build()).build());
    }


    @Override
    public Route route() {
        return this.route;
    }

    @Override
    public Iterable<MediaRange> supportedMediaRanges() {
        return this.supportedMediaRanges;
    }
}
