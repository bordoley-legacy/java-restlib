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

import javax.annotation.concurrent.Immutable;

import restlib.data.ConnectionOption;
import restlib.data.Header;
import restlib.data.Protocol;
import restlib.data.TransferCoding;
import restlib.data.Via;

import com.google.common.base.Preconditions;

/**
 * An implementation of ConnectionInfo which forwards all its method calls to another instance of ConnectionInfo. 
 * Subclasses should override one or more methods to modify the behavior of the backing ConnectionInfo 
 * as desired per the decorator pattern.
 */
@Immutable
public class ConnectionInfoWrapper extends ConnectionInfo {
    private final ConnectionInfo delegate;
    
    /**
     * Constructs an instance of ConnectionInfoWrapper that 
     * forwards all method calls to {@code delegate}.
     * @param delegate a non-null instance of ConnectionInfo.
     * @throws NullPointerException if {@code delegate} is null.
     */
    protected ConnectionInfoWrapper(final ConnectionInfo delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public Set<ConnectionOption> options() {
        return this.delegate.options();
    }

    @Override
    public Set<Header> trailerHeaders() {
        return this.delegate.trailerHeaders();
    }

    @Override
    public List<TransferCoding> transferEncodings() {
        return this.delegate.transferEncodings();
    }

    @Override
    public Set<Protocol> upgradeProtocols() {
        return this.delegate.upgradeProtocols();
    }

    @Override
    public List<Via> via() {
        return this.delegate.via();
    }
}
