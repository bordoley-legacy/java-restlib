package restlib.server;

import restlib.Request;
import restlib.RequestWrapper;
import restlib.data.ExtensionHeaders;
import restlib.data.Method;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

final class MethodRequestFilter implements Function<Request, Request> {

    public MethodRequestFilter() {
        // TODO Auto-generated constructor stub
    }
    
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
}
