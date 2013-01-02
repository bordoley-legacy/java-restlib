package restlib.server.bio;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.bio.OutputStreamSerializer;
import restlib.bio.OutputStreamSerializers;
import restlib.data.Charset;
import restlib.data.MediaRange;
import restlib.data.MediaRanges;
import restlib.data.Preference;

import com.google.common.base.Preconditions;

public final class OutputStreamSerializerSuppliers {
    public static final OutputStreamSerializerSupplier STRING_AS_PLAIN_TEXT_SERIALIZER_SUPPLIER = stringSerializerSupplier(MediaRanges.TEXT_PLAIN);

    public static OutputStreamSerializerSupplier stringSerializerSupplier(
            final MediaRange mediaRange) {
        return new OutputStreamSerializerSupplier() {
            @Override
            public OutputStreamSerializer get(final Request request,
                    final Response response) {
                Preconditions.checkNotNull(request);
                Preconditions.checkNotNull(response);
                
                final Charset charset = 
                        Preference.<Charset> bestMatch(
                                request.preferences().acceptedCharsets(), 
                                Charset.available()).or(Charset.UTF_8);
                final ContentInfo contentInfo = 
                        ContentInfo.builder()
                            .setMediaRange(mediaRange.withCharset(charset)).build();
                return OutputStreamSerializers.stringSerializer(
                        response.entity().get().toString(), contentInfo);
            }

            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }
        };
    }

    private OutputStreamSerializerSuppliers() {}
}
