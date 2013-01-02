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

import restlib.data.Header;
import restlib.impl.Optionals;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;


final class MessageHelpers {
    private static final Joiner joiner = Joiner.on(", ");
    
    static void appendHeader(
            final StringBuilder builder, final Header key, final Iterable<?> value) {
        if (Iterables.isEmpty(value)) {
            return;
        }
        
        builder.append(key).append(": ");
        builder.append(joiner.join(value)).append("\r\n");
    }
    
    static void appendHeader(
            final StringBuilder builder, final Header key, final Optional<?> value) {
        appendHeader(builder, key, Optionals.toStringOrEmpty(value));
    }
    
    static void appendHeader(
            final StringBuilder builder, final Header key, final Object value) {
        final String fieldValue = value.toString();
        if (fieldValue.isEmpty()) {
            return;
        }
        
        builder.append(key).append(": ").append(fieldValue).append("\r\n");
    }

    private MessageHelpers(){}
}
