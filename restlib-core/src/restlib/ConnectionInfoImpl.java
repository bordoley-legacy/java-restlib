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

import java.util.List;
import java.util.Set;

import restlib.data.ConnectionOption;
import restlib.data.Header;
import restlib.data.Protocol;
import restlib.data.TransferCoding;
import restlib.data.Via;

final class ConnectionInfoImpl extends ConnectionInfo {
    private final Set<ConnectionOption> options;
    private final Set<Header> trailerHeaders;
    private final List<TransferCoding> transferEncodings;
    private final Set<Protocol> upgrade;
    private final List<Via> via;

    ConnectionInfoImpl(final ConnectionInfoBuilder builder) {
        this.options = builder.options.build();
        this.trailerHeaders = builder.trailerHeaders.build();
        this.transferEncodings = builder.transferEncodings.build();
        this.upgrade = builder.upgradeProtocols.build();
        this.via = builder.via.build();
    }

    @Override
    public Set<ConnectionOption> options() {
        return this.options;
    }

    @Override
    public Set<Header> trailerHeaders() {
        return this.trailerHeaders;
    }

    @Override
    public List<TransferCoding> transferEncodings() {
        return this.transferEncodings;
    }

    @Override
    public Set<Protocol> upgradeProtocols() {
        return this.upgrade;
    }

    @Override
    public List<Via> via() {
        return this.via;
    }
}
