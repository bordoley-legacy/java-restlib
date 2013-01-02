package restlib.serializable.opensearch;

import restlib.serializable.atom.AtomEntry;
import restlib.serializable.atom.AtomFeed;
import restlib.serializable.atom.AtomFeedWrapper;

public abstract class SearchResultsFeedDecorator<T extends AtomEntry<?>> extends AtomFeedWrapper<T>
        implements SearchResultsFeed<T> {

    protected SearchResultsFeedDecorator(final AtomFeed<T> delegate) {
        super(delegate);
    }
}
