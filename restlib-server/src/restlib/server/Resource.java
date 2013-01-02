package restlib.server;

import com.google.common.util.concurrent.ListenableFuture;

import restlib.Request;
import restlib.Response;

/**
 * An HTTP resource that can handle and respond to HTTP requests.
 * Implementations must be threadsafe.
 */
public interface Resource {
    /**
     * Handle the client request and message entity and return a response. This
     * method is called by a connector when a client HTTP request includes a
     * message entity and handle() provides an HTTP 100 Continue response.
     * 
     * @param request
     *            The client HTTP request.
     * @param message
     *            The client HTTP message body.
     * @return The server's response to the client request.
     */
    public ListenableFuture<Response> acceptMessage(Request request, Object message);

    /**
     * Handle the incoming request and return a response. Note: HTTP methods
     * that require the entity body of the request, should return a response
     * with the 100 Continue status. Connectors will process this response and
     * call acceptMessage when the message is available.
     * 
     * @param request
     *            The client HTTP request.
     * @return The server's response to the client request.
     */
    public ListenableFuture<Response> handle(Request request);

    /**
     * Returns the Route that this Resource matches.
     * 
     * @return This Resource's Route.
     */
    public Route route();
}
