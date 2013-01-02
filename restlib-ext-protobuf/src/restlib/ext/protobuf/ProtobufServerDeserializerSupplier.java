package restlib.ext.protobuf;

import restlib.Request;
import restlib.bio.InputStreamDeserializer;
import restlib.data.MediaRange;
import restlib.server.bio.InputStreamDeserializerSupplier;

import com.google.common.base.Preconditions;
import com.google.protobuf.Message;

public final class ProtobufServerDeserializerSupplier {
    public static <T extends Message> InputStreamDeserializerSupplier<T> create(
            final T prototype, final MediaRange mediaRange) {
        Preconditions.checkNotNull(prototype);
        Preconditions.checkNotNull(mediaRange);

        return new InputStreamDeserializerSupplier<T>() {
            @Override
            public InputStreamDeserializer<T> get(final Request request) {
                return ProtobufDeserializer.create(prototype);
            }

            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }
        };
    }

    private ProtobufServerDeserializerSupplier() {
    }
}
