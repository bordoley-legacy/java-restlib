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

import java.util.Map;

import restlib.Request;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.InternetDomainName;

public final class ApplicationSuppliers {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T extends Application> Function<Request, T> constant(final T application) {
        Preconditions.checkNotNull(application);
        return (Function) Functions.constant(application);
    }

    public static <T extends Application> Function<Request, T> virtualHosts(
            final Map<InternetDomainName, T> hostMap, final T defaultApplication) {
        Preconditions.checkNotNull(hostMap);
        Preconditions.checkNotNull(defaultApplication);

        final Function<InternetDomainName, T> applications = 
                Functions.forMap(
                        ImmutableMap.copyOf(hostMap), defaultApplication);

        return new Function<Request, T>() {
            @Override
            public T apply(final Request request) {
                Preconditions.checkNotNull(request);
                try {
                    final InternetDomainName domain = 
                            InternetDomainName.from(request.uri().host());
                    return applications.apply(domain);
                } catch (final IllegalArgumentException e) {
                    return defaultApplication;
                }
            }
        };
    }

    private ApplicationSuppliers() {
    }
}
