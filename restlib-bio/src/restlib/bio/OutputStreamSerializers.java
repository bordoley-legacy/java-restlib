package restlib.bio;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import restlib.ContentInfo;
import restlib.ContentInfoWrapper;
import restlib.data.Charset;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.CountingOutputStream;

public final class OutputStreamSerializers {
    private OutputStreamSerializers() {}

    public static OutputStreamSerializer stringSerializer(
            final String entity,
            final ContentInfo contentInfo) {
        Preconditions.checkNotNull(contentInfo);
        Preconditions.checkNotNull(entity);
        
        final Charset charset = 
                contentInfo.mediaRange().isPresent() ?
                        contentInfo.mediaRange().get().charset().or(Charset.UTF_8) :
                            Charset.UTF_8;
        
        return new OutputStreamSerializer() {
            @Override
            public ContentInfo contentInfo() {     
                return new ContentInfoWrapper(contentInfo) {
                    @Override
                    public Optional<Long> length() {
                        return Optional.of(
                                    (long) entity.getBytes(charset.toNioCharset()).length);
                    }
                };
            }
    
            @Override
            public long write(final OutputStream os) throws IOException{
                Preconditions.checkNotNull(os);
    
                final CountingOutputStream cos = new CountingOutputStream(os);
                final Writer writer = 
                        new BufferedWriter(
                                new OutputStreamWriter(cos, charset.toNioCharset()));
                writer.write(entity);
                writer.flush();
                writer.close();
    
                return cos.getCount();              
            }           
        };
    }
}
