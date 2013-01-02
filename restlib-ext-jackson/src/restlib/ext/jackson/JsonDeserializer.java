package restlib.ext.jackson;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectReader;

import restlib.bio.InputStreamDeserializer;

import com.google.common.base.Preconditions;

public final class JsonDeserializer {
    public static <T> InputStreamDeserializer<T> create(
            final ObjectReader reader, final Class<T> clss) {
        return new InputStreamDeserializer<T>() {
            @Override
            public T read(final InputStream is) throws IOException {
                Preconditions.checkNotNull(is);
                try {
                    return clss.cast(reader.readValue(is));
                } catch (final JsonParseException e) {
                    throw new IOException(e);
                } catch (final JsonMappingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private JsonDeserializer() {
    }
}
