package restlib.server.bio;

import com.google.common.base.Preconditions;

public final class BioResources {
    public static final BioResource<?> NOT_FOUND = new BioNotFoundResource();
    
    public static <T> BioResource<T> contentEncodingResource(final BioResource<T> resource, final int bufSize) {
        Preconditions.checkNotNull(resource);
        Preconditions.checkArgument(bufSize > 0);
        
        return new BioContentEncodingResource<T>(resource, bufSize);     
    }
    
    public static <T> BioResource<T> errorResource(final BioResource<T> resource) {
        Preconditions.checkNotNull(resource);
        return new BioErrorResource<T>(resource);
    }
    
    public static <T> BioResource<T> limitInputResource(final BioResource<T> resource, final long limit) {
        Preconditions.checkNotNull(resource);
        Preconditions.checkArgument(limit >= 0);
        
        return new LimitInputResource<T>(resource, limit); 
    }
    
    private BioResources(){}
}
