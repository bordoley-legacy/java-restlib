package restlib.server;

import java.util.Map;

import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public final class Resources {    
    public static final Resource NOT_FOUND = new NotFoundResource();
    
    public static Resource authorizedResource(
            final Resource next, 
            final Iterable<Authorizer> authorizers) {
        Preconditions.checkNotNull(next);
        Preconditions.checkNotNull(authorizers);
        Preconditions.checkArgument(!Iterables.isEmpty(authorizers));
        
        final ImmutableMap.Builder<String, Authorizer> mapBuilder = ImmutableMap.builder();
        for (final Authorizer authorizer : authorizers) {
            mapBuilder.put(Ascii.toLowerCase(authorizer.scheme()), authorizer);
        }
        
        final Map<String, Authorizer> authorizerMap = mapBuilder.build();
        
        return new AuthorizedResource(next, authorizerMap);
    }

    private Resources(){}
}
