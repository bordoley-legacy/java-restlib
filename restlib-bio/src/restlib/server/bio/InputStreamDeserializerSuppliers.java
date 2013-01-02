package restlib.server.bio;

import restlib.Request;
import restlib.bio.BioMultiPartInputDeserializer;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.InputStreamDeserializers;
import restlib.data.Charset;
import restlib.data.MediaRange;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public final class InputStreamDeserializerSuppliers {
    public static <T> InputStreamDeserializerSupplier<T> multiPartInputDeserializerSupplier(
            final BioMultiPartInputDeserializer<T> multiPartInputDeserializer,
            final MediaRange mediaRange) {
        return new InputStreamDeserializerSupplier<T>() {
            @Override
            public InputStreamDeserializer<T> get(final Request request) {
                Preconditions.checkArgument(request.contentInfo().mediaRange().isPresent());
                final String boundary = 
                        Iterables.getFirst(request
                            .contentInfo()
                            .mediaRange()
                            .get()
                            .parameters()
                            .get("boundary"), "");
                Preconditions.checkArgument(
                        !boundary.isEmpty(),
                        "Request does not include a boundary in the Content-Type header.");
                return InputStreamDeserializers.multiPartDeserializer(boundary,
                        multiPartInputDeserializer);
            }

            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }
        };
    }

    public static InputStreamDeserializerSupplier<String> stringDeserializerSupplier(
            final MediaRange mediaRange) {
        Preconditions.checkNotNull(mediaRange);

        return new InputStreamDeserializerSupplier<String>() {
            @Override
            public InputStreamDeserializer<String> get(final Request request) {
                Preconditions.checkNotNull(request);
                Preconditions.checkArgument(request.contentInfo().mediaRange().isPresent());
                
                final Charset charset = 
                        request.contentInfo()
                            .mediaRange()
                            .get()
                            .charset().or(Charset.UTF_8);

                return InputStreamDeserializers.stringDeserializer(charset);
            }

            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }
        };
    }

    private InputStreamDeserializerSuppliers() {}
}
