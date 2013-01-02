package restlib.server.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import restlib.ContentInfo;
import restlib.ContentInfoWrapper;
import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.data.ContentEncoding;
import restlib.data.Preference;
import restlib.data.Status;
import restlib.server.FutureResponses;
import restlib.server.Resources;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.ListenableFuture;

public final class BioResources {
    public static final BioResource<?> NOT_FOUND = 
            new BioResourceDecorator<String>(Resources.NOT_FOUND) {
                @Override
                public InputStreamDeserializer<String> getRequestEntityDeserializer(final Request request) {
                    throw new UnsupportedOperationException();
                }
        
                @Override
                public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
                    return OutputStreamSerializerSuppliers.STRING_AS_PLAIN_TEXT_SERIALIZER_SUPPLIER.get(request, response);
                }
            };
    
    public static <T> BioResource<T> contentEncodingResource(final BioResource<T> resource, final int bufSize) {
        Preconditions.checkNotNull(resource);
        return new BioResourceWrapper<T>(resource) {
            private final Iterable<ContentEncoding> AVAILABLE_ENCODINGS = ImmutableList.of(ContentEncoding.GZIP);
            
            @Override
            public InputStreamDeserializer<T> getRequestEntityDeserializer(final Request request) {
                // FIXME: This is wrong. The encodings are listed in the order they were applied by the client.
                // In practice, never more than one is use but for correctness, it would be better to loop
                // through and apply decodings as needed.
                if (request.contentInfo().encodings().contains(ContentEncoding.GZIP)) {
                    final InputStreamDeserializer<T> delegate = super.getRequestEntityDeserializer(request); 
                    return new InputStreamDeserializer<T>() {
                        @Override
                        public T read(final InputStream is) throws IOException {
                            final InputStream gis = new GZIPInputStream(is, bufSize);
                            return delegate.read(gis);
                        }};
                } else {
                    return super.getRequestEntityDeserializer(request);
                }
            }

            @Override
            public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
                final Optional<ContentEncoding> bestMatch = 
                            Preference.<ContentEncoding> bestMatch(
                                    request.preferences().acceptedEncodings(), 
                                    AVAILABLE_ENCODINGS);
                
                if (bestMatch.isPresent() && bestMatch.get().equals(ContentEncoding.GZIP)) {   
                    final OutputStreamSerializer delegate = super.getResponseEntitySerializer(request, response);
                    
                    return new OutputStreamSerializer() {
                        @Override
                        public ContentInfo contentInfo() {
                            final ContentInfo delegateContentInfo = delegate.contentInfo();
                            
                            return new ContentInfoWrapper (delegateContentInfo) {            
                                @Override
                                public List<ContentEncoding> encodings() {
                                    return ImmutableList.<ContentEncoding> builder()
                                            .addAll(delegate.contentInfo().encodings())
                                            .add(ContentEncoding.GZIP)
                                            .build();
                                }};
                        }

                        @Override
                        public long write(final OutputStream os) throws IOException {
                            Preconditions.checkNotNull(os);
                            final OutputStream gos = new GZIPOutputStream(os);
                            return delegate.write(gos);
                        }           
                    };
                }
                
                return super.getResponseEntitySerializer(request, response);
            }
        };
    }
    
    public static <T> BioResource<T> errorResource(final BioResource<T> resource) {
        Preconditions.checkNotNull(resource);
        return new BioResourceWrapper<T>(resource) {
            @Override
            public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
                Preconditions.checkNotNull(response);
                if (!response.status().statusClass().equals(Status.Class.SUCCESS)) {
                    return OutputStreamSerializerSuppliers.STRING_AS_PLAIN_TEXT_SERIALIZER_SUPPLIER.get(request, response);            
                }
                return super.getResponseEntitySerializer(request, response);
            }
        };
    }
    
    public static <T> BioResource<T> limitInputResource(final BioResource<T> resource, final long limit) {
        Preconditions.checkNotNull(resource);
        Preconditions.checkArgument(limit >= 0);
        
        return new BioResourceWrapper<T>(resource) {
            @Override
            public InputStreamDeserializer<T> getRequestEntityDeserializer(final Request request) {
                final InputStreamDeserializer<T> delegate = super.getRequestEntityDeserializer(request);
                
                return new InputStreamDeserializer<T>() {
                    @Override
                    public T read(final InputStream is) throws IOException {
                        final InputStream lis = ByteStreams.limit(is, limit);
                        return delegate.read(lis);
                    }          
                };
            }

            @Override
            public ListenableFuture<Response> handle(final Request request) {
                
                if (request.contentInfo().length().isPresent() && 
                        (request.contentInfo().length().get() > limit)) {
                    return FutureResponses.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE;
                }
                return super.handle(request);
            }
        };
    }
    
    private BioResources(){}
}
