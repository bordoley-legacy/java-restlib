package restlib.server.bio;

import restlib.Request;
import restlib.Response;
import restlib.bio.OutputStreamSerializer;
import restlib.data.Status;

import com.google.common.base.Preconditions;

final class BioErrorResource<T> extends BioResourceWrapper<T>{

    public BioErrorResource(final BioResource<T> resource) {
        super(resource);
    }
    
    @Override
    public OutputStreamSerializer getResponseEntitySerializer(final Request request, final Response response) {
        Preconditions.checkNotNull(response);
        if (!response.status().statusClass().equals(Status.Class.SUCCESS)) {
            return OutputStreamSerializerSuppliers.STRING_AS_PLAIN_TEXT_SERIALIZER_SUPPLIER.get(request, response);            
        }
        return super.getResponseEntitySerializer(request, response);
    }
}
