package restlib.server.bio;

import restlib.Request;
import restlib.bio.InputStreamDeserializer;
import restlib.data.MediaRange;

public interface InputStreamDeserializerSupplier<T> {
    public InputStreamDeserializer<T> get(Request request);
    public MediaRange mediaRange();
}
