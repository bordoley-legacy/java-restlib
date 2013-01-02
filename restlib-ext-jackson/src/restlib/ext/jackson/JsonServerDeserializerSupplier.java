package restlib.ext.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;

import restlib.Request;
import restlib.bio.InputStreamDeserializer;
import restlib.data.MediaRange;
import restlib.server.bio.InputStreamDeserializerSupplier;

import com.google.common.base.Preconditions;

public final class JsonServerDeserializerSupplier {
    public static <T> InputStreamDeserializerSupplier<T> create(
            final Class<T> clss, 
            final ObjectMapper objectMapper,
            final MediaRange mediaRange) {
        Preconditions.checkNotNull(objectMapper);
        final ObjectReader reader = objectMapper.reader(clss);
        return create(clss, reader, mediaRange);
    }

    public static <T> InputStreamDeserializerSupplier<T> create(
            final Class<T> clss, 
            final ObjectReader reader,
            final MediaRange mediaRange) {
        Preconditions.checkNotNull(clss);
        Preconditions.checkNotNull(reader);
        Preconditions.checkNotNull(mediaRange);

        return new InputStreamDeserializerSupplier<T>() {
            @Override
            public InputStreamDeserializer<T> get(final Request request) {
                Preconditions.checkNotNull(request);
                return JsonDeserializer.create(reader, clss);
            }

            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }
        };
    }

    private JsonServerDeserializerSupplier() {
    }
}
