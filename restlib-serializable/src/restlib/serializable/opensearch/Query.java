package restlib.serializable.opensearch;

import restlib.data.Charset;
import restlib.data.Language;

public interface Query {
    public long count();
    public Charset inputEncoding();
    public Language language();
    public Charset outputEncoding();
    public String role();
    public String searchTerms();
    public long startIndex();
    public long startPage();
    public String title();
    public long totalResults();
}
