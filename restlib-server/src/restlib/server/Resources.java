package restlib.server;

import java.util.Map;

import restlib.Request;
import restlib.Response;
import restlib.ResponseBuilder;
import restlib.data.Status;

import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class Resources {    
    public static final Resource NOT_FOUND = new Resource() {
        public ListenableFuture<Response> acceptMessage(final Request request, final Object message) {
            return FutureResponses.CLIENT_ERROR_NOT_FOUND;
        }

        public ListenableFuture<Response> handle(final Request request) {
            return FutureResponses.CLIENT_ERROR_NOT_FOUND;
        }

        public Route route() {
            return Route.NONE;
        }  
    };
    
    public static Resource authorizedResource(
            final Resource next, 
            final Iterable<Authorizer> authorizers) {
        Preconditions.checkNotNull(next);
        Preconditions.checkNotNull(authorizers);
        Preconditions.checkArgument(!Iterables.isEmpty(authorizers));
        
        final ImmutableMap.Builder<String, Authorizer> mapBuilder = ImmutableMap.builder();
        for (final Authorizer authorizer : authorizers) {
            mapBuilder.put(Ascii.toLowerCase(authorizer.scheme()), authorizer);
        }
        
        final Map<String, Authorizer> authorizerMap = mapBuilder.build();
        
        return new ResourceWrapper(next) {
            @Override
            public ListenableFuture<Response> handle(final Request request) {
                Preconditions.checkNotNull(request);

                final ListenableFuture<Response> response;
                final String scheme = 
                        request.authorizationCredentials().isPresent() ?
                                Ascii.toLowerCase(request.authorizationCredentials().get().scheme()) : null;  

                if (authorizerMap.containsKey(scheme)) {
                    response = authorizerMap.get(scheme).authenticate(request);
                } else {
                    response = FutureResponses.CLIENT_ERROR_UNAUTHORIZED;
                }

                return Futures.transform(response, new AsyncFunction<Response, Response>() {
                    @Override
                    public ListenableFuture<Response> apply(final Response response) {
                        if (!response.status().statusClass().equals(Status.Class.SUCCESS)) {
                            if (!response.status().equals(Status.CLIENT_ERROR_UNAUTHORIZED)) {
                                return Futures.immediateFuture(response);
                            }

                            final ResponseBuilder builder = Response.builder().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);

                            for (final Authorizer availableAuthorizer : authorizerMap.values()) {
                                builder.addAuthenticationChallenge(availableAuthorizer.authenticationChallenge());
                            }
                            return Futures.immediateFuture(builder.build());
                        }

                        return next.handle(request);
                    }
                });
            }
        };
    }

    private Resources(){}
}
