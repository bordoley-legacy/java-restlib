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


package restlib.server.connector;

import java.util.List;
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
import restlib.data.Header;
import restlib.data.HttpDate;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.data.Protocol;
import restlib.data.Range;
import restlib.data.TransferCoding;
import restlib.data.UserAgent;
import restlib.data.Via;
import restlib.net.Uri;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public final class ConnectorHelpers {
	public static Request appendTrailers(final Request request, final Iterable<? extends Entry<String, String>> headers) {
		return null;
	}
	
    public static boolean isChunked(final Request request) {
        return Iterables.contains(request.connectionInfo().transferEncodings(), TransferCoding.CHUNKED);
    }
    
    
    public static boolean isContinueExpected(final Request request) {
        return Iterables.contains(request.expectations(), Expectation.EXPECTS_100_CONTINUE);
    }


    public static boolean isKeepAlive(final Request request) {
        // In HTTP 1.1 all connections are considered persistent unless declared otherwise.
        return !Iterables.contains(request.connectionInfo().options(), ConnectionOption.CLOSE);
    }
    
    
    private static boolean isTrailerHeader(final Request request, final Header header) {
        return request.connectionInfo().trailerHeaders().contains(header);
    }
    
    private static Request withHeaders(final Request request, final Iterable<? extends Entry<Header,String>> headers) {
    	Preconditions.checkNotNull(headers);
        final Request headerRequest = 
                Request.parse(
                        request.uri().scheme(), 
                        request.method(), 
                        request.uri(), headers);
        
        final ConnectionInfo connectionInfo = new ConnectionInfoWrapper(request.connectionInfo()) {
            @Override
            public Set<ConnectionOption> options() {
                if(!headerRequest.connectionInfo().options().isEmpty()) {
                    return ImmutableSet.<ConnectionOption> builder()
                                .addAll(headerRequest.connectionInfo().options())
                                .addAll(super.options())
                                .build();
                }
                return super.options();
            }

			@Override
			public Set<Header> trailerHeaders() {
            	if (!headerRequest.connectionInfo().trailerHeaders().isEmpty()) {
            		return ImmutableSet.<Header> builder()
                            .addAll(headerRequest.connectionInfo().trailerHeaders())
                            .addAll(super.trailerHeaders())
                            .build();
            	}

				return super.trailerHeaders();
			}

			@Override
			public List<TransferCoding> transferEncodings() {
				if (!headerRequest.connectionInfo().transferEncodings().isEmpty()) {		
            		return ImmutableList.<TransferCoding> builder()
                            .addAll(super.transferEncodings())
                            .addAll(headerRequest.connectionInfo().transferEncodings())
                            .build();
            	}
				return super.transferEncodings();
			}

            @Override
            public Set<Protocol> upgradeProtocols() {
                if(!headerRequest.connectionInfo().upgradeProtocols().isEmpty()) {
                    return ImmutableSet.<Protocol> builder()
                            .addAll(headerRequest.connectionInfo().upgradeProtocols())
                            .addAll(super.upgradeProtocols())
                            .build();
                }
                return super.upgradeProtocols();
            }

            @Override
            public List<Via> via() {
                if(!headerRequest.connectionInfo().via().isEmpty()) {
                   return ImmutableList.<Via> builder()
                           		.addAll(super.via())                		   
                           		.addAll(headerRequest.connectionInfo().via())
                           		.build();
               }
               return super.via();
            }  
        };
        
        final ContentInfo contentInfo = new ContentInfoWrapper(request.contentInfo()) {
            @Override
            public List<ContentEncoding> encodings() {
                if (!headerRequest.contentInfo().encodings().isEmpty()) {
                    return ImmutableList.<ContentEncoding> builder()
                            	.addAll(super.encodings())
                                .addAll(headerRequest.contentInfo().encodings())
                                .build();
                }
                return super.encodings();
            }

            @Override
            public Set<Language> languages() {
                if (!headerRequest.contentInfo().languages().isEmpty()) {
                    return ImmutableSet.<Language> builder()
                                .addAll(headerRequest.contentInfo().languages())
                                .addAll(super.languages())
                                .build();
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
                    return ImmutableSet.<EntityTag> builder()
                            .addAll(headerRequest.preconditions().ifMatchTags())
                            .addAll(super.ifMatchTags())
                            .build();
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
                    return ImmutableSet.<EntityTag> builder()
                                .addAll(headerRequest.preconditions().ifNoneMatchTags())
                                .addAll(super.ifNoneMatchTags())
                                .build();
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
                    return ImmutableSet.<Preference<Charset>> builder()
                                .addAll(headerRequest.preferences().acceptedCharsets())
                                .addAll(super.acceptedCharsets())
                                .build();
                }
                return super.acceptedCharsets();
            }

            @Override
            public Set<Preference<ContentEncoding>> acceptedEncodings() {
                if (!headerRequest.preferences().acceptedEncodings().isEmpty()){
                    return ImmutableSet.<Preference<ContentEncoding>> builder()
                            .addAll(headerRequest.preferences().acceptedEncodings())
                            .addAll(super.acceptedEncodings())
                            .build();
                }
                return super.acceptedEncodings();
            }

            @Override
            public Set<Preference<Language>> acceptedLanguages() {
                if (!headerRequest.preferences().acceptedLanguages().isEmpty()){
                    return ImmutableSet.<Preference<Language>> builder()
                            .addAll(headerRequest.preferences().acceptedLanguages())
                            .addAll(super.acceptedLanguages())
                            .build();
                }
                return super.acceptedLanguages();
            }

            @Override
            public Set<Preference<MediaRange>> acceptedMediaRanges() {
                if (!headerRequest.preferences().acceptedMediaRanges().isEmpty()){
                    return ImmutableSet.<Preference<MediaRange>> builder()
                            .addAll(headerRequest.preferences().acceptedMediaRanges())
                            .addAll(super.acceptedMediaRanges())
                            .build();
                }
                return super.acceptedMediaRanges();
            }

            @Override
            public Set<Preference<TransferCoding>> acceptedTransferEncodings() {
                if (!headerRequest.preferences().acceptedTransferEncodings().isEmpty()){
                    return ImmutableSet.<Preference<TransferCoding>> builder()
                            .addAll(headerRequest.preferences().acceptedTransferEncodings())
                            .addAll(super.acceptedTransferEncodings())
                            .build();
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
                    return ImmutableSet.<CacheDirective> builder()
                            .addAll(headerRequest.cacheDirectives())
                            .addAll(super.cacheDirectives())
                            .build();
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
                    return ImmutableSet.<Expectation> builder()
                            .addAll(headerRequest.expectations())
                            .addAll(super.expectations())
                            .build();
                }
                return super.expectations();
            }

            @Override
            public Optional<Integer> maxForwards() {
                if (headerRequest.maxForwards().isPresent()) { 
                    return headerRequest.maxForwards();
                }
                return super.maxForwards();
            }

            @Override
            public Set<CacheDirective> pragmaCacheDirectives() {
                if (!headerRequest.pragmaCacheDirectives().isEmpty()) { 
                    return ImmutableSet.<CacheDirective> builder()
                            .addAll(headerRequest.pragmaCacheDirectives())
                            .addAll(super.pragmaCacheDirectives())
                            .build();
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
            public Optional<UserAgent> userAgent() {
                if (headerRequest.userAgent().isPresent() ) { 
                    return headerRequest.userAgent();
                }
                return super.userAgent();
            }
        };
    }
    
    private ConnectorHelpers(){}
}
