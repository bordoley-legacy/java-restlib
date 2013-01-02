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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import restlib.data.ConnectionOption;
import restlib.data.HttpHeaders;
import restlib.data.Protocol;
import restlib.data.TransferCoding;
import restlib.data.Via;
import restlib.test.WrapperTester;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;

/**
 * Tests for  ConnectionInfo,  ConnectionInfoBuilder,  ConnectionInfoImpl and ConnectionInfoWrapper.
 */
public final class ConnectionInfoTest {
    @Test 
    public void testBuilder$build() {
        final ConnectionInfo connectionInfo =
                ConnectionInfo.builder()
                    .addConnectionOption(ConnectionOption.CLOSE)
                    .addConnectionOptions(ImmutableList.of(ConnectionOption.KEEP_ALIVE))
                    .addTrailerHeader(HttpHeaders.ACCEPT)
                    .addTrailerHeaders(ImmutableList.of(HttpHeaders.ACCEPT_CHARSET))
                    .addTransferEncoding(TransferCoding.COMPRESS)
                    .addTransferEncodings(ImmutableList.of(TransferCoding.DEFLATE))
                    .addUpgradeProtocol(Protocol.HTTP_0_9)
                    .addUpgradeProtocols(ImmutableList.of(Protocol.HTTP_1_0))
                    .addVia(Via.create(Protocol.HTTP_0_9, "www.example.com"))
                    .addVias(ImmutableList.of(Via.create(Protocol.HTTP_1_1, "www.example.org")))
                    .build();
        assertEquals(
                ImmutableSet.of(ConnectionOption.CLOSE, ConnectionOption.KEEP_ALIVE),
                connectionInfo.options());
        assertEquals(
                ImmutableSet.of(HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_CHARSET),
                connectionInfo.trailerHeaders());
        assertEquals(
                ImmutableList.of(TransferCoding.COMPRESS, TransferCoding.DEFLATE),
                connectionInfo.transferEncodings());
        assertEquals(
                ImmutableSet.of(Protocol.HTTP_0_9, Protocol.HTTP_1_0),
                connectionInfo.upgradeProtocols());
        assertEquals(
                ImmutableList.of(
                        Via.create(Protocol.HTTP_0_9, "www.example.com"), 
                        Via.create(Protocol.HTTP_1_1, "www.example.org")),
                connectionInfo.via());            
    }
    
    @Test
    public void testEquals() {
        final ConnectionInfoBuilder builder = ConnectionInfo.builder();
        new EqualsTester()
            .addEqualityGroup(
                    builder.build(), 
                    builder.build(),
                    ConnectionInfo.NONE)
            .addEqualityGroup(
                    builder.addConnectionOption(ConnectionOption.CLOSE).build(), 
                    builder.build())
            .addEqualityGroup(
                    builder.addConnectionOptions(ImmutableList.of(ConnectionOption.KEEP_ALIVE)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addTrailerHeader(HttpHeaders.ACCEPT).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addTrailerHeaders(ImmutableList.of(HttpHeaders.ACCEPT_CHARSET)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addTransferEncoding(TransferCoding.COMPRESS).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addTransferEncodings(ImmutableList.of(TransferCoding.DEFLATE)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addUpgradeProtocol(Protocol.HTTP_0_9).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addUpgradeProtocols(ImmutableList.of(Protocol.HTTP_1_0)).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addVia(Via.create(Protocol.HTTP_0_9, "www.example.com")).build(),
                    builder.build())
            .addEqualityGroup(
                    builder.addVias(ImmutableList.of(Via.create(Protocol.HTTP_1_1, "www.example.org"))).build())
            .testEquals();       
    }
    
    @Test
    public void testNulls() {
        new NullPointerTester()
                .testAllPublicInstanceMethods(ConnectionInfo.builder());
        new NullPointerTester()
            .testAllPublicInstanceMethods(ConnectionInfo.NONE);
    }
    
    @Test
    public void testWrapper() {
        final ConnectionInfoBuilder builder = ConnectionInfo.builder();
        WrapperTester.create(
                ConnectionInfo.class,
                new Function<ConnectionInfo,ConnectionInfo>() {
                    @Override
                    public ConnectionInfo apply(final ConnectionInfo connInfo) {
                        return new ConnectionInfoWrapper(connInfo);
                    }                   
                })
            .useDefaultInstances()
            .includingEquals()
            .executeTests(
                    builder.build(),
                    builder.addConnectionOption(ConnectionOption.CLOSE).build(),
                    builder.addTrailerHeader(HttpHeaders.ACCEPT).build(),
                    builder.addTransferEncoding(TransferCoding.GZIP).build(),
                    builder.addUpgradeProtocol(Protocol.HTTP_1_1).build(),
                    builder.addVia(Via.create(Protocol.HTTP_1_1, "www.example.org")).build());
    }
}
