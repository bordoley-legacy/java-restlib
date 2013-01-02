package restlib.ext.protobuf;

import java.io.IOException;
import java.io.InputStream;

import restlib.bio.InputStreamDeserializer;

import com.google.common.base.Preconditions;
import com.google.protobuf.Message;

public final class ProtobufDeserializer {
    public static <T extends Message> InputStreamDeserializer<T> create(final T prototype) {
        Preconditions.checkNotNull(prototype);

        return new InputStreamDeserializer<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T read(final InputStream is) throws IOException {
                Preconditions.checkNotNull(is);
                return (T) prototype.newBuilderForType().mergeFrom(is).build();
            }
        };
    }

    private ProtobufDeserializer() {
    }
}
