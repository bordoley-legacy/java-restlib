package restlib.ext.protobuf;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.bio.OutputStreamSerializer;
import restlib.data.MediaRange;
import restlib.server.bio.OutputStreamSerializerSupplier;

import com.google.protobuf.Message;

public final class ProtobufServerSerializerSupplier {
    public static OutputStreamSerializerSupplier create(
            final Message prototype, final MediaRange mediaRange) {
        final ContentInfo contentInfo = ContentInfo.builder()
                .setMediaRange(mediaRange).build();

        return new OutputStreamSerializerSupplier() {
            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }

            @Override
            public OutputStreamSerializer get(final Request request,
                    final Response response) {
                return ProtobufSerializer.create(
                        prototype, response.entity().get(), contentInfo);
            }
        };
    }

    private ProtobufServerSerializerSupplier() {
    }
}
