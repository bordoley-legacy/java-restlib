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

import javax.annotation.concurrent.NotThreadSafe;

import restlib.data.ConnectionOption;
import restlib.data.Header;
import restlib.data.Protocol;
import restlib.data.TransferCoding;
import restlib.data.Via;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * A builder for generating instances of {@code ConnectionInfo}. 
 * ConnectionInfoBuilder instances can be reused; it is safe to call build() 
 * multiple times to build multiple {@code ConnectionInfo} instances.
 */
@NotThreadSafe
public final class ConnectionInfoBuilder {
    final ImmutableSet.Builder<ConnectionOption> options = ImmutableSet.builder();
    final ImmutableSet.Builder<Header> trailerHeaders = ImmutableSet.builder();
    final ImmutableList.Builder<TransferCoding> transferEncodings = ImmutableList.builder();
    final ImmutableSet.Builder<Protocol> upgradeProtocols = ImmutableSet.builder();
    final ImmutableList.Builder<Via> via = ImmutableList.builder();

    ConnectionInfoBuilder() {}

    /**
     * Adds the ConnectionOption to this builder's ConnectionOption set.
     * @param connectionOption the ConnectionOption to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code connectionOption} is null.
     */
    public ConnectionInfoBuilder addConnectionOption(final ConnectionOption connectionOption) {
        this.options.add(connectionOption);
        return this;
    }

    /**
     * Adds each ConnectionOption to this builder's ConnectionOption set.
     * @param connectionOptions the ConnectionOptions to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code connectionOptions} is null or contains a null element.
     */
    public ConnectionInfoBuilder addConnectionOptions(final Iterable<ConnectionOption> connectionOptions) {
        this.options.addAll(connectionOptions);
        return this;
    }

    /**
     * Adds the Header to this builder's Trailer header set.
     * 
     * <p>Note: although not enforced, the HTTP specification does not allow the following 
     * headers fields in the Trailer header set:
     * <ul>
     * <li>Transfer-Encoding,
     * <li>Content-Length,
     * <li>Trailer.
     * </ul>
     * @param header the Header to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code header} is null.
     */
    public ConnectionInfoBuilder addTrailerHeader(final Header header) {
        this.trailerHeaders.add(header);
        return this;
    }

    /**
     * Adds each header to this builder's Trailer header set.
     * 
     * <p>Note: although not enforced, the HTTP specification does not allow the following 
     * headers fields in the Trailer header set:
     * <ul>
     * <li>Transfer-Encoding,
     * <li>Content-Length,
     * <li>Trailer.
     * </ul>
     * @param headers the Headers to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code headers} is null or contains a null element.
     */
    public ConnectionInfoBuilder addTrailerHeaders(final Iterable<Header> headers) {
        this.trailerHeaders.addAll(headers);
        return this;
    }

    /**
     * Adds the TransferEncoding to this builder's TransferEncoding list.
     * @param transferEncoding the TransferEncoding to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code transferEncoding} is null.
     */
    public ConnectionInfoBuilder addTransferEncoding(final TransferCoding transferEncoding) {
        this.transferEncodings.add(transferEncoding);
        return this;
    }

    /**
     * Adds each TransferEncoding to this builder's TransferEncoding list.
     * @param transferEncodings the TransferEncodings to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code transferEncodings} is null or contains a null element.
     */
    public ConnectionInfoBuilder addTransferEncodings(final Iterable<TransferCoding> transferEncodings) {
        this.transferEncodings.addAll(transferEncodings);
        return this;
    }

    /**
     * Adds the Protocol to this builder's Upgrade protocol set.
     * @param upgradeProtocol the Protocol to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code upgradeProtocol} is null.
     */
    public ConnectionInfoBuilder addUpgradeProtocol(final Protocol upgradeProtocol) {
        this.upgradeProtocols.add(upgradeProtocol);
        return this;
    }

    /**
     * Adds each Protocol to this builder's Upgrade protocol set.
     * @param upgradeProtocols the Protocol to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code upgradeProtocols} is null or contains a null element.
     */
    public ConnectionInfoBuilder addUpgradeProtocols(final Iterable<Protocol> upgradeProtocols) {
        this.upgradeProtocols.addAll(upgradeProtocols);
        return this;
    }

    /**
     * Adds the Via to this builder's Via list.
     * @param via the Via to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code via} is null.
     */
    public ConnectionInfoBuilder addVia(final Via via) {
        this.via.add(via);
        return this;
    }
    
    /**
     * Adds each Via to this builder's Via list.
     * @param vias the Vias to add.
     * @return this {@code ConnectionOptionBuilder} instance.
     * @throws NullPointerException if {@code vias} is null or contains a null element.
     */
    public ConnectionInfoBuilder addVias(final Iterable<Via> vias) {
        this.via.addAll(vias);
        return this;
    }
    
    /**
     * Returns a newly-created {@code ConnectionInfo} instance based 
     * on the contents of the ConnectionInfoBuilder.
     */
    public ConnectionInfo build() {
        return new ConnectionInfoImpl(this);
    }
}