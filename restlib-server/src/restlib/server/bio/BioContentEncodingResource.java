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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

final class BioContentEncodingResource<T> extends BioResourceWrapper<T> {

    private final Iterable<ContentEncoding> AVAILABLE_ENCODINGS = ImmutableList.of(ContentEncoding.GZIP);
    private final int bufSize;
    
    BioContentEncodingResource(final BioResource<T> resource, final int bufSize) {
        super(resource);
        this.bufSize = bufSize;
    }

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
}
