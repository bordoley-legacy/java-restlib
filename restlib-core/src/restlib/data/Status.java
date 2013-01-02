package restlib.data;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.Response;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Status of an HTTP Response.
 */
@Immutable
public final class Status {
	/**
	 * Enumeration representing the different classes of HTTP response statuses.
	 */
    public static enum Class {
        /**
         * The request contains bad syntax or cannot be fulfilled.
         */
        CLIENT_ERROR,
        
        /**
         * Informational - Request received, continuing process.
         */
        INFORMATIONAL,
        
        /**
         * Further action must be taken in order to complete the request.
         */
        REDIRECTION,
        
        /**
         * The server failed to fulfill an apparently valid request.
         */
        SERVER_ERROR,
        
        /**
         * The action was successfully received, understood, and accepted.
         */
        SUCCESS,
        
        /**
         * The status does not belong to a defined status class.
         */
        UNDEFINED;
        
        
        private static Class getStatusClassForCode(final int code) {
            if ((code >= 100) && (code < 200)) {
                return Class.INFORMATIONAL;
            } else if ((code >= 200) && (code < 300)) {
                return Class.SUCCESS;
            } else if ((code >= 300) && (code < 400)) {
                return Class.REDIRECTION;
            } else if ((code >= 400) && (code < 500)) {
                return Class.CLIENT_ERROR;
            } else if ((code >= 500) && (code < 600)) {
                return Class.SERVER_ERROR;
            } else {
                return UNDEFINED;
            }
        }
    }
    
    /**
     * The request could not be understood by the server due to malformed syntax.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.1">400 Bad Request</a>
     */
    public static final Status CLIENT_ERROR_BAD_REQUEST; 

    /**
     * The request could not be completed due to a conflict with the current state of the resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.10">409 Conflict</a>
     */
    public static final Status CLIENT_ERROR_CONFLICT;

    /**
     * The expectation given in an Expect request-header field could not be met by this server.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.18">417 Expectation Failed</a>        
     */
    public static final Status CLIENT_ERROR_EXPECTATION_FAILED;
    
    /**
     * The method could not be performed on the resource because the 
     * requested action depended on another action and that action failed.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.4">424 Failed Dependency</a>
     */
    public static final Status CLIENT_ERROR_FAILED_DEPENDENCY; 
    
    /**
     * The server understood the request, but is refusing to authorize it.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.4">403 Bad Request</a>
     */         
    public static final Status CLIENT_ERROR_FORBIDDEN;
                      
    /**
     * The requested resource is no longer available at the server and no forwarding address is known.
     * This condition is expected to be considered permanent.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.11">410 Gone</a>
     */
    public static final Status CLIENT_ERROR_GONE;
    
    /**
     * The server refuses to accept the request without a defined Content-Length.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.12">411 Length Required</a>
     */
    public static final Status CLIENT_ERROR_LENGTH_REQUIRED;        
            
    /**
     * The source or destination resource of a method is locked.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.3">423 Locked</a>
     */
    public static final Status CLIENT_ERROR_LOCKED;
    
    /**
     * The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.6">405 Method Not Allowed</a>
     */                
    public static final Status CLIENT_ERROR_METHOD_NOT_ALLOWED;
            
    /**
     * The resource identified by the request is only capable of generating response entities 
     * which have content characteristics not acceptable according to the accept headers sent in the request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.7">406 Not Acceptable</a>
     */
    public static final Status CLIENT_ERROR_NOT_ACCEPTABLE;
   
    /**
     * The server has not found anything matching the Request-URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.5">404 Not Found</a>
     */          
    public static final Status CLIENT_ERROR_NOT_FOUND;            
    /**
     * The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.13">412 Precondition Failed</a>
     */
    public static final Status CLIENT_ERROR_PRECONDITION_FAILED;
            
    /**
     * The request requires user authentication.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.2">407 Unauthorized</a>
     */
    public static final Status CLIENT_ERROR_PROXY_AUTHENTICATED;
            
    /**
     * The server is refusing to process a request because the request entity is larger than the server is willing or able to process.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.14">413 Request Entity Too Large</a>        
     */
    public static final Status CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE;

    /**
     * The client did not produce a request within the time that the server was prepared to wait.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.9">408 Request Timeout</a>
     */
    public static final Status CLIENT_ERROR_REQUEST_TIMEOUT;
            
    /**
     * The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.15">414 Request-URI Too Long</a>
     */
    public static final Status CLIENT_ERROR_REQUEST_URI_TOO_LONG;
            
    /**
     * Request included a Range request-header field, and none of the range-specifier values in this field overlap the current extent of the selected resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.17">416 Requested Range Not Satisfiable</a>
     */
    public static final Status CLIENT_ERROR_REQUESTED_RANGE_NOT_SATISFIABLE;
            
    /**
     * The request requires user authentication.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.2">401 Unauthorized</a>
     */  
    public static final Status CLIENT_ERROR_UNAUTHORIZED;
            
    /**
     * The server understands the content type of the request entity, 
     * and the syntax of the request entity is correct but was unable 
     * to process the contained instructions.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.2">422 Unprocessable Entity</a>
     */
    public static final Status CLIENT_ERROR_UNPROCESSABLE_ENTITY;
            
    /**
     * The server is refusing to service the request because the entity of the request is in a 
     * format not supported by the requested resource for the requested method.        
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.4.16">415 Unsupported Media Type</a>
     */
    public static final Status CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE;
            
    /**
     * Allows a server to definitively state the precise protocol extensionsa given resource must be served with.
     * @see <a href="http://tools.ietf.org/html/rfc2817#section-6">426 Upgrade Required</a>
     */
    public static final Status CLIENT_ERROR_UPGRADE_REQUIRED;
    
    /**
     * The client SHOULD continue with its request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.1.1">100 Continue</a>
     */
    public static final Status INFORMATIONAL_CONTINUE;
            
    /**
     * The server has accepted the complete request, but has not yet completed it.
     * @see <a href="http://tools.ietf.org/html/rfc2518#section-10.1">102 Processing</a>
     */
    public static final Status INFORMATIONAL_PROCESSING;        
    /**
     * The server understands and is willing to comply with the client's request for a change 
     * in the application protocol being used on this connection.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.1.2">101 Switching Protocols</a>
     */
    public static final Status INFORMATIONAL_SWITCHING_PROTOCOLS;
            
    /**
     * The requested resource resides temporarily under a different URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.3">302 Found</a>
     */
    public static final Status REDIRECTION_FOUND;
            
    /**
     * The requested resource has been assigned a new permanent URI and any future references to this resource SHOULD use one of the returned URIs.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.2">310 Moved Permanently</a>
     */
    public static final Status REDIRECTION_MOVED_PERMANENTLY;
            
    /**
     * The requested resource corresponds to any one of a set of representations, 
     * each with its own specific location, and agent-driven negotiation information 
     * is being provided so that the user (or user agent) can select a preferred 
     * representation and redirect its request to that location.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.1">300 Multiple Choices</a>
     */
    public static final Status REDIRECTION_MULTIPLE_CHOICES;
            
    /**
     * The response to the request has not been modified since the 
     * conditions indicated by the client's conditional GET request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.5">304 Not Modified</a>
     */
    public static final Status REDIRECTION_NOT_MODIFIED;
            
    /**
     * The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on that resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.4">303 See Other</a>
     */
    public static final Status REDIRECTION_SEE_OTHER;
            
    /**
     * The requested resource resides temporarily under a different URI.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.8">307 Temporary Redirect</a>
     */
    public static final Status REDIRECTION_TEMPORARY_REDIRECT; 
            
    /**
     * The requested resource MUST be accessed through the proxy given by the Location field.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.3.6">305 Use Proxy</a>
     */
    public static final Status REDIRECTION_USE_PROXY;
   
    @VisibleForTesting
    static final ConcurrentMap<Integer,Status> REGISTERED_STATUSES = Maps.newConcurrentMap();
            
    /**
     * The server, while acting as a gateway or proxy, received an 
     * invalid response from the upstream server it accessed in 
     * attempting to fulfill the request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.3">502 Bad Gateway</a>
     */
    public static final Status SERVER_ERROR_BAD_GATEWAY;
            
    /**
     * The server, while acting as a gateway or proxy, did not receive a 
     * timely response from the upstream server specified by the URI 
     * (e.g. HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS) 
     * it needed to access in attempting to complete the request. 
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.5">504 Gateway Timeout</a>
     */
    public static final Status SERVER_ERROR_GATEWAY_TIMEOUT;
            
    /**
     * The server does not support, or refuses to support, 
     * the HTTP protocol version that was used in the request message.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.6">505 HTTP Version Not Supported</a>
     */
    public static final Status SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED;
            
    /**
     * The method could not be performed on the resource because the server is unable to 
     * store the representation needed to successfully complete the request.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.5">507 Insufficient Storage</a>
     */
    public static final Status SERVER_ERROR_INSUFFICIENT_STORAGE;
            
    /**
     * The server encountered an unexpected condition which prevented it from fulfilling the request.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.1">500 Internal Server Error</a>
     */
    public static final Status SERVER_ERROR_INTERNAL;
            
    /**
     * The server terminated an operation because it encountered an infinite loop while processing the request.
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.2">508 Loop Detected</a>
     */
    public static final Status SERVER_ERROR_LOOP_DETECTED;
            
    /**
     * The policy for accessing the resource has not been met in the request.
     * @see <a href="http://tools.ietf.org/html/rfc2774#section-7">510 Not Extended </a>
     */
    public static final Status SERVER_ERROR_NOT_EXTENDED;
            
    /**
     * The server does not support the functionality required to fulfill the request. 
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.2">501 Not Implemented</a>      
     */
    public static final Status SERVER_ERROR_NOT_IMPLEMENTED;
            
    /**
     * The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.5.4">503 Service Unavailable</a>
     */
    public static final Status SERVER_ERROR_SERVICE_UNAVAILABLE;
    
    /**
     * The server has an internal configuration error: the chosen variant resource is 
     * configured to engage in transparent content negotiation itself, and is therefore 
     * not a proper end point in the negotiation process.
     * @see <a href="http://tools.ietf.org/html/rfc2295#section-8.1">506 Variant Also Negotiates</a>        
     */
    public static final Status SERVER_ERROR_VARIANT_ALSO_NEGOTIATES;
            
    /**
     * The request has been accepted for processing, but the processing has not been completed.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.3">202 Accepted</a>
     */
    public static final Status SUCCESS_ACCEPTED;
            
    /**
     * Already Reported
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.1">208 Already Reported</a>
     */
    public static final Status SUCCESS_ALREADY_REPORTED;
            
    /**
     * The request has been fulfilled and resulted in a new resource being created.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.2">201 Created</a>
     */
    public static final Status SUCCESS_CREATED;
            
    /**
     * The response is a representation of the result of one or 
     * more instance-manipulations applied to the current instance.
     * @see <a href="http://tools.ietf.org/html/rfc3229#section-10.4.1">226 IM Used</a>
     */
    public static final Status SUCCESS_IM_USED;
            
    /**
     * Multiple resources were to be affected by the COPY, but errors on some of them prevented the operation from taking place.
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.1">207 Multi-Status</a>
     */
    public static final Status SUCCESS_MULTI_STATUS;   
            
    /**
     * The server has fulfilled the request but does not need to return an entity-body, and might want to return updated metainformation.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.5">204 No Content</a>
     */
    public static final Status SUCCESS_NO_CONTENT;
    
    /**
     * The returned metainformation in the entity-header is not 
     * the definitive set as available from the origin server, 
     * but is gatheredfrom a local or a third-party copy.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.4">203 Non-Authoritative Information</a>
     */
    public static final Status SUCCESS_NON_AUTHORITATIVE_INFORMATION;
            
    /**
     * The request has succeeded.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.1">200 OK</a>
     */
    public static final Status SUCCESS_OK;
            
    /**
     * The server has fulfilled the partial GET request for the resource.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.7">206 Partial Content</a>
     */
    public static final Status SUCCESS_PARTIAL_CONTENT;
            
    /**
     * The server has fulfilled the request and the user agent SHOULD reset the document view which caused the request to be sent.
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-7.2.6">205 Reset Content</a>
     */
    public static final Status SUCCESS_RESET_CONTENT;
    
    // Define these in a block to guarantee that REGISTERED_STATUSES is initialized before static calls to register()
    static {
    	CLIENT_ERROR_BAD_REQUEST = register(
                400,
                "Bad Request",
                "The request could not be understood by the server due to malformed syntax.");

        CLIENT_ERROR_CONFLICT = register(
                409,
                "Conflict",
                "The request could not be completed due to a conflict with the current state of the resource.");
        
        CLIENT_ERROR_EXPECTATION_FAILED = register(
                417,
                "Expectation Failed",
                "The expectation given in an Expect request-header field could not be met by this server."); 
        
        CLIENT_ERROR_FAILED_DEPENDENCY = register(       
                424,
                "Failed Dependency",
                "The method could not be performed on the resource because the requested action depended on another action and that action failed.");
                          
        CLIENT_ERROR_FORBIDDEN = register(
                403,
                "Forbidden",
                "The server understood the request, but is refusing to authorize it.");
        
        CLIENT_ERROR_GONE = register(
                410,
                "Gone",
                "The requested resource is no longer available at the server and no forwarding address is known. This condition is expected to be considered permanent.");        
                
        CLIENT_ERROR_LENGTH_REQUIRED = register(
                411,
                "Length Required",
                "The server refuses to accept the request without a defined Content-Length.");
        
        CLIENT_ERROR_LOCKED = register(
                423,
                "Locked",
                "The source or destination resource of a method is locked.");
                
        CLIENT_ERROR_METHOD_NOT_ALLOWED = register(
                405,
                "Method Not Allowed",
                "The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.");
       
        CLIENT_ERROR_NOT_ACCEPTABLE = register(
                406,
                "Not Acceptable",
                "The resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.");
                
        CLIENT_ERROR_NOT_FOUND = register(
                404,
                "Not Found",
                "The server has not found anything matching the Request-URI.");
                
        CLIENT_ERROR_PRECONDITION_FAILED = register(
                412,
                "Precondition Failed",
                "The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.");       
                
        CLIENT_ERROR_PROXY_AUTHENTICATED = register(
                407,
                "Proxy Authentication Required",
                "Indicates that the client must first authenticate itself with the proxy.");

        CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE = register(
                413,
                "Request Entity Too Large",
                "The server is refusing to process a request because the request entity is larger than the server is willing or able to process.");
                
        CLIENT_ERROR_REQUEST_TIMEOUT = register(
                408,
                "Request Timeout",
                "The client did not produce a request within the time that the server was prepared to wait.");
                
        CLIENT_ERROR_REQUEST_URI_TOO_LONG = register(
                414,
                "Request-URI Too Long",
                "The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.");
                
        CLIENT_ERROR_REQUESTED_RANGE_NOT_SATISFIABLE = register(
                416,
                "Requested Range Not Satisfiable",
                "Request included a Range request-header field, and none of the range-specifier values in this field overlap the current extent of the selected resource.");
                
        CLIENT_ERROR_UNAUTHORIZED = register(
                401,
                "Unauthorized",
                "The request requires user authentication.");
                
        CLIENT_ERROR_UNPROCESSABLE_ENTITY = register(
                422,
                "Unprocessable Entity",
                "The server understands the content type of the request entity, and the syntax of the request entity is correct but was unable to process the contained instructions.");  
                
        CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE = register(
                415,
                "Unsupported Media Type",
                "The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.");
        
        CLIENT_ERROR_UPGRADE_REQUIRED = register(
                426,
                "Upgrade Required",
                "Allows a server to definitively state the precise protocol extensionsa given resource must be served with.");
                
        INFORMATIONAL_CONTINUE = register(
                100,
                "Continue",
                "The client SHOULD continue with its request."
                );        
        
        INFORMATIONAL_PROCESSING = register(
                102,
                "Processing",
                "The server has accepted the complete request, but has not yet completed it.");
                
        INFORMATIONAL_SWITCHING_PROTOCOLS = register(
                101,
                "Switching Protocols",
                "The server understands and is willing to comply with the client's request for a change in the application protocol being used on this connection.");
                
        REDIRECTION_FOUND = register(
                302,
                "Found",
                "The requested resource resides temporarily under a different URI.");
                
        REDIRECTION_MOVED_PERMANENTLY = register(
                301,
                "Moved Permanently",
                "The requested resource has been assigned a new permanent URI and any future references to this resource SHOULD use one of the returned URIs.");
                
        REDIRECTION_MULTIPLE_CHOICES = register(
                300,
                "Multiple Choices",
                "The requested resource corresponds to any one of a set of representations, each with its own specific location, and agent-driven negotiation information is being provided so that the user (or user agent) can select a preferred representation and redirect its request to that location.");
                
        REDIRECTION_NOT_MODIFIED = register(
                304,
                "Not Modified",
                "The response to the request has not been modified since the conditions indicated by the client's conditional GET request.");
                
        REDIRECTION_SEE_OTHER = register(
                303,
                "See Other",
                "The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on that resource."); 
                
        REDIRECTION_TEMPORARY_REDIRECT = register(
                307,
                "Temporary Redirect",
                "The requested resource resides temporarily under a different URI.");
                
        REDIRECTION_USE_PROXY = register(
                305,
                "Use Proxy",
                "The requested resource MUST be accessed through the proxy given by the Location field.");
                
        SERVER_ERROR_BAD_GATEWAY = register(  
                502,
                "Bad Gateway",
                "The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.");
                
        SERVER_ERROR_GATEWAY_TIMEOUT = register(
                504,
                "Gateway Timeout",
                "The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server specified by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS) it needed to access in attempting to complete the request.");
                
        SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED = register(
                505,
                "HTTP Version Not Supported",
                "The server does not support, or refuses to support, the HTTP protocol version that was used in the request message.");
                
        SERVER_ERROR_INSUFFICIENT_STORAGE = register(
                507,
                "Insufficient Storage",
                "The method could not be performed on the resource because the server is unable to store the representation needed to successfully complete the request.");
                
        SERVER_ERROR_INTERNAL = register(
                500,
                "Internal Server Error",
                "The server encountered an unexpected condition which prevented it from fulfilling the request.");
                
        SERVER_ERROR_LOOP_DETECTED = register(
                508,
                "Loop Detected",
                "The server terminated an operation because it encountered an infinite loop while processing the request.");
                
        SERVER_ERROR_NOT_EXTENDED = register(
                510,
                "Not Extended",
                "The policy for accessing the resource has not been met in the request.");
                
        SERVER_ERROR_NOT_IMPLEMENTED = register(
                501,
                "Not Implemented",
                "The server does not support the functionality required to fulfill the request.");
                
        SERVER_ERROR_SERVICE_UNAVAILABLE = register(
                503,
                "Service Unavailable",
                "The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.");
        
        SERVER_ERROR_VARIANT_ALSO_NEGOTIATES = register(
                506,
                "Variant Also Negotiates",
                "The server has an internal configuration error: the chosen variant resource is configured to engage in transparent content negotiation itself, and is therefore not a proper end point in the negotiation process.");
                
        SUCCESS_ACCEPTED = register(
                202,
                "Accepted",
                "The request has been accepted for processing, but the processing has not been completed."); 
                
        SUCCESS_ALREADY_REPORTED = register(
                208,
                "Already Reported",
                "Already Reported.");        
                
        SUCCESS_CREATED = register(
                201,
                "Created",
                "The request has been fulfilled and resulted in a new resource being created.");
                
        SUCCESS_IM_USED = register(
                226,
                "IM Used",
                "The response is a representation of the result of one or more instance-manipulations applied to the current instance.");
                
        SUCCESS_MULTI_STATUS = register(
                207,
                "Multi-Status",
                "Multiple resources were to be affected by the COPY, but errors on some of them prevented the operation from taking place.");          
                
        SUCCESS_NO_CONTENT = register(
                204,
                "No Content",
                "The server has fulfilled the request but does not need to return an entity-body, and might want to return updated metainformation.");
                
        SUCCESS_NON_AUTHORITATIVE_INFORMATION = register(
                203,
                "Non-Authoritative Information",
                "The returned metainformation in the entity-header is not the definitive set as available from the origin server, but is gatheredfrom a local or a third-party copy.");
                
        SUCCESS_OK = register(
                200,
                "OK",
                "The request has succeeded.");
                
        SUCCESS_PARTIAL_CONTENT = register(
                206,
                "Partial Content",
                "The server has fulfilled the partial GET request for the resource.");
                
        SUCCESS_RESET_CONTENT = register(
                205,
                "Reset Content",
                "The server has fulfilled the request and the user agent SHOULD reset the document view which caused the request to be sent.");
                
    }
    
    /**
     * Returns a Status instance for {@code code}
     * @param code any valid status code between 100 and 999 inclusive.
     * @throws IllegalArgumentException if {@code code} is not a valid status code.
     */
    public static Status forCode(final int code) {
    	Preconditions.checkArgument((code >= 100) && (code < 1000));
    	
        return Objects.firstNonNull(
                REGISTERED_STATUSES.get(code), 
                new Status(code, "", ""));
    }
            
    private static Status register(
            final int code,
            final String reason,
            final String message) {
        Preconditions.checkArgument((code >= 100) && (code < 1000));
        Preconditions.checkNotNull(reason);
        Preconditions.checkArgument(CharMatchers.REASON_PHRASE.matchesAllOf(reason));
        Preconditions.checkNotNull(message);

        final Status status = new Status(code, reason, message);
        return Objects.firstNonNull(
                REGISTERED_STATUSES.putIfAbsent(code, status), status);
    }
    
    private final int code;
    private final String message;
    private final String reason;
    private final Class statusClass;
    
    private Status(
            final int code,
            final String reason,
            final String message) {
        this.statusClass = Class.getStatusClassForCode(code);
        this.code = code;
        this.reason = reason;
        this.message = message;
    }
    
    /**
     * Returns the status code of this status.
     */
    public int code() {
        return this.code;
    }
    
    /**
     * Determines if two Status objects are considered equal. 
     * Equality is defined as this.code() == that.code().
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Status) {
            final Status that = (Status) obj;
            return this.code == that.code;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.code;
    }

    /**
     * Returns a human readable message describing this status
     * that is suitable to be included in a response entity.
     */
    public String message() {
        return this.message;
    }

    /**
     * Returns the reason message for this status.
     */
    public String reason() {
        return this.reason;
    }

    /** 
     * Returns the Status.Class of this status.
     */
    public Status.Class statusClass() {
        return this.statusClass;
    }
    
    /**
     * Returns a Response with this status. 
     */
    public Response toResponse() {
        return Response.builder()
                .setStatus(this)
                .setEntity(this.message())
                .build();
    }
    
    @Override
    public String toString() {
        return this.code + " " + this.reason;
    }
}
