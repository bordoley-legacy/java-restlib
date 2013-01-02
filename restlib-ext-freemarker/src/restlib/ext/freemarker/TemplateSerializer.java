package restlib.ext.freemarker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import restlib.ContentInfo;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Charset;

import com.google.common.base.Preconditions;
import com.google.common.io.CountingOutputStream;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public final class TemplateSerializer {
    public static OutputStreamSerializer create(
            final Template template, 
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

                final CountingOutputStream cos = new CountingOutputStream(os);
                final Charset charset = contentInfo.mediaRange().isPresent() ?
                                    contentInfo.mediaRange().get().charset().or(Charset.UTF_8) :
                                        Charset.UTF_8;
                
                try {
                    template.process(entity, 
                            new BufferedWriter(
                                    new OutputStreamWriter(cos, charset.toNioCharset())));
                } catch (final TemplateException e) {
                    throw new IOException(e);
                }
                return cos.getCount();
            }           
        };
    }
    
    private TemplateSerializer(){}
}
