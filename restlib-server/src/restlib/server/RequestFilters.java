package restlib.server;

import restlib.Request;
import restlib.data.ExtensionMap;
import restlib.data.Header;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public final class RequestFilters {
    public static Function<Request, Request> DEFAULT_EXTENSION_FILTER =
            ExtensionRequestFilter.getDefaultInstance();
            
    public static Function<Request, Request> METHOD_OVERRIDE = new MethodRequestFilter();
    
    public static Function<Request, Request> queryFilter(final Iterable<Header> headers) {  
        return new QueryRequestFilter(ImmutableSet.copyOf(headers));   
    };
    
    public static Function<Request, Request> extensionFilter(final ExtensionMap extensionMap) {
        Preconditions.checkNotNull(extensionMap);
        return new ExtensionRequestFilter(extensionMap);
    }
 
    private RequestFilters(){}
}
