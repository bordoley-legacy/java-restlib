package restlib.serializable.opensearch;

import restlib.data.MediaRange;

public interface SearchUrl {
    public SearchUrlTemplate template();
    public MediaRange type();
    public String rel();
    public long indexOffset();
    public long pageOffset();
}
