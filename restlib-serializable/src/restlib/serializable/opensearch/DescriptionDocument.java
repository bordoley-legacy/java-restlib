package restlib.serializable.opensearch;

import restlib.data.Charset;
import restlib.data.Language;
import restlib.net.EmailAddress;

public interface DescriptionDocument {
    public boolean adultContent();
    public String attribution();
    public EmailAddress contact();
    public String description();
    public String developer();
    public SearchImage image();
    public Charset inputEncoding();
    public Language language();
    public String longName();
    public Charset outputEncoding();
    public Query query();
    public String shortName();
    public SyndicationRight syndicationRight();
    public Iterable<String> tags();
    public SearchUrl url();
}
