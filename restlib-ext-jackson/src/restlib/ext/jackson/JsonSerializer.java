package restlib.ext.jackson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectWriter;

import restlib.ContentInfo;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Charset;

import com.google.common.base.Preconditions;
import com.google.common.io.CountingOutputStream;

public final class JsonSerializer {
    public static OutputStreamSerializer create(
            final ObjectWriter writer,
            final Object entity, 
            final ContentInfo contentInfo) {
        return new OutputStreamSerializer() {
            @Override
            public ContentInfo contentInfo() {
                return contentInfo;
            }

            @Override
            public long write(final OutputStream os) throws IOException {
                Preconditions.checkNotNull(os);

                final Charset charset = contentInfo.mediaRange().isPresent() ?
                        contentInfo.mediaRange().get().charset().or(Charset.UTF_8) :
                            Charset.UTF_8;
                
                final CountingOutputStream cos = new CountingOutputStream(os);
                final Writer bWriter = new BufferedWriter(
                        new OutputStreamWriter(cos, charset.toNioCharset()));

                try {
                    writer.writeValue(bWriter, entity);
                    return cos.getCount();
                } catch (final JsonGenerationException e) {
                    throw new IOException(e);
                } catch (final JsonMappingException e) {
                    throw new IOException(e);
                }
            }
        };
    }

    private JsonSerializer() {
    }
}
