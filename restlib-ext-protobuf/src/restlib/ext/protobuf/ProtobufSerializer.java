package restlib.ext.protobuf;

import java.io.IOException;
import java.io.OutputStream;

import restlib.ContentInfo;
import restlib.ContentInfoWrapper;
import restlib.bio.OutputStreamSerializer;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.protobuf.Message;

public final class ProtobufSerializer {

    public static OutputStreamSerializer create(
            final Message prototype,
            final Object entity, 
            final ContentInfo contentInfo) {
        return new OutputStreamSerializer() {
            @Override
            public ContentInfo contentInfo() {
                return new ContentInfoWrapper(contentInfo) {
                    @Override
                    public Optional<Long> length() {
                        return Optional.of((long) prototype.getClass().cast(entity).getSerializedSize());
                    }
                };
            }

            @Override
            public long write(final OutputStream os) throws IOException {
                Preconditions.checkNotNull(os);
                Preconditions.checkArgument(
                        prototype.getClass().isInstance(entity));

                prototype.getClass().cast(entity).writeTo(os);
                return prototype.getClass().cast(entity).getSerializedSize();
            }
        };
    }

    private ProtobufSerializer() {
    }
}
