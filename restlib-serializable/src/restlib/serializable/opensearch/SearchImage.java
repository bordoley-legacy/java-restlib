package restlib.serializable.opensearch;

import restlib.data.MediaRange;
import restlib.net.Uri;

public interface SearchImage {
    public long heigth();
    public long width();
    public MediaRange type();
    public Uri uri();
}
