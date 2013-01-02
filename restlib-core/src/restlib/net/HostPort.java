package restlib.net;

import java.net.IDN;
import java.net.InetAddress;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import restlib.impl.CommonParsers;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

/**
 * An immutable representation of a host and port.
 */
@Immutable
public final class HostPort {    
    private static HostPort create(final InetAddress host, final int port) {
        Preconditions.checkNotNull(host);        
        return new HostPort(InetAddresses.toUriString(host), getPort(port));
    }
    
    private static HostPort create(final InternetDomainName host, final int port) {
        Preconditions.checkNotNull(host);       
        return new HostPort(IDN.toASCII(host.name()), getPort(port));
    }
    
    /**
     * Constructs a HostPort instance from its component parts.
     * @param host a valid domain name, IPv4 address or IPv6 address uri-host.
     * @param port a valid TCP port number or -1 to indicate no port.
     * @throws NullPointerException if host is null.
     * @throws IllegalArgumentException if host is not valid uri-host. Also if port
     * is not a valid TCP port or -1.
     */
    public static HostPort create(final String host, final int port) {
        Preconditions.checkNotNull(host);
        Preconditions.checkArgument(!host.isEmpty());
        Preconditions.checkArgument(IRIPredicates.IS_URI_CHARS.apply(host));
        
        try {
            return HostPort.create(InternetDomainName.from(host), port);
        } catch (final IllegalArgumentException e) {}
        
        try {        
            return HostPort.create(InetAddresses.forUriString(host), port);
        } catch (final IllegalArgumentException e) {               
        }

        throw new IllegalArgumentException("Invalid host specifier: " + host);
    }
    
    private static Optional<Integer> getPort(final int port) {
        Preconditions.checkArgument((port == -1) || ((port > 0) && (port <  65536)));
        if (port > 0) {
            return Optional.of(port);
        } else {
            return Optional.absent();
        }
    }
    
    /**
     * Returns a HostPort including only a host component.
     * @param host a valid domain name, IPv4 address or IPv6 address uri-host.
     * @throws NullPointerException if host is null.
     * @throws IllegalArgumentException if host is not valid uri-host.
     */
    public static HostPort hostOnly(final String host) {
        return create(host, -1);
    }
    
    /**
     * Parses a {@code HostPort} from its {@code String} representation.
     * @throws NullPointerException if {@code hostPort} is null.
     * @throws IllegalArgumentException {@code hostPort} is not parseable or 
     * is the empty String.
     */
    public static HostPort parse(final CharSequence hostPort) {
        Preconditions.checkNotNull(hostPort);
        
        final List<String> parts = NetParserImpl.parseAuthority(hostPort);
        Preconditions.checkArgument(parts.get(0).isEmpty());      
        
        return create(parts.get(1), 
                parts.get(2).isEmpty() ? -1 : CommonParsers.parseUnsignedInteger(parts.get(2)));
    }
    
    private final String host;
    private final Optional<Integer> port;
    
    private HostPort(final String host, final Optional<Integer> port) {
        this.host = host;
        this.port = port; 
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof HostPort) {
            final HostPort that = (HostPort) obj;
            return this.host.equals(that.host) &&
                    (this.port.equals(that.port));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.host, this.port);
    }
    
    /**
     * Returns the portion of this HostPort instance that should 
     * represent the hostname or IPv4/IPv6 literal.
     */
    public String host() {
        return host;
    }
    
    /**
     * Returns the port number if present
     */
    public Optional<Integer> port() {
        return port;
    }

    @Override
    public String toString() {
        return host + (port.isPresent() ? ":" + port.get() : "");
    }   
}
