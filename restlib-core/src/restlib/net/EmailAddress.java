/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

/**
 * An <a href="http://tools.ietf.org/html/rfc5322">RFC 5322</a> compliant email address.
 * 
 * <p> Note: This class only supports a strict subset of the RFC 5322 legal 
 * addr-spec syntax. Specifically the local-part must use the dot-atom syntax with
 * no comments. The domain part supports any valid domain name, IPv4 or IPv6 address,
 * but must not contain comments or free whitespace.
 */
@Immutable
public final class EmailAddress {
    private static final CharMatcher ATEXT = 
            CharMatcher.inRange('a','z')
                .or(CharMatcher.inRange('A', 'Z'))
                .or(CharMatcher.inRange('0', '9'))
                .or(CharMatcher.anyOf("!#$%&'*+-/=?^_`{|}~"));
    
    private static final Joiner DOT_ATOM_TEXT_JOINER =
            Joiner.on('.');
    
    private static final Splitter DOT_ATOM_TEXT_SPLITTER = 
            Splitter.on('.').trimResults();
    
    /**
     * Creates a new {@code EmailAddress} from its localPart and domain.
     * @param localPart
     * @param domain
     * @throws NullPointerException if either {@code localPart} or {@code domain} are null.
     * @throws IllegalArgumentException if {@code localPart} is not valid 
     * {@code dot-atom-text} or if {@code domain} is not a valid domain name or IP address.
     */
    @VisibleForTesting
    static EmailAddress create(final String localPart, final String domain) {
        Preconditions.checkNotNull(localPart);
        Preconditions.checkNotNull(domain);
        
        return new EmailAddress(validateLocalPartAsDotAtom(localPart), validateDomain(domain));
    }
    
    /**
     * Parses an {@code EmailAddress} from its {@code String} representation.
     * @throws NullPointerException if {@code email} is null.
     * @throws IllegalArgumentException if {@code email} is not parseable.
     */
    public static EmailAddress parse(final CharSequence email) {
        Preconditions.checkNotNull(email);
        
        final List<String> parts = NetParserImpl.parseEmail(email);
        return create(parts.get(0), parts.get(1));
    }
    
    private static String validateDomain(String domain) {
        Preconditions.checkNotNull(domain);
        Preconditions.checkArgument(!domain.isEmpty());
        
        try {
            return InternetDomainName.from(domain).name();
        } catch (final IllegalArgumentException e){}
        
        if (domain.startsWith("[") && domain.endsWith("]")) {
            domain = domain.substring(1, domain.length() - 1);
        }
       
        if (domain.startsWith("IPv6:")) {
            domain = domain.substring(5, domain.length());
            final InetAddress address = InetAddresses.forString(domain);
            Preconditions.checkArgument(address instanceof Inet6Address);
            return "[IPv6:" + InetAddresses.toAddrString(address) + "]";
        } else {
            final InetAddress address = InetAddresses.forString(domain);
            Preconditions.checkArgument(address instanceof Inet4Address);
            return InetAddresses.toAddrString(address);
        }
    }
    
    private static String validateLocalPartAsDotAtom(final String localPart) {
        Preconditions.checkNotNull(localPart);
        Preconditions.checkArgument(!localPart.isEmpty());
        
        final Iterable<String> parts = DOT_ATOM_TEXT_SPLITTER.split(localPart);
        for(final String part : parts) {
            Preconditions.checkArgument(!part.isEmpty());
            Preconditions.checkArgument(ATEXT.matchesAllOf(part));
        }
        
        return DOT_ATOM_TEXT_JOINER.join(parts);        
    };
    
    private final String domain;
    private final String localPart;
    
    private EmailAddress(final String localPart, final String domain){
        this.localPart = localPart;
        this.domain = domain;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof EmailAddress) {
            final EmailAddress that = (EmailAddress) obj;
            return this.localPart.equals(that.localPart) &&
                    this.domain.equals(that.domain);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.localPart, this.domain);
    }

    @Override
    public String toString() {
        return this.localPart + "@" + domain;
    }
}
