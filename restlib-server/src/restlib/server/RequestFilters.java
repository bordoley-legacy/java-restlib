package restlib.server;

import restlib.Request;
import restlib.RequestWrapper;
import restlib.data.ExtensionHeaders;
import restlib.data.ExtensionMap;
import restlib.data.Header;
import restlib.data.Method;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public final class RequestFilters {
    public static Function<Request, Request> DEFAULT_EXTENSION_FILTER =
            ExtensionFilter.getDefaultInstance();
            
    public static Function<Request, Request> METHOD_OVERRIDE =
            new Function<Request, Request>() {
                @Override
                public Request apply(final Request request) {
                    if (!request.method().equals(Method.POST)) {
                        // Method override is only allowed for POST
                        return request;
                    } else if (!request.customHeaders().containsKey(ExtensionHeaders.X_HTTP_METHOD_OVERRIDE)) {
                        return request;
                    }
                    
                    final Method method = 
                            Method.forName(
                                    Iterables.getFirst(
                                            request.customHeaders().get(ExtensionHeaders.X_HTTP_METHOD_OVERRIDE),
                                            Method.POST).toString());
                    return new RequestWrapper(request) {
                        @Override
                        public Method method() {
                            return method;
                        }
                    };
                }
            };
    
    public static Function<Request, Request> queryFilter(final Iterable<Header> headers) {  
        return new QueryFilter(ImmutableSet.copyOf(headers));   
    };
    
    public static Function<Request, Request> extensionFilter(final ExtensionMap extensionMap) {
        Preconditions.checkNotNull(extensionMap);
        return new ExtensionFilter(extensionMap);
    }
 
    private RequestFilters(){}
}
