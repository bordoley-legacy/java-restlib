package restlib.example.blog.bio.serialization;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import restlib.data.Language;
import restlib.data.MediaRange;
import restlib.data.MediaRanges;
import restlib.example.blog.bio.serialization.freemarker.Templates;
import restlib.ext.freemarker.TemplateServerSerializerSupplier;
import restlib.ext.jackson.JsonServerSerializerSupplier;
import restlib.ext.jackson.RestlibModuleFactory;
import restlib.server.bio.OutputStreamSerializerSupplier;

public final class SerializerSuppliers {
    public static final Iterable<OutputStreamSerializerSupplier> FEED_SERIALIZER_SUPPLIERS = 
            ImmutableList.<OutputStreamSerializerSupplier> builder()
            .add(jsonMessageFeed())
            .add(htmlMessageFeed())
            .add(atomMessageFeed())
            .build();

    public static final Iterable<OutputStreamSerializerSupplier> ENTRY_SERIALIZER_SUPPLIERS = 
            ImmutableList.<OutputStreamSerializerSupplier> of(
                    jsonMessageEntry(),
                    htmlMessageEntry(), 
                    atomMessageEntry());

    // FIXME: Maybe make this standard API function in RestlibBio
    public static final Function<OutputStreamSerializerSupplier, MediaRange> OUTPUT_STREAM_SERIALIZER_SUPPLIER_TO_MEDIARANGE = 
            new Function<OutputStreamSerializerSupplier, MediaRange>() {
                public MediaRange apply(final OutputStreamSerializerSupplier supplier) {
                    return supplier.mediaRange();
                }
            };

    private static OutputStreamSerializerSupplier atomMessageEntry() {
        return TemplateServerSerializerSupplier.create(
                Templates.ATOM_ENTRY_TMPL, MediaRanges.APPLICATION_ATOM_ENTRY);
    }

    private static OutputStreamSerializerSupplier atomMessageFeed() {
        return TemplateServerSerializerSupplier.create(
                Templates.ATOM_FEED_TMPL, MediaRanges.APPLICATION_ATOM_FEED);
    }

    private static OutputStreamSerializerSupplier htmlMessageEntry() {
        return TemplateServerSerializerSupplier.create(
                Templates.HTML_ENTRY_TMPL, MediaRanges.TEXT_HTML_ENTRY);
    }

    private static OutputStreamSerializerSupplier htmlMessageFeed() {
        return TemplateServerSerializerSupplier.create(
                Templates.HTML_FEED_TMPL, MediaRanges.TEXT_HTML_FEED);
    }

    private static OutputStreamSerializerSupplier jsonMessageEntry() {
        return JsonServerSerializerSupplier.create(
                RestlibModuleFactory.objectMapper(),
                MediaRanges.APPLICATION_JSON_ENTRY,
                ImmutableList.<Language> of());
    }

    private static OutputStreamSerializerSupplier jsonMessageFeed() {
        return JsonServerSerializerSupplier.create(
                RestlibModuleFactory.objectMapper(),
                MediaRanges.APPLICATION_JSON_FEED,
                ImmutableList.<Language> of());
    }

    private SerializerSuppliers() {
    }
}
