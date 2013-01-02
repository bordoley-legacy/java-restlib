package restlib.serializable.opensearch;

import restlib.serializable.atom.AtomEntry;
import restlib.serializable.atom.AtomFeed;

public interface SearchResultsFeed<T extends AtomEntry<?>> extends AtomFeed<T> {
    public long totalResults();
    public long startIndex();
    public long itemsPerPage();
    public Query query();
}
