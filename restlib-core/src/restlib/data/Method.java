package restlib.data;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.Registry;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Object representation of a HTTP Method.
 */
@Immutable
public final class Method {
    private static final Registry<Method> _REGISTERED = new Registry<Method>();
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3744#section-8.1">[RFC3744], Section 8.1</a>
     */
    public static final Method ACL = register("ACL");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-12.6">[RFC3253], Section 12.6</a>
     */
    public static final Method BASELINE_CONTROL = register("BASELINE-CONTROL");

    /**
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-4">[RFC5842], Section 4</a>
     */
    public static final Method BIND = register("BIND");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-4.4">[RFC3253], Section 4.4</a>
     */
    public static final Method CHECKIN = register("CHECKIN");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-4.3">[RFC3253], Section 4.3</a>
     */
    public static final Method CHECKOUT = register("CHECKOUT");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.9">HTTP/1.1 CONNECT Method Definition</>
     */
    public static final Method CONNECT = register("CONNECT");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.8">[RFC4918], Section 9.8 </a>
     */
    public static final Method COPY = register("COPY");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.7">HTTP/1.1 DELETE Method Definition</a>
     */
    public static final Method DELETE = register("DELETE");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.3">HTTP/1.1 GET Method Definition</a>
     */
    public static final Method GET = register("GET");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.4">HTTP/1.1 HEADS Method Definition</a>
     */
    public static final Method HEAD = register("HEAD");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-8.2">[RFC3253], Section 8.2</a>
     */
    public static final Method LABEL = register("LABEL");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc2068#section-19.6.1.2">[RFC2068], Section 19.6.1.2</a>
     */
    public static final Method LINK = register("LINK");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.10">[RFC4918], Section 9.10</a>
     */
    public static final Method LOCK = register("LOCK");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-11.2">[RFC3253], Section 11.2</a>
     */
    public static final Method MERGE = register("MERGE");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-13.5">[RFC3253], Section 13.5</a>
     */
    public static final Method MKACTIVITY = register("MKACTIVITY");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4791#section-5.3.1">[RFC4791], Section 5.3.1</a>
     */
    public static final Method MKCALENDAR = register("MKCALENDAR");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.3">[RFC4918], Section 9.3</a>
     */
    public static final Method MKCOL = register("MKCOL");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4437#section-6">[RFC4437], Section 6</a>
     */
    public static final Method MKREDIRECTREF = register("MKREDIRECTREF");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-6.3">[RFC3253], Section 6.3</a>
     */
    public static final Method MKWORKSPACE = register("MKWORKSPACE");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.9">[RFC4918], Section 9.9</a>
     */
    public static final Method MOVE = register("MOVE");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.2">HTTP/1.1 OPTIONS Method Definition</a>
     */
    public static final Method OPTIONS = register("OPTIONS");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3648#section-7">[RFC3648], Section 7</a>
     */
    public static final Method ORDERPATCH = register("ORDERPATCH");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc5789#section-2">[RFC5789], Section 2</a>
     */
    public static final Method PATCH = register("PATCH");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.5">HTTP/1.1 POST Method Definition</a>
     */
    public static final Method POST = register("POST");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.1">[RFC4918], Section 9.1</a>
     */
    public static final Method PROPFIND = register("PROPFIND");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.2">[RFC4918], Section 9.2</a>
     */
    public static final Method PROPPATCH = register("PROPPATCH");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.6">HTTP/1.1 PUT Method Definition</a>
     */
    public static final Method PUT = register("PUT");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-6">[RFC5842], Section 6</a>
     */
    public static final Method REBIND = register("REBIND");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-3.6">[RFC3253], Section 3.6</a>
     */
    public static final Method REPORT = register("REPORT");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc5323#section-2">[RFC5323], Section 2</a>
     */
    public static final Method SEARCH = register("SEARCH");
    
    /**
     * @see <a href="http://tools.ietf.org/html/draft-ietf-httpbis-p2-semantics-17#section-6.8">HTTP/1.1 TRACE Method Definition</>
     */
    public static final Method TRACE = register("TRACE");   
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc5842#section-5">[RFC5842], Section 5</a>
     */
    public static final Method UNBIND = register("UNBIND");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-4.5">[RFC3253], Section 4.5</a>
     */
    public static final Method UNCHECKOUT = register("UNCHECKOUT");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc2068#section-19.6.1.3">[RFC2068], Section 19.6.1.3</a>
     */
    public static final Method UNLINK = register("UNLINK");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4918#section-9.11">[RFC4918], Section 9.11</a>
     */
    public static final Method UNLOCK = register("UNLOCK");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-7.1">[RFC3253], Section 7.1</a>
     */
    public static final Method UPDATE = register("UPDATE");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc4437#section-7">[RFC4437], Section 7</a>
     */
    public static final Method UPDATEDIRECTREF = register("UPDATEDIRECTREF");
    
    /**
     * @see <a href="http://tools.ietf.org/html/rfc3253#section-3.5">[RFC3253], Section 3.5</a>
     */
    public static final Method VERSION_CONTROL = register("VERSION-CONTROL");
    
    private static Method register(final String methodName) {
        Preconditions.checkNotNull(methodName);
        final Method method = Method.forName(methodName);
        return _REGISTERED.register(method);
    }
    
    /**
     * Returns an instance of Method for the given {@code methodName}.
     * @param methodName a non-null HTTP token.
     * @throws NullPointerException if {@code methodName} is null.
     * @throws IllegalArgumentException if {@code methodName} is not a valid 
     * HTTP token.
     * 
     */
    public static Method forName(final String methodName) {
        Preconditions.checkNotNull(methodName);
        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(methodName));
        return _REGISTERED.getIfPresent(new Method(methodName));
    }
 
    private final String methodName;
    
    private Method(final String methodName) {
        this.methodName = methodName;
    }
       
    @Override
    public int hashCode() {
        return Objects.hashCode(methodName);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Method) {
            final Method that = (Method) obj;
            return this.methodName.equals(that.methodName);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.methodName;
    }
}
