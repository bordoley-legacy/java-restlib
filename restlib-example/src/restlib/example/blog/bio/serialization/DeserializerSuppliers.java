package restlib.example.blog.bio.serialization;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import restlib.Request;
import restlib.bio.InputStreamDeserializer;
import restlib.data.Form;
import restlib.data.MediaRange;
import restlib.data.MediaRanges;
import restlib.example.blog.serializable.MessageEntry;
import restlib.example.blog.serializable.MessageFeed;
import restlib.ext.jackson.JsonServerDeserializerSupplier;
import restlib.ext.jackson.RestlibModuleFactory;
import restlib.impl.BeanUtils;
import restlib.server.bio.InputStreamDeserializerSupplier;
import restlib.server.bio.InputStreamDeserializerSuppliers;

public final class DeserializerSuppliers {
    public static final Iterable<InputStreamDeserializerSupplier<MessageEntry>> ENTRY_DESERIALIZER_SUPPLIERS = 
            ImmutableList.<InputStreamDeserializerSupplier<MessageEntry>> of(jsonMessageEntry());

    public static final Iterable<InputStreamDeserializerSupplier<MessageFeed>> FEED_DESERIALIZER_SUPPLIERS = 
            ImmutableList.<InputStreamDeserializerSupplier<MessageFeed>> builder()
                .add(jsonMessageFeed()).add(jsonMessageEntryAsFeed())
                .add(formMessageEntryAsFeed()).build();

    // FIXME: Maybe make this standard API function in RestlibBio
    public static final Function<InputStreamDeserializerSupplier<?>, MediaRange> INPUT_STREAM_DESERIALIZER_SUPPLIER_TO_MEDIARANGE = 
            new Function<InputStreamDeserializerSupplier<?>, MediaRange>() {
                public MediaRange apply(
                        final InputStreamDeserializerSupplier<?> supplier) {
                    return supplier.mediaRange();
                }
            };

    private static InputStreamDeserializerSupplier<MessageFeed> formMessageEntryAsFeed() {
        final InputStreamDeserializerSupplier<String> delegate = 
                InputStreamDeserializerSuppliers
                    .stringDeserializerSupplier(MediaRanges.TEXT_ANY);

        return new InputStreamDeserializerSupplier<MessageFeed>() {
            @Override
            public InputStreamDeserializer<MessageFeed> get(final Request request) {
                Preconditions.checkNotNull(request);
                return new InputStreamDeserializer<MessageFeed>() {
                    @Override
                    public MessageFeed read(final InputStream is)
                            throws IOException {
                        final String form = delegate.get(request).read(is);
                        final MessageEntry.Builder builder = MessageEntry
                                .builder();

                        try {
                            BeanUtils.populateObject(
                                    Form.parse(form), builder);
                        } catch (final IllegalArgumentException e) {
                            throw new IOException(e);
                        }

                        return MessageFeed.builder()
                                .addEntry(builder.build())
                                .build();
                    }
                };
            }

            @Override
            public MediaRange mediaRange() {
                return MediaRanges.APPLICATION_WWW_FORM;
            }
        };
    }

    private static InputStreamDeserializerSupplier<MessageEntry> jsonMessageEntry() {
        final InputStreamDeserializerSupplier<MessageEntry.Builder> delegate = 
                JsonServerDeserializerSupplier
                    .create(MessageEntry.Builder.class,
                            RestlibModuleFactory.objectMapper(),
                            MediaRanges.APPLICATION_JSON_ENTRY);

        return new InputStreamDeserializerSupplier<MessageEntry>() {
            @Override
            public InputStreamDeserializer<MessageEntry> get(
                    final Request request) {
                return new InputStreamDeserializer<MessageEntry>() {
                    @Override
                    public MessageEntry read(final InputStream is)
                            throws IOException {
                        return delegate.get(request).read(is).build();
                    }
                };
            }

            @Override
            public MediaRange mediaRange() {
                return delegate.mediaRange();
            }
        };
    }

    private static InputStreamDeserializerSupplier<MessageFeed> jsonMessageEntryAsFeed() {
        final InputStreamDeserializerSupplier<MessageEntry.Builder> delegate = JsonServerDeserializerSupplier
                .create(MessageEntry.Builder.class,
                        RestlibModuleFactory.objectMapper(),
                        MediaRanges.APPLICATION_JSON_ENTRY);

        return new InputStreamDeserializerSupplier<MessageFeed>() {
            @Override
            public InputStreamDeserializer<MessageFeed> get(
                    final Request request) {
                Preconditions.checkNotNull(request);
                return new InputStreamDeserializer<MessageFeed>() {
                    @Override
                    public MessageFeed read(final InputStream is) throws IOException {
                        final MessageEntry messageEntry = 
                                delegate.get(request).read(is).build();
                        return MessageFeed.builder()
                                    .addEntry(messageEntry)
                                    .build();
                    }
                };
            }

            @Override
            public MediaRange mediaRange() {
                return MediaRanges.APPLICATION_JSON_ENTRY;
            }
        };
    }

    private static InputStreamDeserializerSupplier<MessageFeed> jsonMessageFeed() {
        final InputStreamDeserializerSupplier<MessageFeed.Builder> delegate = 
                JsonServerDeserializerSupplier
                    .create(MessageFeed.Builder.class,
                            RestlibModuleFactory.objectMapper(),
                            MediaRanges.APPLICATION_JSON_FEED);

        return new InputStreamDeserializerSupplier<MessageFeed>() {
            @Override
            public InputStreamDeserializer<MessageFeed> get(
                    final Request request) {
                return new InputStreamDeserializer<MessageFeed>() {
                    @Override
                    public MessageFeed read(final InputStream is) throws IOException {
                        return delegate.get(request).read(is).build();
                    }
                };
            }

            @Override
            public MediaRange mediaRange() {
                return delegate.mediaRange();
            }
        };
    }

    private DeserializerSuppliers() {
    }
}
