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


package restlib;

import static restlib.MessageHelpers.appendHeader;
import static restlib.data.HttpHeaders.ACCEPT;
import static restlib.data.HttpHeaders.ACCEPT_CHARSET;
import static restlib.data.HttpHeaders.ACCEPT_ENCODING;
import static restlib.data.HttpHeaders.ACCEPT_LANGUAGE;
import static restlib.data.HttpHeaders.AUTHORIZATION;
import static restlib.data.HttpHeaders.CACHE_CONTROL;
import static restlib.data.HttpHeaders.CONNECTION;
import static restlib.data.HttpHeaders.CONTENT_ENCODING;
import static restlib.data.HttpHeaders.CONTENT_LANGUAGE;
import static restlib.data.HttpHeaders.CONTENT_LENGTH;
import static restlib.data.HttpHeaders.CONTENT_LOCATION;
import static restlib.data.HttpHeaders.CONTENT_TYPE;
import static restlib.data.HttpHeaders.EXPECT;
import static restlib.data.HttpHeaders.FROM;
import static restlib.data.HttpHeaders.HOST;
import static restlib.data.HttpHeaders.IF_MATCH;
import static restlib.data.HttpHeaders.IF_MODIFIED_SINCE;
import static restlib.data.HttpHeaders.IF_NONE_MATCH;
import static restlib.data.HttpHeaders.IF_RANGE;
import static restlib.data.HttpHeaders.IF_UNMODIFIED_SINCE;
import static restlib.data.HttpHeaders.MAX_FORWARDS;
import static restlib.data.HttpHeaders.PRAGMA;
import static restlib.data.HttpHeaders.PROXY_AUTHORIZATION;
import static restlib.data.HttpHeaders.RANGE;
import static restlib.data.HttpHeaders.REFERER;
import static restlib.data.HttpHeaders.TE;
import static restlib.data.HttpHeaders.TRAILER;
import static restlib.data.HttpHeaders.TRANSFER_ENCODING;
import static restlib.data.HttpHeaders.UPGRADE;
import static restlib.data.HttpHeaders.USER_AGENT;
import static restlib.data.HttpHeaders.VIA;

import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.data.CacheDirective;
import restlib.data.ChallengeMessage;
import restlib.data.Expectation;
import restlib.data.Header;
import restlib.data.HttpHeaders;
import restlib.data.Method;
import restlib.data.UserAgent;
import restlib.impl.ImmutableMapEntry;
import restlib.net.EmailAddress;
import restlib.net.Uri;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
/**
 * Object representation of an HTTP Request. 
 * Implementations must be immutable or effectively immutable.
 */
@Immutable
public abstract class Request {        
    /**
     * Returns a new RequestBuilder instance.
     */
    public static RequestBuilder builder() {
        return new RequestBuilder();
    }
    
    /**
     * Parses a Request from the String representation of its components.
     * @param uriScheme the URI scheme of the request. Usually http or https.
     * @param methodName the request method name from the request line.
     * @param requestTarget the request target from the request line.
     * @param headers an Iterable of the header field name/value pairs.
     * @throws NullPointerException if any argument is null. Also if any entry 
     * in {@code headers} is null or contains null key/values.
     */
    public static Request parse(
            final String uriScheme,
            final Method method, 
            final Uri requestTarget, 
            final Iterable<? extends Entry<Header, String>> headers) {	
    	Preconditions.checkNotNull(uriScheme);
    	Preconditions.checkNotNull(method);
    	Preconditions.checkNotNull(requestTarget);
    	Preconditions.checkNotNull(headers);
    	
        final RequestBuilder requestBuilder = Request.builder();
        final ClientPreferencesBuilder preferencesBuilder = ClientPreferences.builder();
        final ConnectionInfoBuilder connectionInfoBuilder = ConnectionInfo.builder();
        final ContentInfoBuilder contentInfoBuilder = ContentInfo.builder();
        final RequestPreconditionsBuilder preconditionsBuilder = RequestPreconditions.builder();
        
        String host = "";
        
        final Function<Header, Void> checkIfHeaderIsUnique = new Function<Header,Void>() {
            final Set<Header> uniqueHeaders = Sets.newHashSet();
            
            @Override
            public Void apply(final Header fieldName) {
                Preconditions.checkArgument(!uniqueHeaders.contains(fieldName), 
                        "Request may not include the header " + fieldName.toString() + " more than once.");
                uniqueHeaders.add(fieldName);         
                return null;
            }           
        };    
        
        for (final Entry<Header, String> header : headers) {
            Preconditions.checkNotNull(header);
            
            final Header fieldName = header.getKey();
            final String fieldValue = Preconditions.checkNotNull(header.getValue());
            
            if (fieldName.equals(ACCEPT)) {
                preferencesBuilder.addAcceptedMediaRanges(
                        HttpHeaders.parseAccept(fieldValue));
            } else if (fieldName.equals(ACCEPT_CHARSET)) {  
                preferencesBuilder.addAcceptedCharsets(
                        HttpHeaders.parseAcceptCharset(fieldValue));
            } else if (fieldName.equals(ACCEPT_ENCODING)) {   
                preferencesBuilder.addAcceptedEncodings(
                        HttpHeaders.parseAcceptEncoding(fieldValue));
            } else if (fieldName.equals(ACCEPT_LANGUAGE)) {  
                preferencesBuilder.addAcceptedLanguages(
                        HttpHeaders.parseAcceptLanguage(fieldValue));  
            } else if (fieldName.equals(AUTHORIZATION)) {
                checkIfHeaderIsUnique.apply(fieldName);                
                requestBuilder.setAuthorizationCredentials(
                        HttpHeaders.parsetAuthorization(fieldValue));
            } else if (fieldName.equals(CACHE_CONTROL)) {   
                requestBuilder.addCacheDirectives(
                        HttpHeaders.parseCacheControl(fieldValue));
            } else if (fieldName.equals(CONNECTION)) {  
                connectionInfoBuilder.addConnectionOptions(
                        HttpHeaders.parseConnection(fieldValue));
            } else if (fieldName.equals(CONTENT_ENCODING)) {
                contentInfoBuilder.addEncodings(
                        HttpHeaders.parseContentEncoding(fieldValue));
            } else if (fieldName.equals(CONTENT_LANGUAGE)) {
                contentInfoBuilder.addLanguages(
                        HttpHeaders.parseContentLanguage(fieldValue));
            } else if (fieldName.equals(CONTENT_LENGTH)) { 
                checkIfHeaderIsUnique.apply(fieldName);                
                contentInfoBuilder.setLength(
                            HttpHeaders.parseContentLength(fieldValue));
            } else if (fieldName.equals(CONTENT_LOCATION)) { 
                checkIfHeaderIsUnique.apply(fieldName);               
                contentInfoBuilder.setLocation(
                        HttpHeaders.parseContentLocation(fieldValue));
            } else if (fieldName.equals(CONTENT_TYPE)) { 
                checkIfHeaderIsUnique.apply(fieldName);                
                contentInfoBuilder.setMediaRange(
                        HttpHeaders.parseContentType(fieldValue));
            } else if (fieldName.equals(EXPECT)) {
                requestBuilder.addExpectations(
                        HttpHeaders.parseExpect(fieldValue));
            } else if (fieldName.equals(FROM)) {  
                checkIfHeaderIsUnique.apply(fieldName);               
                requestBuilder.setFrom(HttpHeaders.parseFrom(fieldValue));
            } else if (fieldName.equals(HOST)) {
                checkIfHeaderIsUnique.apply(fieldName);             
                host = fieldValue;
            } else if (fieldName.equals(IF_MATCH)) {    
                preconditionsBuilder.addIfMatchTags(
                        HttpHeaders.parseIfMatch(fieldValue));
            } else if (fieldName.equals(IF_MODIFIED_SINCE)) {  
                checkIfHeaderIsUnique.apply(fieldName);              
                preconditionsBuilder.setIfModifiedSinceDate(
                        HttpHeaders.parseIfModifiedSince(fieldValue));
            } else if (fieldName.equals(IF_NONE_MATCH)) { 
                preconditionsBuilder.addIfNoneMatchTags(
                        HttpHeaders.parseIfNoneMatch(fieldValue));
            } else if (fieldName.equals(IF_RANGE)) {
                checkIfHeaderIsUnique.apply(fieldName);                
                final Object ifRange = 
                        HttpHeaders.parseIfRange(fieldValue);
                preconditionsBuilder.setIfRange(ifRange);
            } else if (fieldName.equals(IF_UNMODIFIED_SINCE)) {  
                checkIfHeaderIsUnique.apply(fieldName);               
                preconditionsBuilder.setIfUnmodifiedSinceDate(
                        HttpHeaders.parseIfUnmodifiedSince(fieldValue));
            } else if (fieldName.equals(MAX_FORWARDS)) { 
                checkIfHeaderIsUnique.apply(fieldName);              
                requestBuilder.setMaxForwards(
                        HttpHeaders.parseMaxForwards(fieldValue));
            } else if (fieldName.equals(PRAGMA)) {  
                requestBuilder.addPragmaCacheDirectives(
                        HttpHeaders.parsePragma(fieldValue));
            } else if (fieldName.equals(PROXY_AUTHORIZATION)) { 
                checkIfHeaderIsUnique.apply(fieldName);       
                requestBuilder.setProxyAuthorizationCredentials(
                        HttpHeaders.parseProxyAuthorization(fieldValue));
            } else if (fieldName.equals(RANGE)) {
                checkIfHeaderIsUnique.apply(fieldName);               
                preferencesBuilder.setRange(
                        HttpHeaders.parseRange(fieldValue));
            } else if (fieldName.equals(REFERER)) {  
                checkIfHeaderIsUnique.apply(fieldName);         
                requestBuilder.setReferrer(
                        HttpHeaders.parseReferer(fieldValue));
            } else if (fieldName.equals(TE)) { 
                preferencesBuilder.addAcceptedTransferEncodings(
                        HttpHeaders.parseTE(fieldValue));
            } else if (fieldName.equals(TRAILER)) {  
                connectionInfoBuilder.addTrailerHeaders(
                        HttpHeaders.parseTrailer(fieldValue));
            } else if (fieldName.equals(TRANSFER_ENCODING)) { 
                connectionInfoBuilder.addTransferEncodings(
                        HttpHeaders.parseTransferEncoding(fieldValue));
            } else if (fieldName.equals(UPGRADE)) {
                connectionInfoBuilder.addUpgradeProtocols(
                        HttpHeaders.parseUpgrade(fieldValue));
            } else if (fieldName.equals(USER_AGENT)) {  
                checkIfHeaderIsUnique.apply(fieldName);          
                requestBuilder.setUserAgent(
                        HttpHeaders.parseUserAgent(fieldValue));
            } else if (fieldName.equals(VIA)) { 
                connectionInfoBuilder.addVias(
                        HttpHeaders.parseVia(fieldValue));
            } else {
                requestBuilder.addCustomHeader(fieldName, fieldValue);
            }    
        }
        
        final Uri uri;
        if (!requestTarget.isAbsolute()) {
            Preconditions.checkArgument(
                    !host.isEmpty(), "Uri is not absolute, and does not include a host field.");
            uri = Uri.builder()
                    .setScheme(uriScheme)
                    .setAuthority(host)
                    .setPath(requestTarget.path())
                    .setQuery(requestTarget.query())
                    .build();
        } else {
        	Preconditions.checkArgument(host.equals(""), "Host header present when request target is an absolute URI.");
            uri = requestTarget;
        }
        
        return requestBuilder
                .setMethod(method)
                .setUri(uri)
                .setPreferences(preferencesBuilder.build())
                .setConnectionInfo(connectionInfoBuilder.build())
                .setContentInfo(contentInfoBuilder.build())
                .setPreconditions(preconditionsBuilder.build()).build();
    }
    
    /**
     * Parses a Request from the String representation of its components.
     * @param uriScheme the URI scheme of the request. Usually http or https.
     * @param methodName the request method name from the request line.
     * @param requestTarget the request target from the request line.
     * @param headers an Iterable of the header field name/value pairs.
     * @throws NullPointerException if any argument is null. Also if any entry 
     * in {@code headers} is null or contains null key/values.
     */
    public static Request parse(
            final String uriScheme,
            final String method, 
            final String requestTarget, 
            final Iterable<? extends Entry<String, String>> headers) {  
        Preconditions.checkNotNull(uriScheme);
        Preconditions.checkNotNull(method);
        Preconditions.checkNotNull(requestTarget);
        Preconditions.checkNotNull(headers);
    	return parse(uriScheme, Method.forName(method), Uri.parse(requestTarget), 
    			Iterables.transform(headers,
    					new Function<Entry<String, String>, Entry<Header, String>>(){
							@Override
							public Entry<Header, String> apply(
									final Entry<String, String> input) {
								Preconditions.checkNotNull(input);
								return ImmutableMapEntry.create(
										Header.create(input.getKey()), input.getValue());
							}    				
    			}));
    }
    
    Request(){}
    
    /**
     * Returns the client's authorization credentials.
     */
    public abstract Optional<ChallengeMessage> authorizationCredentials();

    
    /**
     * Returns the set of all cache directives provided by the client.
     */
    public abstract Set<CacheDirective> cacheDirectives();
    
    /**
     * Returns the connectionInfo associated with this request.
     */
    public abstract ConnectionInfo connectionInfo();

    /**
     * Returns the connectionInfo associated with this request.
     */
    public abstract ContentInfo contentInfo();

    /**
     * Returns a ListMultimap containing any non-standard headers. 
     */
    public abstract ListMultimap<Header, String> customHeaders();

    /** 
     * Returns the request entity as an object if available.
     */
    public abstract Optional<Object> entity();

    @Override
    public final boolean equals(@Nullable final Object obj) {
        return super.equals(obj);
    }
    
    /**
     * Returns the set of client Expectations.
     */
    public abstract Set<Expectation> expectations();

    /**
     * Returns the from email address of the request.
     */
    public abstract Optional<EmailAddress> from();

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
    
    /**
     * Returns the maximum number of times this request may be forwarded, if specified.
     */
    public abstract Optional<Integer> maxForwards();
    
    /**
     * Returns the http method of this request.
     */
    public abstract Method method();
    
    /**
     * Returns the set of all pragma cache directives provided by the client. 
     */
    public abstract Set<CacheDirective> pragmaCacheDirectives();

    /**
     * Returns the client request preconditions.
     */
    public abstract RequestPreconditions preconditions();

    /**
     * Returns the client's preferences for content negotiation.
     */
    public abstract ClientPreferences preferences();

    /**
     * Returns the client's proxy authorization credentials.
     */
    public abstract Optional<ChallengeMessage> proxyAuthorizationCredentials();

    /**
     * Returns the URI of the referrer of this request.
     */
    public abstract Optional<Uri> referrer();

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(this.method()).append(" ").append(this.uri().path());
        if (!this.uri().query().isEmpty()) {
            builder.append('?').append(this.uri().query());
        }
        builder.append("\r\n");

        appendHeader(builder, HttpHeaders.HOST, this.uri().authority());
        appendHeader(builder, HttpHeaders.AUTHORIZATION, this.authorizationCredentials());
        appendHeader(builder, HttpHeaders.CACHE_CONTROL, this.cacheDirectives());
        appendHeader(builder, HttpHeaders.EXPECT, this.expectations());
        appendHeader(builder, HttpHeaders.FROM, this.from());
        appendHeader(builder, HttpHeaders.MAX_FORWARDS, this.maxForwards());
        appendHeader(builder, HttpHeaders.PRAGMA, this.pragmaCacheDirectives());
        appendHeader(builder, HttpHeaders.PROXY_AUTHORIZATION, this.proxyAuthorizationCredentials());
        appendHeader(builder, HttpHeaders.REFERER, this.referrer());
        appendHeader(builder, HttpHeaders.USER_AGENT, this.userAgent());

        return builder
                .append(this.connectionInfo())
                .append(this.contentInfo())
                .append(this.preconditions())
                .append(this.preferences())
                .toString();
    }

    /**
     * Returns the URI of this request.
     */
    public abstract Uri uri();

    /**
     * Returns the clients user agent string.
     */
    public abstract Optional<UserAgent> userAgent();
}
