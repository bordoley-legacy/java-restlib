package restlib.server.bio;

import java.io.IOException;
import java.io.InputStream;

import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.server.FutureResponses;

import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.ListenableFuture;

final class LimitInputResource<T> extends BioResourceWrapper<T> {

    private final long limit;
    
    LimitInputResource(final BioResource<T> resource, final long limit) {
        super(resource);
        this.limit = limit;
    }
    
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
}
