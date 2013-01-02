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

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.data.ConnectionOption;
import restlib.data.Header;
import restlib.data.HttpHeaders;
import restlib.data.Protocol;
import restlib.data.TransferCoding;
import restlib.data.Via;

import com.google.common.base.Objects;

/**
 * Connection features of an HTTP request or response.
 * Implementations must be immutable or effectively immutable.
 */
@Immutable
public abstract class ConnectionInfo {
    /**
     * ConnectionInfo null object instance.
     */
    static final ConnectionInfo NONE = ConnectionInfo.builder().build();
    
    /**
     * Returns a new ConnectionInfoBuilder instance.
     */
    public static ConnectionInfoBuilder builder() {
        return new ConnectionInfoBuilder();
    }
    
    ConnectionInfo(){}
    
    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ConnectionInfo) {
            final ConnectionInfo that = (ConnectionInfo) obj;
            return this.options().equals(that.options()) &&
                    this.trailerHeaders().equals(that.trailerHeaders()) && this.transferEncodings().equals(that.transferEncodings()) &&
                    this.upgradeProtocols().equals(that.upgradeProtocols()) && this.via().equals(that.via());
        } 
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(this.options(), this.trailerHeaders(), this.transferEncodings(), this.upgradeProtocols(), this.via());
    }

    /**
     * Returns a sender to specified set of options that are desired only for that particular connection.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<ConnectionOption> options();

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        appendHeader(builder, HttpHeaders.CONNECTION, this.options());
        appendHeader(builder, HttpHeaders.TRAILER, this.trailerHeaders());
        appendHeader(builder, HttpHeaders.TRANSFER_ENCODING, this.transferEncodings());
        appendHeader(builder, HttpHeaders.UPGRADE, this.upgradeProtocols());
        appendHeader(builder, HttpHeaders.VIA, this.via());
        
        return builder.toString();
    }

    /**
     * Returns the set of header fields present in the trailer of a message 
     * encoded with the chunked TransferEncoding.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Header> trailerHeaders();

    /**
     * Returns the list of transfer codings applied to the payload 
     * body in the order they were applied.
     * The returned {@code List} is unmodifiable.
     */
    public abstract List<TransferCoding> transferEncodings();

    /**
     * Returns the client specifies communication protocols it would like to use,
     * if the server chooses to switch protocols. Servers can use it to indicate
     * what protocols they are willing to switch to.
     * The returned {@code Set} is unmodifiable.
     */
    public abstract Set<Protocol> upgradeProtocols();

    /**
     * Returns the list of intermediate protocols and recipients between  
     * the user agent and the server on requests, and between the origin server
     * and the client on responses.
     * The returned {@code List} is unmodifiable.
     */
    public abstract List<Via> via();
}
