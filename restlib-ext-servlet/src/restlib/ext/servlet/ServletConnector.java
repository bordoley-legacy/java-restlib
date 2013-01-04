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


package restlib.ext.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.ResponseWrapper;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Method;
import restlib.data.Status;
import restlib.net.UriSchemes;
import restlib.server.FutureResponses;
import restlib.server.bio.BioApplication;
import restlib.server.bio.BioResource;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@SuppressWarnings("serial")
public abstract class ServletConnector extends GenericServlet { 
    private static final Logger logger = LoggerFactory.getLogger(ServletConnector.class);
  
    private static void sendResponse(
            final Request request, 
            final Response response, 
            final HttpServletResponse servletResponse,
            final BioResource<?> resource) {
        
        try {
            final OutputStreamSerializer serializer = 
                    resource.getResponseEntitySerializer(request, response);
            
            ServletConnectorResponse.newInstance(servletResponse).populate(
                    new ResponseWrapper(response) {
                        @Override
                        public ContentInfo contentInfo() {
                            return serializer.contentInfo();
                        }});
            
            if (!request.method().equals(Method.HEAD)) {
                serializer.write(servletResponse.getOutputStream());
            }
        } catch (final IOException e) {
            logger.error("", e);
        }
    }
    
    private static Iterable<Entry<String, String>> servletRequestHeaders(final HttpServletRequest servletRequest) {
        return  new Iterable<Entry<String,String>> () {
            @Override
            public Iterator<Entry<String, String>> iterator() {
                final Iterator<String> names = 
                        Iterators.forEnumeration(servletRequest.getHeaderNames());
          
                return new Iterator<Entry<String, String>>() {
                    String name = "";
                    Iterator<String> values = Iterators.emptyIterator();
                    
                    @Override
                    public boolean hasNext() {
                        if (values.hasNext()) {
                            return true;
                        } 
                        
                        while (names.hasNext()) {
                            name = names.next();
                            values = Iterators.forEnumeration(servletRequest.getHeaders(name.toString()));
                            if (values.hasNext()) {
                                return true;
                            }
                            
                        }
                        return false;
                    }

                    @Override
                    public Entry<String, String> next() {
                        if (values.hasNext()) {
                            final String value = values.next();
                            return new AbstractMap.SimpleImmutableEntry<String, String>(
                                name, value);
                        } 
                        
                        while (names.hasNext()) {
                            name = names.next();
                            values = Iterators.forEnumeration(servletRequest.getHeaders(name.toString()));
                            
                            if (values.hasNext()) {
                                final String value = values.next();
                                return new AbstractMap.SimpleImmutableEntry<String, String>(
                                    name, value);
                            }
                        }
                        throw new NoSuchElementException();                    
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }                 
                };
            }    
        };
    }
     
    private static String servletRequestTarget(final HttpServletRequest servletRequest) {
        return Strings.nullToEmpty(servletRequest.getRequestURI()) + "?" + 
                Strings.nullToEmpty(servletRequest.getQueryString());
    }
    

    public ServletConnector() {    
    }

    protected abstract Function<Request, BioApplication> applicationSupplier();
    
    protected long continuationTimeout() {
        return -1;
    }
    
    private boolean handle(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws InterruptedException, ExecutionException {
        final BioApplication application;
        final Request request;
        final BioResource<?> resource;
        ListenableFuture<Response> response; 
        
        final Object requestAttribute = servletRequest.getAttribute("request");
        if (requestAttribute == null) {
            final Request unfilteredRequest = 
                    Request.parse(
                            this.uriScheme(), 
                            servletRequest.getMethod(), 
                            servletRequestTarget(servletRequest), 
                            servletRequestHeaders(servletRequest));
            application = this.applicationSupplier().apply(unfilteredRequest); 
            request = application.requestFilter().apply(unfilteredRequest);
            resource = application.getResource(request);
            response = resource.handle(request);
        } else { 
            // Process the continuation
            application = (BioApplication) Preconditions.checkNotNull(servletRequest.getAttribute("application"));
            request = (Request) requestAttribute;
            resource = (BioResource<?>) Preconditions.checkNotNull(servletRequest.getAttribute("resource"));
            response = Futures.immediateFuture((Response) Preconditions.checkNotNull(servletRequest.getAttribute("response")));      
        }  
        
        if (response.isDone() && response.get().status().equals(Status.INFORMATIONAL_CONTINUE)) {           
            try {
                // On jetty calling getInputStream() results in sending
                // 100-continue to the client
                final InputStream requestInputStream = servletRequest.getInputStream();
                final Object message = resource.getRequestEntityDeserializer(request).read(requestInputStream);
                response = resource.acceptMessage(request, message);
            } catch (final IOException e) {
                response = FutureResponses.CLIENT_ERROR_BAD_REQUEST;
            }
        }
        
        if (response.isDone()) {
            Response finalResponse = application.responseFilter().apply(response.get());
            sendResponse(request, finalResponse, servletResponse, resource);
            return true;
        } else {
            final ListenableFuture<Response> futureResponse = response;
            final Continuation continuation = ContinuationSupport.getContinuation(servletRequest);
            continuation.suspend();
            continuation.setTimeout(this.continuationTimeout());
            continuation.setAttribute("application", application);
            continuation.setAttribute("request", request);
            continuation.setAttribute("resource", resource);
            continuation.addContinuationListener(
                    new ContinuationListener() {
                        @Override
                        public void onComplete(final Continuation jettyContinuation) {
                            // Do Nothing
                        }

                        @Override
                        public void onTimeout(final Continuation jettyContinuation) {
                            futureResponse.cancel(false);
                        }
                    });
            
            final FutureCallback<Response> futureCallback = new FutureCallback<Response>() {
                @Override
                public void onFailure(final Throwable exception) {
                    // FIXME: handle all the different cases.
                    final Response response = 
                            Response.builder()
                                .setStatus(Status.SERVER_ERROR_INTERNAL)
                                .setEntity("The server timed out while processing the request.")
                                .build();
                    resume(response);     
                }

                @Override
                public void onSuccess(final Response response) {
                    resume(response);             
                }        
                
                private void resume (final Response response) {
                    continuation.setAttribute("response", response);
                    continuation.resume();  
                }
            };
            
            Futures.addCallback(response, futureCallback);
            return false;
        }
    }
    
    protected boolean printExceptions() {
      return false;
    }
    
    private void service(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
        boolean flushOutput = true;
        try {
            flushOutput = this.handle(servletRequest, servletResponse);
        } catch (final Throwable e) {
            logger.error("", e);
           /*
            final BioApplication defaultApplication = defaultApplication();
            final Response response;         
            
            if (this.printExceptions()) {
                final StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                
                response = Response.builder()
                            .setStatus(Status.SERVER_ERROR_INTERNAL)
                            .setEntity(stringWriter.toString()).build();
            } else {
                response = Status.SERVER_ERROR_INTERNAL.toResponse();
            }         
            
            final BioResource<?> defaultResource = defaultApplication.getResource(Request.NONE);
            sendResponse(Request.NONE, defaultApplication.responseFilter().apply(response), 
                    servletResponse, defaultResource);
            */
            flushOutput = true;
        }

        if (flushOutput) {
            try {
                servletResponse.getOutputStream().flush();
                servletResponse.getOutputStream().close();
            } catch (final IOException e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {        
        try {
            final HttpServletRequest request = (HttpServletRequest) req;
            final HttpServletResponse response = (HttpServletResponse) res;
            
            service(request, response);
        } catch (final ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }      
    }
    
    protected String uriScheme() {
        return UriSchemes.HTTP;
    }
}   
