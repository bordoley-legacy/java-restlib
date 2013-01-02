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

package restlib.ext.jackson;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;

import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.net.IRI;
import restlib.net.Uri;
import restlib.serializable.atom.AtomDate;

public final class RestlibModuleFactory {
    private static ObjectMapper getDefaultObjectMapperInstance() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        objectMapper.configure(
                SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(
                DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        objectMapper.configure(
                DeserializationConfig.Feature.USE_ANNOTATIONS,
                true);
        return objectMapper;
    }

    public static ObjectMapper objectMapper() {
        final ObjectMapper mapper = getDefaultObjectMapperInstance();
        mapper.registerModule(restlibCoreModule());
        mapper.registerModule(restlibSerializableModule());
        return mapper;
    }

    public static Module restlibCoreModule() {
        return new SimpleModule("restlib.core", new Version(1, 0, 0, null))
                .addSerializer(Uri.class, ToStringSerializer.instance)
                .addSerializer(IRI.class, ToStringSerializer.instance)
                .addSerializer(MediaRange.class, ToStringSerializer.instance)
                .addSerializer(Language.class, ToStringSerializer.instance)
                .addDeserializer(IRI.class, Deserializers.IRI_DESERIALIZER)
                .addDeserializer(Uri.class, Deserializers.URI_DESERIALIZER)
                .addDeserializer(MediaRange.class,
                        Deserializers.MEDIA_RANGE_DESERIALIZER);
    }

    public static Module restlibSerializableModule() {
        return new SimpleModule("restlib.serializable", new Version(1, 0, 0, null))
                .addSerializer(AtomDate.class, ToStringSerializer.instance)
                .addDeserializer(AtomDate.class, Deserializers.ATOM_DATE_DESERIALIZER);
    }

    private RestlibModuleFactory() {
    }
}
