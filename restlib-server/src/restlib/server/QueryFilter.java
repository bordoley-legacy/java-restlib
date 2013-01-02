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


package restlib.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import restlib.ClientPreferences;
import restlib.ClientPreferencesWrapper;
import restlib.ConnectionInfo;
import restlib.ConnectionInfoWrapper;
import restlib.ContentInfo;
import restlib.ContentInfoWrapper;
import restlib.Request;
import restlib.RequestPreconditions;
import restlib.RequestPreconditionsWrapper;
import restlib.RequestWrapper;
import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.Charset;
import restlib.data.ConnectionOption;
import restlib.data.ContentEncoding;
import restlib.data.ContentRange;
import restlib.data.EntityTag;
import restlib.data.Expectation;
import restlib.data.ExtensionHeaders;
import restlib.data.Form;
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Method;
import restlib.data.Preference;
import restlib.data.Protocol;
import restlib.data.Range;
import restlib.data.TransferCoding;
import restlib.data.UserAgent;
import restlib.data.Via;
import restlib.net.EmailAddress;
import restlib.net.Uri;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

final class QueryFilter implements Function<Request, Request> {       
    private static Request withHeaders(
    		final Request request, 
    		final Method method,
    		final Uri uri,   		
    		final Iterable<? extends Entry<Header,String>> headers) {
    	Preconditions.checkNotNull(headers);
        final Request headerRequest = 
                Request.parse(
                        request.uri().scheme(), 
                        method, 
                        uri, headers);
        
        final ConnectionInfo connectionInfo = new ConnectionInfoWrapper(request.connectionInfo()) {
            @Override
            public Set<ConnectionOption> options() {
                if(!headerRequest.connectionInfo().options().isEmpty()) {
                    return headerRequest.connectionInfo().options();
                }
                return super.options();
            }

			@Override
			public Set<Header> trailerHeaders() {
            	if (!headerRequest.connectionInfo().trailerHeaders().isEmpty()) {
            		return headerRequest.connectionInfo().trailerHeaders();
            	}

				return super.trailerHeaders();
			}

			@Override
			public List<TransferCoding> transferEncodings() {
				if (!headerRequest.connectionInfo().transferEncodings().isEmpty()) {		
            		return headerRequest.connectionInfo().transferEncodings();
            	}
				return super.transferEncodings();
			}

            @Override
            public Set<Protocol> upgradeProtocols() {
                if(!headerRequest.connectionInfo().upgradeProtocols().isEmpty()) {
                    return headerRequest.connectionInfo().upgradeProtocols();
                }
                return super.upgradeProtocols();
            }

            @Override
            public List<Via> via() {
                if(!headerRequest.connectionInfo().via().isEmpty()) {
                   return headerRequest.connectionInfo().via();
               }
               return super.via();
            }  
        };
        
        final ContentInfo contentInfo = new ContentInfoWrapper(request.contentInfo()) {
            @Override
            public List<ContentEncoding> encodings() {
                if (!headerRequest.contentInfo().encodings().isEmpty()) {
                    return headerRequest.contentInfo().encodings();
                }
                return super.encodings();
            }

            @Override
            public Set<Language> languages() {
                if (!headerRequest.contentInfo().languages().isEmpty()) {
                    return headerRequest.contentInfo().languages();
                }
                return super.languages();
            }

            @Override
            public Optional<Uri> location() {
                if (headerRequest.contentInfo().location().isPresent()) {
                    return headerRequest.contentInfo().location();
                }
                return super.location();
            }

            @Override
            public Optional<MediaRange> mediaRange() {
                if (headerRequest.contentInfo().mediaRange().isPresent()) {
                    return headerRequest.contentInfo().mediaRange();
                }
                return super.mediaRange();
            }

            @Override
            public Optional<ContentRange> range() {
                if (headerRequest.contentInfo().range().isPresent()) {
                    return headerRequest.contentInfo().range();
                }
                return super.range();
            }                  
        };
        
        final RequestPreconditions preconditions = new RequestPreconditionsWrapper(request.preconditions()) {
            @Override
            public Set<EntityTag> ifMatchTags() {
                if (!headerRequest.preconditions().ifMatchTags().isEmpty()) {
                    return headerRequest.preconditions().ifMatchTags();
                }
                return super.ifMatchTags();
            }

            @Override
            public Optional<HttpDate> ifModifiedSinceDate() {
                if (headerRequest.preconditions().ifModifiedSinceDate().isPresent()) {
                    return headerRequest.preconditions().ifModifiedSinceDate();
                }
                return super.ifModifiedSinceDate();
            }

            @Override
            public Set<EntityTag> ifNoneMatchTags() {
                if (!headerRequest.preconditions().ifNoneMatchTags().isEmpty()) {
                    return headerRequest.preconditions().ifNoneMatchTags();
                }
                return super.ifNoneMatchTags();
            }

            @Override
            public Optional<Object> ifRange() {
                if (headerRequest.preconditions().ifRange().isPresent()) {
                    return headerRequest.preconditions().ifRange();
                }
                return super.ifRange();
            }

            @Override
            public Optional<HttpDate> ifUnmodifiedSinceDate() {
                if (headerRequest.preconditions().ifUnmodifiedSinceDate().isPresent()) {
                    return headerRequest.preconditions().ifUnmodifiedSinceDate();
                }
                return super.ifUnmodifiedSinceDate();
            }       
        };
        
        
        final ClientPreferences preferences = new ClientPreferencesWrapper(request.preferences()){
            @Override
            public Set<Preference<Charset>> acceptedCharsets() {
                if (!headerRequest.preferences().acceptedCharsets().isEmpty()){
                    return headerRequest.preferences().acceptedCharsets();
                }
                return super.acceptedCharsets();
            }

            @Override
            public Set<Preference<ContentEncoding>> acceptedEncodings() {
                if (!headerRequest.preferences().acceptedEncodings().isEmpty()){
                    return headerRequest.preferences().acceptedEncodings();
                }
                return super.acceptedEncodings();
            }

            @Override
            public Set<Preference<Language>> acceptedLanguages() {
                if (!headerRequest.preferences().acceptedLanguages().isEmpty()){
                    return headerRequest.preferences().acceptedLanguages();
                }
                return super.acceptedLanguages();
            }

            @Override
            public Set<Preference<MediaRange>> acceptedMediaRanges() {
                if (!headerRequest.preferences().acceptedMediaRanges().isEmpty()){
                    return headerRequest.preferences().acceptedMediaRanges();
                }
                return super.acceptedMediaRanges();
            }

            @Override
            public Set<Preference<TransferCoding>> acceptedTransferEncodings() {
                if (!headerRequest.preferences().acceptedTransferEncodings().isEmpty()){
                    return headerRequest.preferences().acceptedTransferEncodings();
                }
                return super.acceptedTransferEncodings();
            }

            @Override
            public Optional<Range> range() {
                if (headerRequest.preferences().range().isPresent()){
                    return headerRequest.preferences().range();
                }
                return super.range();
            }                   
        };
        
        return new RequestWrapper(request) {  
            @Override
            public Optional<ChallengeMessage> authorizationCredentials() {
                if (headerRequest.authorizationCredentials().isPresent()) {
                    return headerRequest.authorizationCredentials();
                }
                return super.authorizationCredentials();
            }

            @Override
            public Set<CacheDirective> cacheDirectives() {
                if (!headerRequest.cacheDirectives().isEmpty()) {
                    return headerRequest.cacheDirectives();
                }
                return super.cacheDirectives();
            }

            @Override
            public ConnectionInfo connectionInfo() {
                return connectionInfo;
            }

            @Override
            public ContentInfo contentInfo() {
                return contentInfo;
            }

            @Override
            public Set<Expectation> expectations() {
                if (!headerRequest.expectations().isEmpty()) { 
                    return headerRequest.expectations();
                }
                return super.expectations();
            }

            @Override
            public Optional<EmailAddress> from() {
                if (headerRequest.from().isPresent()) {
                    return headerRequest.from();
                }               
                return super.from();
            }

            @Override
            public Optional<Integer> maxForwards() {
                if (headerRequest.maxForwards().isPresent()) { 
                    return headerRequest.maxForwards();
                }
                return super.maxForwards();
            }

            @Override
            public Method method() {
                return headerRequest.method();
            }

            @Override
            public Set<CacheDirective> pragmaCacheDirectives() {
                if (!headerRequest.pragmaCacheDirectives().isEmpty()) { 
                    return headerRequest.pragmaCacheDirectives();
                } 
                return super.pragmaCacheDirectives();
            }

            @Override
            public RequestPreconditions preconditions() {
                return preconditions;
            }

            @Override
            public ClientPreferences preferences() {
                return preferences;
            }

            @Override
            public Optional<ChallengeMessage> proxyAuthorizationCredentials() {
                if (headerRequest.proxyAuthorizationCredentials().isPresent()) {
                    return headerRequest.proxyAuthorizationCredentials();
                }
                return super.proxyAuthorizationCredentials();
            }

            @Override
            public Optional<Uri> referrer() {
                if (headerRequest.referrer().isPresent()) { 
                    return headerRequest.referrer();
                }
                return super.referrer();
            }

            @Override
            public Uri uri() {
                return headerRequest.uri();
            }

            @Override
            public Optional<UserAgent> userAgent() {
                if (headerRequest.userAgent().isPresent()) { 
                    return headerRequest.userAgent();
                }
                return super.userAgent();
            }
        };
    }
    private final Set<Header> allowedHeaders;
    
    private final Predicate<String> filterHeaderPredicate = 
            new Predicate<String>() {
                @Override
                public boolean apply(final String input) {
                    try {
                        return allowedHeaders.contains(Header.create(input));
                    } catch (final IllegalArgumentException e) {
                        return false;
                    }
                }  
    };
    
    QueryFilter(final Set<Header> allowedHeaders){   
        this.allowedHeaders = allowedHeaders;
    }
    
    @Override
    public Request apply(final Request request) {
        Preconditions.checkNotNull(request);
        
        // Parse the query component of the Uri as a form.
        // Retrieve all the allowed headers from the form and generate a new Uri
        // with the allowed headers removed from the query string.
        // Parse the header field namesin the form to make sure they are valid.
        final Uri uri;
        final Multimap<Header, String> headers;    
        try {
            final Form query = Form.parse(request.uri().query());
            uri = Uri.builder()
                    .setScheme(request.uri().scheme())
                    .setAuthority(request.uri().authority())
                    .setPath(request.uri().path())
                    .setQuery(
                            Form.builder().putAll(
                                    Multimaps.filterKeys(query,Predicates.not(filterHeaderPredicate))).build().toString())
                    .setFragment(request.uri().fragment())
                    .build();
            
            // FIXME: Seems expensive. Maybe use the multimap that is used above to generate the query
            // and do a set exclusion?
            final ImmutableSetMultimap.Builder<Header, String> headerBuilder = ImmutableSetMultimap.builder();  
            for (final Map.Entry<String, String> entry : 
                    Multimaps.filterKeys(query, filterHeaderPredicate).entries()) {
                headerBuilder.put(Header.create(entry.getKey()), entry.getValue());
            }     
            
            headers = headerBuilder.build();
        } catch (final IllegalArgumentException e) {
            return request;
        }
        
        // Special case for X_HTTP_METHOD_OVERRIDE
        final Method method;   
        if (headers.containsKey(ExtensionHeaders.X_HTTP_METHOD_OVERRIDE) && 
        		allowedHeaders.contains(ExtensionHeaders.X_HTTP_METHOD_OVERRIDE)) {
            if (request.method().equals(Method.POST)) {               
                try {
                    method = Method.forName(
                            Iterables.getFirst(
                                    headers.get(ExtensionHeaders.X_HTTP_METHOD_OVERRIDE), ""));
                } catch (final IllegalArgumentException e) {
                    return request;
                }
            } else {
                return request;
            }
        } else {
            method = request.method();
        }
        
        return withHeaders(request, method, uri, headers.entries());      
    }
}
