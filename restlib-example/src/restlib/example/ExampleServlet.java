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


package restlib.example;

import restlib.Request;
import restlib.Response;
import restlib.data.ExtensionHeaders;
import restlib.data.HttpHeaders;
import restlib.example.async.BioContinuationResource;
import restlib.example.blog.bio.BlogBuilder;
import restlib.example.echo.BioEchoResource;
import restlib.example.echo.EchoResource;
import restlib.ext.servlet.ServletConnector;
import restlib.server.ApplicationSuppliers;
import restlib.server.Authorizer;
import restlib.server.BasicAuthorizer;
import restlib.server.FutureResponses;
import restlib.server.RequestFilters;
import restlib.server.Resource;
import restlib.server.Resources;
import restlib.server.Route;
import restlib.server.bio.BioApplication;
import restlib.server.bio.BioApplicationBuilder;
import restlib.server.bio.BioResource;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

@SuppressWarnings("serial")
public final class ExampleServlet extends ServletConnector { 
    private static BioResource<?> echoResourceWithAuth(final Route route) {
        final Authorizer authorizer = 
                new BasicAuthorizer("www.example.com") {
                    private final String pwd = "test";
                    private final String user = "test";
                
                    @Override
                    protected ListenableFuture<Response> authenticate(final Request request, final String user, final String pwd) {
                        return (this.user.equals(user) && this.pwd.equals(pwd)) ? 
                                    FutureResponses.SUCCESS_OK :
                                        FutureResponses.CLIENT_ERROR_UNAUTHORIZED;
                    }
            };
        
        final Resource authEchoResource = 
                Resources.authorizedResource(
                        EchoResource.newInstance(route), 
                        ImmutableList.of(authorizer));
 
        return BioEchoResource.newInstance(authEchoResource);   
    }
    
    private final Function<Request, BioApplication> applicationSupplier;

    public ExampleServlet() {        
        final Route echoWithAuth = Route.startsWith("/example/echo/*/auth");
        final Route echo = Route.startsWith("/example/echo").exclude(echoWithAuth);
        final Route continuation = Route.parse("/example/continuation");
        final Route feed = Route.parse("/example/blog");
        final Route entry = Route.parse("/example/blog/entries/:id");
        
        final BlogBuilder blogBuilder = BlogBuilder.newInstance(feed, entry);
        
        final BioApplication application = BioApplicationBuilder
                .newInstance()
                .addRequestFilter(
                        RequestFilters.queryFilter(
                                ImmutableList.of(
                                        ExtensionHeaders.X_HTTP_METHOD_OVERRIDE,
                                        HttpHeaders.ACCEPT)))
                .addRequestFilter(RequestFilters.DEFAULT_EXTENSION_FILTER)                       
                .addResource(BioEchoResource.newInstance(EchoResource.newInstance(echo)))
                .addResource(echoResourceWithAuth(echoWithAuth))
                .addResource(BioContinuationResource.newInstance(continuation))
                .addResource(blogBuilder.bioEntryResource)
                .addResource(blogBuilder.bioFeedResource)
                .build();

        this.applicationSupplier = ApplicationSuppliers.constant(application);
    }   

    @Override
    protected Function<Request, BioApplication> applicationSupplier() {
        return this.applicationSupplier;
    }

    @Override
    protected boolean printExceptions() {
        return true;
    }

    @Override
    protected String uriScheme() {
        return "http";
    }
}
