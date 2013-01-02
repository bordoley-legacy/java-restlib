package restlib.server;

import java.util.Set;

import restlib.Request;
import restlib.Response;
import restlib.data.EntityTag;
import restlib.data.Method;
import restlib.data.Status;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A {@code Resource} implementation that provides an abstract implementation
 * for the standard HTTP methods.  
 * 
 * <p><b>Note:</b> Systems that process high volumes of updates may want to validate the
 * update conditions in the handlePut() and handlePatch() methods to prevent 
 * lost updates, as there is no programmatic way for this class to prevent
 * multiple requests from changing the same resource in parallel.</p>
 */
public abstract class UniformResource<T> implements Resource {
    private static boolean unmodified(final Request request, final Response response) {
        
        // Not a conditional request
        if (request.preconditions().ifNoneMatchTags().isEmpty() && 
                !request.preconditions().ifModifiedSinceDate().isPresent()) {
            return false;
        }
        
        // Tag matching takes precedence over modification date.
        if (!request.preconditions().ifNoneMatchTags().isEmpty() && 
                response.entityTag().isPresent()) {
            if ((response.entityTag().get() instanceof EntityTag.Strong) &&
                    request.preconditions().ifNoneMatchTags().contains(response.entityTag().get())) {
                return true;
            } else {
                final EntityTag strong = EntityTag.strongTag(response.entityTag().get().value());
                if (request.preconditions().ifNoneMatchTags().contains(response.entityTag().get()) ||
                    request.preconditions().ifNoneMatchTags().contains(strong)) {
                    return true;
                }
            }
        } 
        
        if (request.preconditions().ifModifiedSinceDate().isPresent() && response.lastModified().isPresent()) {
            if (request.preconditions().ifModifiedSinceDate().get().compareTo(response.lastModified().get()) >= 0) {
                return true;
            }
        } 
    
        return false;
    }

    private final Set<Method> allowedMethods;
    private final Class<T> messageClass;
    private final ListenableFuture<Response> methodNotAllowedResponse;
    private final ListenableFuture<Response> optionsResponse;

    /**
     * @param messageClass
     */
    protected UniformResource(final Class<T> messageClass) {
        this.messageClass = messageClass;
        this.allowedMethods = getImplementedMethods();
        this.methodNotAllowedResponse = 
                Futures.immediateFuture(
                        Response.builder()
                            .setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED)
                            .addAllowedMethods(this.allowedMethods)
                            .build());
        this.optionsResponse =
                Futures.immediateFuture(
                        Response.builder()
                            .setStatus(Status.SUCCESS_OK)
                            .addAllowedMethods(this.allowedMethods)
                            .setEntity(Status.SUCCESS_OK)
                            .build());
    }
    
    /* (non-Javadoc)
     * @see restlib.server.Resource#acceptMessage(restlib.Request, java.lang.Object)
     */
    public final ListenableFuture<Response> acceptMessage(final Request request,
            final Object message) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(message);
        Preconditions.checkArgument(this.messageClass.isInstance(message));

        final T msg = this.messageClass.cast(message);
        if (request.method().equals(Method.POST)) {
            return post(request, msg);
        } else if (request.method().equals(Method.PUT)) {
            return put(request, msg);
        } else if (request.method().equals(Method.PATCH)) {
            return patch(request, msg);
        } else {
            throw new IllegalArgumentException("Request method: " + request.method() + " is not a valid argument to UniformResource.acceptMessage().");
        }
    }
    
    private ListenableFuture<Response> checkUpdateConditions(final Request request) {
        final ListenableFuture<Response> response = get(request);
        
        return Futures.transform(response, new AsyncFunction<Response, Response>() {
            @Override
            public ListenableFuture<Response> apply(final Response response) {      
                if (!response.status().statusClass().equals(Status.Class.SUCCESS)) {
                    return Futures.immediateFuture(response);
                } 
                
                if (requireETagForUpdate() && response.entityTag().isPresent()) {
                    if (request.preconditions().ifMatchTags().isEmpty()) {
                        return FutureResponses.CLIENT_ERROR_FORBIDDEN;
                    } 
                    
                    if ((response.entityTag().get() instanceof EntityTag.Strong) &&
                                request.preconditions().ifNoneMatchTags().contains(response.entityTag().get())) {
                        return FutureResponses.INFORMATIONAL_CONTINUE;
                    } 
                    
                    final EntityTag strong = EntityTag.strongTag(response.entityTag().get().value());
                    if (request.preconditions().ifMatchTags().contains(response.entityTag().get()) ||
                        request.preconditions().ifMatchTags().contains(strong)) {
                        return FutureResponses.INFORMATIONAL_CONTINUE;
                    } 
                    
                    return FutureResponses.CLIENT_ERROR_PRECONDITION_FAILED;
                } 
                
                if (requireIfUnmodifiedSinceForUpdate() && response.lastModified().isPresent()) {
                    if (!request.preconditions().ifUnmodifiedSinceDate().isPresent()) {
                        return FutureResponses.CLIENT_ERROR_FORBIDDEN;
                    } 
                    
                    if (request.preconditions().ifUnmodifiedSinceDate().get().compareTo(response.lastModified().get()) >= 0) {                 
                        return FutureResponses.INFORMATIONAL_CONTINUE;
                    } 
                    
                    return FutureResponses.CLIENT_ERROR_PRECONDITION_FAILED;
                } 
                
                return FutureResponses.INFORMATIONAL_CONTINUE;
            }
        });
    }

    
    private ListenableFuture<Response> conditionalGet(final Request request) {
        final ListenableFuture<Response> response = get(request);

        return Futures.transform(response, new Function<Response, Response>() {
            @Override
            public Response apply(final Response response) {
                final Status responseStatus = response.status();

                if (!responseStatus.statusClass().equals(Status.Class.SUCCESS)) {
                    return response;
                } else if (unmodified(request, response)) {
                    return Status.REDIRECTION_NOT_MODIFIED.toResponse();
                } else {
                    return response;
                }
            }
        });
    }

    
    /**
     * Sub-classes may override this method in order to implement the HTTP
     * DELETE method.
     * 
     * @param request
     *            The client HTTP request.
     * @return The server's response to the client request.
     */
    protected ListenableFuture<Response> delete(final Request request) {
        // This method should not be called unless a subclass has overridden it.
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-classes must provide an implementation of the HTTP GET method.
     * 
     * @param request
     *            The client HTTP request.
     * @return The server's response to the client request.
     */
    protected abstract ListenableFuture<Response> get(final Request request);

    private Set<Method> getImplementedMethods() {
        final Set<Method> set = 
                Sets.newHashSet(
                        Method.GET, Method.HEAD, 
                        Method.OPTIONS, Method.DELETE,
                        Method.PATCH, Method.POST, Method.PUT);
        
        try {
            delete(null);
        } catch (final UnsupportedOperationException e) {
            set.remove(Method.DELETE);
        } catch (final Throwable e) {
        }
        
        try {
            patch(null, null);
        } catch (final UnsupportedOperationException e) {
            set.remove(Method.PATCH);
        } catch (final Throwable e) {
        }
        
        try {
            post(null, null);
        } catch (final UnsupportedOperationException e) {
            set.remove(Method.POST);
        } catch (final Throwable e) {
        }
        
        try {
            put(null, null);
        } catch (final UnsupportedOperationException e) {
            set.remove(Method.PUT);
        } catch (final Throwable e) {
        }
        
        return ImmutableSet.copyOf(set);
    }

    @Override
    public final ListenableFuture<Response> handle(final Request request) {
        Preconditions.checkNotNull(request);

        // Check if the method is supported by this resource
        if (!this.allowedMethods.contains(request.method())) {
            return this.methodNotAllowedResponse;
        }

        if (request.method().equals(Method.GET) || 
                request.method().equals(Method.HEAD)) {
            return conditionalGet(request);
        } else if (request.method().equals(Method.POST)) {
            final ListenableFuture<Response> response = get(request);
            return Futures.transform(response, new Function<Response, Response>() {
                @Override
                public Response apply(final Response response) {
                    final Status.Class statusClass = response.status().statusClass();           
                    return (!statusClass.equals(Status.Class.SUCCESS)) ? 
                            response : Status.INFORMATIONAL_CONTINUE.toResponse();
                }
            }); 
        } else if (request.method().equals(Method.PUT) ||
                request.method().equals(Method.PATCH)) {
            return checkUpdateConditions(request);
        } else if (request.method().equals(Method.DELETE)) {
            final ListenableFuture<Response> futureResponse = get(request);
            return Futures.<Response, Response> transform(futureResponse, new AsyncFunction<Response, Response>() {
                @Override
                public ListenableFuture<Response> apply(final Response response) {
                    final Status.Class statusClass = response.status().statusClass();
                    return (!statusClass.equals(Status.Class.SUCCESS)) ? futureResponse : delete(request);
                    }
            });
        } else if (request.method().equals(Method.OPTIONS)) {
            return this.optionsResponse;
        } else {
            // This should never happen
           throw new RuntimeException();
        }
    }

    /**
     * Sub-classes may override this method in order to implement the HTTP PATCH
     * method.
     * 
     * @param request
     *            The client HTTP request.
     * @param message
     *            The client HTTP message body.
     * @return The server's response to the client request.
     */
    protected ListenableFuture<Response> patch(final Request request,
            final T message) {
        // This method should not be called unless a subclass has overridden it.
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-classes may override this method in order to implement the HTTP POST
     * method.
     * 
     * @param request
     *            The client HTTP request.
     * @param message
     *            The client HTTP message body.
     * @return The server's response to the client request.
     */
    protected ListenableFuture<Response> post(final Request request,
            final T message) {
        // This method should not be called unless a subclass has overridden it.
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-classes may override this method in order to implement the HTTP PUT
     * method.
     * 
     * @param request
     *            The client HTTP request.
     * @param message
     *            The client HTTP message body.
     * @return The server's response to the client request.
     */
    protected ListenableFuture<Response> put(final Request request,
            final T message) {
        // This method should not be called unless a subclass has overridden it.
        throw new UnsupportedOperationException();
    }

    /**
     * Returns whether this resource requires an ETag for updates via HTTP PUT
     * or PATCH requests.By default this method returns true.
     */
    protected boolean requireETagForUpdate() {
        return true;
    }

    /**
     * Returns whether this resource requires a client to provide the
     * If-Unmodified-Since header for updates via HTTP PUT or PATCH requests.By
     * default this method returns false.
     * 
     * <p>
     * <b>Note:</b> This class gives precedence to
     * {@link UniformResource#requireETagForUpdate()} over this method.
     * </p>
     */
    protected boolean requireIfUnmodifiedSinceForUpdate() {
        return false;
    }
}
