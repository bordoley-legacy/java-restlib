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

import java.util.Map.Entry;

import restlib.ContentInfo;
import restlib.Response;
import restlib.data.Header;
import restlib.data.HttpHeaders;
import restlib.data.Protocol;
import restlib.data.Status;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public abstract class ConnectorResponse {    
    private static final Joiner joiner = Joiner.on(", ");
    
    public final ConnectorResponse populate(final Response response) {
        Preconditions.checkNotNull(response);
        
        final ContentInfo contentInfo = response.contentInfo();
        
        if (!response.acceptedRangeUnits().isEmpty()) {
            this.addHeader(HttpHeaders.ACCEPT_RANGES, joiner.join(response.acceptedRangeUnits()));
        }

        if (!response.allowedMethods().isEmpty()) {
            this.addHeader(HttpHeaders.ALLOW, joiner.join(response.allowedMethods()));
        }

        if (!response.authenticationChallenges().isEmpty()) {
            this.addHeader(HttpHeaders.WWW_AUTHENTICATE,
                    joiner.join(response.authenticationChallenges()));
        }

        if (response.entityTag().isPresent()) {
            this.addHeader(HttpHeaders.ENTITY_TAG, response.entityTag().get());
        }

        if (response.expires().isPresent()) {
            this.addHeader(HttpHeaders.EXPIRES, response.expires().get());
        }

        if (response.lastModified().isPresent()) {
            this.addHeader(HttpHeaders.LAST_MODIFIED, response.lastModified().get());
        }

        if (!response.location().isPresent()) {
            this.addHeader(HttpHeaders.LOCATION, response.location().get());
        }

        if (response.retryAfterDate().isPresent()) {
            this.addHeader(HttpHeaders.RETRY_AFTER, response.retryAfterDate().get());
        }

        if (response.server().isPresent()) {
            this.addHeader(HttpHeaders.SERVER, response.server().get());
        }

        this.setStatus(response.status());
        this.setProtocolVersion(Protocol.HTTP_1_1);

        if (!response.vary().isEmpty()) {
            this.addHeader(HttpHeaders.VARY,
                    joiner.join(response.vary()));
        }

        // Helpers Headers
        if (!response.cacheDirectives().isEmpty()) {
            this.addHeader(HttpHeaders.CACHE_CONTROL, 
                    joiner.join(response.cacheDirectives()));
        }

        if (response.date().isPresent()) {
            this.addHeader(HttpHeaders.DATE, response.date().get());
        }
        
        // Content Headers
        if (contentInfo.mediaRange().isPresent()) {
            this.addHeader(HttpHeaders.CONTENT_TYPE, contentInfo.mediaRange().get());
        }

        if (contentInfo.length().isPresent()) {
            this.addHeader(HttpHeaders.CONTENT_LENGTH, contentInfo.length().get());
        }

        if (!contentInfo.encodings().isEmpty()) {
            this.addHeader(HttpHeaders.CONTENT_ENCODING, 
                    joiner.join(contentInfo.encodings()));
        }

        if (!contentInfo.languages().isEmpty()) {
            this.addHeader(HttpHeaders.CONTENT_LANGUAGE, 
                    joiner.join(contentInfo.languages()));
        }

        if (contentInfo.location().isPresent()) {
            this.addHeader(HttpHeaders.CONTENT_LOCATION, 
                    contentInfo.location().get());
        }

        if (contentInfo.range().isPresent()) {
            this.addHeader(HttpHeaders.CONTENT_RANGE, contentInfo.range().get());
        }
        
        if(response.age().isPresent()) {
            this.addHeader(HttpHeaders.AGE, response.age().get());
        }
        
        if (!response.proxyAuthenticationChallenge().isEmpty()) {
            this.addHeader(
                    HttpHeaders.PROXY_AUTHENTICATE, 
                    joiner.join(
                            response.proxyAuthenticationChallenge()));
        }
        
        if(!response.connectionInfo().options().isEmpty()) {
            this.addHeader(
                    HttpHeaders.CONNECTION, 
                    joiner.join(
                            response.connectionInfo().options()));
        }
        
        if(!response.connectionInfo().trailerHeaders().isEmpty()) {
            this.addHeader(
                    HttpHeaders.TRAILER, 
                    joiner.join(
                            response.connectionInfo().trailerHeaders()));
        }
        
        if(!response.connectionInfo().transferEncodings().isEmpty()) {
            this.addHeader(
                    HttpHeaders.TRANSFER_ENCODING,
                    joiner.join(
                            response.connectionInfo().transferEncodings()));
        }
        
        if(!response.connectionInfo().upgradeProtocols().isEmpty()) {
            this.addHeader(
                    HttpHeaders.UPGRADE,
                    joiner.join(
                            response.connectionInfo().upgradeProtocols()));
        }
        
        if (!response.connectionInfo().via().isEmpty()) {
            this.addHeader(
                    HttpHeaders.VIA, 
                    joiner.join(response.connectionInfo().via()));
        }
        
        if (!response.warnings().isEmpty()) {
            this.addHeader(
                   HttpHeaders.WARNING, 
                   joiner.join(response.warnings()));
        }
        
        for (final Entry<Header, String> field : response.customHeaders().entries()) {
            this.addHeader(field.getKey(), field.getValue());
        }
        
        return this;
    }
    
    protected abstract ConnectorResponse addHeader(Header header, Object value);
    protected abstract ConnectorResponse setProtocolVersion(Protocol version);
    protected abstract ConnectorResponse setStatus(Status status);
}
