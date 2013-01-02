package restlib.serializable.opensearch;

import com.google.common.base.Ascii;

public enum SyndicationRight {
    OPEN,
    LIMITED,
    PRIVATE,
    CLOSED;  
    
    public String toString() {
        return Ascii.toLowerCase(this.name());
    }
}
