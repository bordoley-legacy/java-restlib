package restlib.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import restlib.bio.multipart.BioMultiPartInput;
import restlib.data.Charset;

import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;

public final class InputStreamDeserializers {
    private InputStreamDeserializers(){}

    public static <T> InputStreamDeserializer<T> multiPartDeserializer(
            final String boundary, 
            final BioMultiPartInputDeserializer<T> deserializer) {
        Preconditions.checkNotNull(boundary);
        Preconditions.checkNotNull(deserializer);
        
        return new InputStreamDeserializer<T>() {
            @Override
            public T read(final InputStream is) throws IOException {
                Preconditions.checkNotNull(is);
                final BioMultiPartInput multiPartInput = 
                        BioMultiPartInput.wrap(is, boundary.getBytes());
                return deserializer.read(multiPartInput);           
            }       
        };
    }

    public static InputStreamDeserializer<String> stringDeserializer(final Charset charset) {
        Preconditions.checkNotNull(charset);
        return new InputStreamDeserializer<String>() {
            @Override
            public String read(final InputStream is) throws IOException {
                Preconditions.checkNotNull(is);
                return CharStreams.toString(new InputStreamReader(is, charset.toNioCharset()));
            }         
        };      
    }
}
