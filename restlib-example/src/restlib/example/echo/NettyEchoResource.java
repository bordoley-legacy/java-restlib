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


package restlib.example.echo;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.data.Charset;
import restlib.data.MediaRanges;
import restlib.ext.netty.v3_5.io.ChannelBufferDeserializer;
import restlib.ext.netty.v3_5.io.ChannelBufferDeserializers;
import restlib.ext.netty.v3_5.io.ChannelBufferSerializer;
import restlib.ext.netty.v3_5.io.ChannelBufferSerializers;
import restlib.ext.netty.v3_5.server.NettyResourceDecorator;
import restlib.server.Resource;

import com.google.common.base.Preconditions;

public class NettyEchoResource extends NettyResourceDecorator<String>{ 
    public static NettyEchoResource newInstance(final Resource resource) {
        Preconditions.checkNotNull(resource);
        return new NettyEchoResource(resource);
    }
    
    private NettyEchoResource(final Resource resource) {
        super(resource);
    }

    @Override
    public ChannelBufferDeserializer<String> getRequestEntityDeserializer(final Request request) {
        return ChannelBufferDeserializers.stringDeserializerUsingDynamicBuffer(
                request.contentInfo().mediaRange().get().charset().get(), 1024);
    }

    @Override
    public ChannelBufferSerializer getResponseEntitySerializer(
            final Request request, final Response response) {
        final ContentInfo contentInfo =
                ContentInfo.builder()
                    .setMediaRange(MediaRanges.TEXT_PLAIN.withCharset(Charset.UTF_8))
                    .build();
        return ChannelBufferSerializers.stringSerializer(request.toString(), contentInfo);
    }
}
