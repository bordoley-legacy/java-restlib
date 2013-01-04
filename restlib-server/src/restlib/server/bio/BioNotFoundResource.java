package restlib.server.bio;

import restlib.Request;
import restlib.Response;
import restlib.bio.InputStreamDeserializer;
import restlib.bio.OutputStreamSerializer;
import restlib.server.Resources;


final class BioNotFoundResource extends BioResourceDecorator<String> {

    public BioNotFoundResource() {
        super(Resources.NOT_FOUND);
    }

    @Override
    public InputStreamDeserializer<String> getRequestEntityDeserializer(final Request request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
        return OutputStreamSerializerSuppliers.STRING_AS_PLAIN_TEXT_SERIALIZER_SUPPLIER.get(request, response);
    }
}
