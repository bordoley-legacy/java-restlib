package restlib.server;

import restlib.Request;
import restlib.Response;

import com.google.common.util.concurrent.ListenableFuture;

final class NotFoundResource implements Resource {

    NotFoundResource() {}

    public ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
        return FutureResponses.CLIENT_ERROR_NOT_FOUND;
    }

    public ListenableFuture<Response> handle(final Request request) {
        return FutureResponses.CLIENT_ERROR_NOT_FOUND;
    }

    public Route route() {
        return Route.NONE;
    }  
}
