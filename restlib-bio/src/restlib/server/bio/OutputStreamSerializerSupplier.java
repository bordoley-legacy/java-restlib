package restlib.server.bio;

import restlib.Request;
import restlib.Response;
import restlib.bio.OutputStreamSerializer;
import restlib.data.MediaRange;

public interface OutputStreamSerializerSupplier {
    public OutputStreamSerializer get(Request request, Response response);
    public MediaRange mediaRange();
}
