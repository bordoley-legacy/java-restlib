package restlib.ext.freemarker;

import java.util.Set;

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

import freemarker.template.Template;

public final class TemplateServerSerializerSupplier {
    public static OutputStreamSerializerSupplier create(
            final Template template,
            final MediaRange mediaRange) {
        return create(template, mediaRange, ImmutableSet.<Language> of());
    }
    
    public static OutputStreamSerializerSupplier create(
            final Template template,
            final MediaRange mediaRange,
            Iterable<Language> languages) {
        Preconditions.checkNotNull(template);
        Preconditions.checkNotNull(mediaRange);
        final Set<Language> langCopy = ImmutableSet.copyOf(languages);
        
        return new OutputStreamSerializerSupplier () {
            @Override
            public MediaRange mediaRange() {
                return mediaRange;
            }

            @Override
            public OutputStreamSerializer get(
                    final Request request, final Response response) {
                Preconditions.checkNotNull(request);
                Preconditions.checkNotNull(response);
                final Charset charset = 
                        Preference.<Charset> bestMatch(
                                request.preferences().acceptedCharsets(), 
                                Charset.available()).or(Charset.UTF_8);
                        
                final ContentInfo contentInfo = 
                        ContentInfo.builder()
                        .setMediaRange(
                                mediaRange.withCharset(charset))
                        .addLanguages(langCopy).build();
                
                return TemplateSerializer.create(template, response.entity().get(), contentInfo); 
            }         
        };
    }
    
    private TemplateServerSerializerSupplier(){}
}
