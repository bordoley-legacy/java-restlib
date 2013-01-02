package restlib.ext.jackson;

import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import restlib.ContentInfo;
import restlib.Request;
import restlib.Response;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Charset;
import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.Preference;
import restlib.server.bio.OutputStreamSerializerSupplier;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public final class JsonServerSerializerSupplier {
    public static OutputStreamSerializerSupplier create(
            final ObjectMapper objectMapper, final MediaRange mediaRange) {
        return create(objectMapper, mediaRange, ImmutableSet.<Language> of());
    }

    public static OutputStreamSerializerSupplier create(
            final ObjectMapper objectMapper, 
            final MediaRange mediaRange,
            final Iterable<Language> languages) {
        Preconditions.checkNotNull(objectMapper);
        final ObjectWriter writer = objectMapper.writer();
        return create(writer, mediaRange, languages);
    }

    public static OutputStreamSerializerSupplier create(
            final ObjectWriter writer, final MediaRange mediaRange) {
        return create(writer, mediaRange, ImmutableSet.<Language> of());
    }

    public static OutputStreamSerializerSupplier create(
            final ObjectWriter writer, 
            final MediaRange mediaRange,
            Iterable<Language> languages) {
        Preconditions.checkNotNull(writer);
        Preconditions.checkNotNull(mediaRange);
        final Set<Language> langCopy = ImmutableSet.copyOf(languages);

        return new OutputStreamSerializerSupplier() {
            @Override
            public OutputStreamSerializer get(final Request request,
                    final Response response) {
                final Charset charset = 
                        Preference.<Charset> bestMatch(
                                request.preferences().acceptedCharsets(), 
                                Charset.available()).or(Charset.UTF_8);

                final ContentInfo contentInfo = 
                        ContentInfo.builder()
                            .setMediaRange(mediaRange.withCharset(charset))
                            .addLanguages(langCopy).build();

                return JsonSerializer.create(
                        writer, response.entity().get(), contentInfo);
            }

            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }
        };
    }

    private JsonServerSerializerSupplier() {
    }
}
