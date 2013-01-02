package restlib.server;

import restlib.Response;
import restlib.data.Status;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public final class FutureResponses {
    /**
     * The request could not be understood by the server due to malformed syntax.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.1">400 Bad Request</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_BAD_REQUEST = Futures.immediateFuture(Status.CLIENT_ERROR_BAD_REQUEST.toResponse()); 

    /**
     * The request could not be completed due to a conflict with the current state of the resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.10">409 Conflict</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_CONFLICT= Futures.immediateFuture(Status.CLIENT_ERROR_CONFLICT.toResponse()); 

    /**
     * The expectation given in an Expect request-header field could not be met by this server.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.18">417 Expectation Failed</a>        
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_EXPECTATION_FAILED = Futures.immediateFuture(Status.CLIENT_ERROR_EXPECTATION_FAILED.toResponse()); 
    
    /**
     * The method could not be performed on the resource because the 
     * requested action depended on another action and that action failed.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.4">424 Failed Dependency</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_FAILED_DEPENDENCY = Futures.immediateFuture(Status.CLIENT_ERROR_FAILED_DEPENDENCY.toResponse()); 
    
    /**
     * The server understood the request, but is refusing to authorize it.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.4">403 Bad Request</a>
     */         
    public static final ListenableFuture<Response> CLIENT_ERROR_FORBIDDEN = Futures.immediateFuture(Status.CLIENT_ERROR_FORBIDDEN.toResponse()); ;
                      
    /**
     * The requested resource is no longer available at the server and no forwarding address is known.
     * This condition is expected to be considered permanent.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.11">410 Gone</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_GONE = Futures.immediateFuture(Status.CLIENT_ERROR_GONE.toResponse()); 
    
    /**
     * The server refuses to accept the request without a defined Content-Length.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.12">411 Length Required</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_LENGTH_REQUIRED = Futures.immediateFuture(Status.CLIENT_ERROR_LENGTH_REQUIRED.toResponse());     
            
    /**
     * The source or destination resource of a method is locked.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.3">423 Locked</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_LOCKED = Futures.immediateFuture(Status.CLIENT_ERROR_LOCKED.toResponse()); 
    
    /**
     * The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.6">405 Method Not Allowed</a>
     */                
    public static final ListenableFuture<Response> CLIENT_ERROR_METHOD_NOT_ALLOWED = Futures.immediateFuture(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED.toResponse()); 
            
    /**
     * The resource identified by the request is only capable of generating response entities 
     * which have content characteristics not acceptable according to the accept headers sent in the request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.7">406 Not Acceptable</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_NOT_ACCEPTABLE = Futures.immediateFuture(Status.CLIENT_ERROR_NOT_ACCEPTABLE.toResponse()); 
   
    /**
     * The server has not found anything matching the Request-URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.5">404 Not Found</a>
     */          
    public static final ListenableFuture<Response> CLIENT_ERROR_NOT_FOUND = Futures.immediateFuture(Status.CLIENT_ERROR_NOT_FOUND.toResponse());           
    /**
     * The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.13">412 Precondition Failed</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_PRECONDITION_FAILED = Futures.immediateFuture(Status.CLIENT_ERROR_PRECONDITION_FAILED.toResponse()); 
            
    /**
     * The request requires user authentication.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.2">407 Unauthorized</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_PROXY_AUTHENTICATED = Futures.immediateFuture(Status.CLIENT_ERROR_PROXY_AUTHENTICATED.toResponse()); 
            
    /**
     * The server is refusing to process a request because the request entity is larger than the server is willing or able to process.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.14">413 Request Entity Too Large</a>        
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE = Futures.immediateFuture(Status.CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE.toResponse()); 

    /**
     * The client did not produce a request within the time that the server was prepared to wait.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.9">408 Request Timeout</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_REQUEST_TIMEOUT = Futures.immediateFuture(Status.CLIENT_ERROR_REQUEST_TIMEOUT.toResponse()); 
            
    /**
     * The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.15">414 Request-URI Too Long</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_REQUEST_URI_TOO_LONG = Futures.immediateFuture(Status.CLIENT_ERROR_REQUEST_URI_TOO_LONG.toResponse()); 
            
    /**
     * Request included a Range request-header field, and none of the range-specifier values in this field overlap the current extent of the selected resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.17">416 Requested Range Not Satisfiable</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_REQUESTED_RANGE_NOT_SATISFIABLE = Futures.immediateFuture(Status.CLIENT_ERROR_REQUESTED_RANGE_NOT_SATISFIABLE.toResponse()); 
            
    /**
     * The request requires user authentication.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.2">401 Unauthorized</a>
     */  
    public static final ListenableFuture<Response> CLIENT_ERROR_UNAUTHORIZED = Futures.immediateFuture(Status.CLIENT_ERROR_UNAUTHORIZED.toResponse()); 
            
    /**
     * The server understands the content type of the request entity, 
     * and the syntax of the request entity is correct but was unable 
     * to process the contained instructions.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.2">422 Unprocessable Entity</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_UNPROCESSABLE_ENTITY = Futures.immediateFuture(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY.toResponse()); 
            
    /**
     * The server is refusing to service the request because the entity of the request is in a 
     * format not supported by the requested resource for the requested method.        
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.16">415 Unsupported Media Type</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE = Futures.immediateFuture(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.toResponse()); 
            
    /**
     * Allows a server to definitively state the precise protocol extensions given resource must be served with.
     * @see <a href="http://tools.ietf.org/html/rfc2817#section-6">426 Upgrade Required</a>
     */
    public static final ListenableFuture<Response> CLIENT_ERROR_UPGRADE_REQUIRED = Futures.immediateFuture(Status.CLIENT_ERROR_UPGRADE_REQUIRED.toResponse()); 
    
    /**
     * The client SHOULD continue with its request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.1.1">100 Continue</a>
     */
    public static final ListenableFuture<Response> INFORMATIONAL_CONTINUE = Futures.immediateFuture(Status.INFORMATIONAL_CONTINUE.toResponse()); 
    
            
    /**
     * The server has accepted the complete request, but has not yet completed it.
     * @see <a href="http://tools.ietf.org/html/rfc2518#section-10.1">102 Processing</a>
     */
    public static final ListenableFuture<Response> INFORMATIONAL_PROCESSING = Futures.immediateFuture(Status.INFORMATIONAL_PROCESSING.toResponse());  
    /**
     * The server understands and is willing to comply with the client's request for a change 
     * in the application protocol being used on this connection.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.1.2">101 Switching Protocols</a>
     */
    public static final ListenableFuture<Response> INFORMATIONAL_SWITCHING_PROTOCOLS = Futures.immediateFuture(Status.INFORMATIONAL_SWITCHING_PROTOCOLS.toResponse()); 
            
    /**
     * The requested resource resides temporarily under a different URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.3">302 Found</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_FOUND = Futures.immediateFuture(Status.REDIRECTION_FOUND.toResponse()); 
            
    /**
     * The requested resource has been assigned a new permanent URI and any future references to this resource SHOULD use one of the returned URIs.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.2">310 Moved Permanently</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_MOVED_PERMANENTLY = Futures.immediateFuture(Status.REDIRECTION_MOVED_PERMANENTLY.toResponse()); 
            
    /**
     * The requested resource corresponds to any one of a set of representations, 
     * each with its own specific location, and agent-driven negotiation information 
     * is being provided so that the user (or user agent) can select a preferred 
     * representation and redirect its request to that location.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.1">300 Multiple Choices</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_MULTIPLE_CHOICES = Futures.immediateFuture(Status.REDIRECTION_MULTIPLE_CHOICES.toResponse()); 
            
    /**
     * The response to the request has not been modified since the 
     * conditions indicated by the client's conditional GET request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.5">304 Not Modified</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_NOT_MODIFIED = Futures.immediateFuture(Status.REDIRECTION_NOT_MODIFIED.toResponse()); 
            
    /**
     * The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on that resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.4">303 See Other</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_SEE_OTHER = Futures.immediateFuture(Status.REDIRECTION_SEE_OTHER.toResponse()); 
            
    /**
     * The requested resource resides temporarily under a different URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.8">307 Temporary Redirect</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_TEMPORARY_REDIRECT = Futures.immediateFuture(Status.REDIRECTION_TEMPORARY_REDIRECT.toResponse()); 
            
    /**
     * The requested resource MUST be accessed through the proxy given by the Location field.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.6">305 Use Proxy</a>
     */
    public static final ListenableFuture<Response> REDIRECTION_USE_PROXY = Futures.immediateFuture(Status.REDIRECTION_USE_PROXY.toResponse()); 
            
    /**
     * The server, while acting as a gateway or proxy, received an 
     * invalid response from the upstream server it accessed in 
     * attempting to fulfill the request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.3">502 Bad Gateway</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_BAD_GATEWAY = Futures.immediateFuture(Status.SERVER_ERROR_BAD_GATEWAY.toResponse()); 
            
    /**
     * The server, while acting as a gateway or proxy, did not receive a 
     * timely response from the upstream server specified by the URI 
     * (e.g. HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS) 
     * it needed to access in attempting to complete the request. 
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.5">504 Gateway Timeout</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_GATEWAY_TIMEOUT = Futures.immediateFuture(Status.SERVER_ERROR_GATEWAY_TIMEOUT.toResponse()); 
            
    /**
     * The server does not support, or refuses to support, 
     * the HTTP protocol version that was used in the request message.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.6">505 HTTP Version Not Supported</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED = Futures.immediateFuture(Status.SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED.toResponse()); 
            
    /**
     * The method could not be performed on the resource because the server is unable to 
     * store the representation needed to successfully complete the request.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.5">507 Insufficient Storage</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_INSUFFICIENT_STORAGE = Futures.immediateFuture(Status.SERVER_ERROR_INSUFFICIENT_STORAGE.toResponse()); 
            
    /**
     * The server encountered an unexpected condition which prevented it from fulfilling the request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.1">500 Internal Server Error</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_INTERNAL = Futures.immediateFuture(Status.SERVER_ERROR_INTERNAL.toResponse()); 
            
    /**
     * The server terminated an operation because it encountered an infinite loop while processing the request.
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.2">508 Loop Detected</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_LOOP_DETECTED = Futures.immediateFuture(Status.SERVER_ERROR_LOOP_DETECTED.toResponse()); 
            
    /**
     * The policy for accessing the resource has not been met in the request.
     * @see <a href="http://tools.ietf.org/html/rfc2774#section-7">510 Not Extended </a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_NOT_EXTENDED = Futures.immediateFuture(Status.SERVER_ERROR_NOT_EXTENDED.toResponse()); 
            
    /**
     * The server does not support the functionality required to fulfill the request. 
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.2">501 Not Implemented</a>      
     */
    public static final ListenableFuture<Response> SERVER_ERROR_NOT_IMPLEMENTED = Futures.immediateFuture(Status.SERVER_ERROR_NOT_IMPLEMENTED.toResponse()); 
            
    /**
     * The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.4">503 Service Unavailable</a>
     */
    public static final ListenableFuture<Response> SERVER_ERROR_SERVICE_UNAVAILABLE = Futures.immediateFuture(Status.SERVER_ERROR_SERVICE_UNAVAILABLE.toResponse()); 
    
    /**
     * The server has an internal configuration error: the chosen variant resource is 
     * configured to engage in transparent content negotiation itself, and is therefore 
     * not a proper end point in the negotiation process.
     * @see <a href="http://tools.ietf.org/html/rfc2295#section-8.1">506 Variant Also Negotiates</a>        
     */
    public static final ListenableFuture<Response> SERVER_ERROR_VARIANT_ALSO_NEGOTIATES = Futures.immediateFuture(Status.SERVER_ERROR_VARIANT_ALSO_NEGOTIATES.toResponse()); 
            
    /**
     * The request has been accepted for processing, but the processing has not been completed.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.3">202 Accepted</a>
     */
    public static final ListenableFuture<Response> SUCCESS_ACCEPTED = Futures.immediateFuture(Status.SUCCESS_ACCEPTED.toResponse()); 
            
    /**
     * Already Reported
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.1">208 Already Reported</a>
     */
    public static final ListenableFuture<Response> SUCCESS_ALREADY_REPORTED = Futures.immediateFuture(Status.SUCCESS_ALREADY_REPORTED.toResponse()); 
            
    /**
     * The request has been fulfilled and resulted in a new resource being created.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.2">201 Created</a>
     */
    public static final ListenableFuture<Response> SUCCESS_CREATED = Futures.immediateFuture(Status.SUCCESS_CREATED.toResponse()); 
            
    /**
     * The response is a representation of the result of one or 
     * more instance-manipulations applied to the current instance.
     * @see <a href="http://tools.ietf.org/html/rfc3229#section-10.4.1">226 IM Used</a>
     */
    public static final ListenableFuture<Response> SUCCESS_IM_USED = Futures.immediateFuture(Status.SUCCESS_IM_USED.toResponse()); 
            
    /**
     * Multiple resources were to be affected by the COPY, but errors on some of them prevented the operation from taking place.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.1">207 Multi-Status</a>
     */
    public static final ListenableFuture<Response> SUCCESS_MULTI_STATUS = Futures.immediateFuture(Status.SUCCESS_MULTI_STATUS.toResponse()); 
            
    /**
     * The server has fulfilled the request but does not need to return an entity-body, and might want to return updated metainformation.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.5">204 No Content</a>
     */
    public static final ListenableFuture<Response> SUCCESS_NO_CONTENT = Futures.immediateFuture(Status.SUCCESS_NO_CONTENT.toResponse()); 
    
    /**
     * The returned metainformation in the entity-header is not 
     * the definitive set as available from the origin server, 
     * but is gatheredfrom a local or a third-party copy.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.4">203 Non-Authoritative Information</a>
     */
    public static final ListenableFuture<Response> SUCCESS_NON_AUTHORITATIVE_INFORMATION = Futures.immediateFuture(Status.SUCCESS_NON_AUTHORITATIVE_INFORMATION.toResponse()); 
            
    /**
     * The request has succeeded.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.1">200 OK</a>
     */
    public static final ListenableFuture<Response> SUCCESS_OK = Futures.immediateFuture(Status.SUCCESS_OK.toResponse()); 
            
    /**
     * The server has fulfilled the partial GET request for the resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.7">206 Partial Content</a>
     */
    public static final ListenableFuture<Response> SUCCESS_PARTIAL_CONTENT = Futures.immediateFuture(Status.SUCCESS_PARTIAL_CONTENT.toResponse()); 
            
    /**
     * The server has fulfilled the request and the user agent SHOULD reset the document view which caused the request to be sent.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.6">205 Reset Content</a>
     */
    public static final ListenableFuture<Response> SUCCESS_RESET_CONTENT = Futures.immediateFuture(Status.SUCCESS_RESET_CONTENT.toResponse()); 
    
    private FutureResponses() {
    }
}
