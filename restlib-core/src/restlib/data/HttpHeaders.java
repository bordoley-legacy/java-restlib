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

package restlib.data;

import java.nio.CharBuffer;
import java.util.Collections;
import java.util.Set;

import restlib.impl.Optionals;
import restlib.impl.Parser;
import restlib.impl.Parsers;
import restlib.net.EmailAddress;
import restlib.net.Uri;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Headers defined in the HTTP specification.
 */
public final class HttpHeaders {
    private static final Set<Header> _STANDARD_HEADERS = 
            Collections.newSetFromMap(Maps.<Header, Boolean> newConcurrentMap());
    
    public static final Header ACCEPT = register("Accept");   
    private static final Parser<Iterable<Preference<MediaRange>>> ACCEPT_PARSER = 
            parsePreferenceList(MediaRange.PARSER, Primitives.OWS_COMMA_OWS_PARSER, MediaRange.class);
    public static final Header ACCEPT_CHARSET = register("Accept-Charset");
    private static final Parser<Iterable<Preference<Charset>>> ACCEPT_CHARSET_PARSER =
            parsePreferenceList(Charset.PARSER, Primitives.OWS_COMMA_OWS_PARSER, Charset.class);
    public static final Header ACCEPT_ENCODING = register("Accept-Encoding");
    private static final Parser<Iterable<Preference<ContentEncoding>>> ACCEPT_ENCODING_PARSER =
            parsePreferenceList(ContentEncoding.PARSER, Primitives.OWS_COMMA_OWS_PARSER, ContentEncoding.class);   
    public static final Header ACCEPT_LANGUAGE = register("Accept-Language"); 
    private static final Parser<Iterable<Preference<Language>>> ACCEPT_LANGUAGE_PARSER = 
            parsePreferenceList(Language.PARSER, Primitives.OWS_COMMA_OWS_PARSER, Language.class);
    public static final Header ACCEPT_RANGES = register("Accept-Ranges");
    public static final Header AGE = register("Age");
    public static final Header ALLOW = register("Allow");
    public static final Header AUTHORIZATION = register("Authorization");
    public static final Header CACHE_CONTROL = register("Cache-Control");
    private static  final Parser<Iterable<CacheDirective>> CACHE_CONTROL_PARSER =
            Parsers.listParser(
                    CacheDirective.PARSER,
                    Primitives.OWS_COMMA_OWS_PARSER,
                    CacheDirective.class);
    public static final Header CONNECTION = register("Connection"); 
    private static final Parser<Iterable<ConnectionOption>> CONNECTION_PARSER =
            Parsers.listParser(
                    ConnectionOption.PARSER,
                    Primitives.OWS_COMMA_OWS_PARSER,
                    ConnectionOption.class);
    public static final Header CONTENT_ENCODING = register("Content-Encoding");
    public static final Header CONTENT_LANGUAGE = register("Content-Language");
    private static final Parser<Iterable<Language>> CONTENT_LANGUAGE_PARSER = 
            Parsers.listParser(Language.PARSER, Primitives.OWS_COMMA_OWS_PARSER, Language.class);
    public static final Header CONTENT_LENGTH = register("Content-Length");
    public static final Header CONTENT_LOCATION = register("Content-Location");
    public static final Header CONTENT_MD5 = register("Content-MD5");
    public static final Header CONTENT_RANGE = register("Content-Range");
    public static final Header CONTENT_TYPE = register("Content-Type");
    public static final Header DATE = register("Date");
    public static final Header ENTITY_TAG = register("ETag");
    public static final Header EXPECT = register("Expect");
    private static final Parser<Iterable<Expectation>> EXPECT_PARSER = 
            Parsers.listParser(Expectation.PARSER, Primitives.OWS_COMMA_OWS_PARSER, Expectation.class);
    public static final Header EXPIRES = register("Expires");
    public static final Header FROM = register("From");
    public static final Header HOST = register("Host");
    public static final Header IF_MATCH = register("If-Match");
    private static Parser<Iterable<EntityTag>> IF_MATCH_PARSER =
            Parsers.listParser(EntityTag.PARSER, Primitives.OWS_COMMA_OWS_PARSER, EntityTag.class);
    
    public static final Header IF_MODIFIED_SINCE = register("If-Modified-Since");
    public static final Header IF_NONE_MATCH = register("If-None-Match");
    private static Parser<Iterable<EntityTag>> IF_NONE_MATCH_PARSER = IF_MATCH_PARSER;
    
    public static final Header IF_RANGE = register("If-Range");
    public static final Header IF_UNMODIFIED_SINCE = register("If-Unmodified-Since");
    public static final Header LAST_MODIFIED = register("Last-Modified");
    public static final Header LOCATION = register("Location");
    public static final Header MAX_FORWARDS = register("Max-Forwards");
    public static final Header PRAGMA = register("Pragma");
    public static final Header PROXY_AUTHENTICATE = register("Proxy-Authenticate");
    private static Parser<Iterable<ChallengeMessage>> PROXY_AUTHENTICATE_PARSER = 
            Parsers.listParser(ChallengeMessage.PARSER, Primitives.OWS_COMMA_OWS_PARSER, ChallengeMessage.class);  
    public static final Header PROXY_AUTHORIZATION = register("Proxy-Authorization");
    public static final Header RANGE = register("Range");
    public static final Header REFERER = register("Referer");
    public static final Header RETRY_AFTER = register("Retry-After");
    public static final Header SERVER = register("Server");
    public static final Header TE = register("TE");
    private static final Parser<Iterable<Preference<TransferCoding>>> TE_PARSER = 
            parsePreferenceList(
                    TransferCoding.PARSER, Primitives.OWS_COMMA_OWS_PARSER, TransferCoding.class);
    public static final Header TRAILER = register("Trailer");
    private static final Parser<Iterable<Header>> TRAILER_PARSER =
            Parsers.listParser(Header.PARSER, Primitives.OWS_COMMA_OWS_PARSER, Header.class);
    public static final Header TRANSFER_ENCODING = register("Transfer-Encoding");
    private static final Parser<Iterable<TransferCoding>> TRANSFER_ENCODING_PARSER =
            Parsers.listParser(
                    TransferCoding.PARSER, Primitives.OWS_COMMA_OWS_PARSER, TransferCoding.class);
    public static final Header UPGRADE = register("Upgrade");
    private static final Parser<Iterable<Protocol>> UPGRADE_PARSER =
            Parsers.listParser(
                    Protocol.PARSER, 
                    Primitives.OWS_COMMA_OWS_PARSER,
                    Protocol.class);
    public static final Header USER_AGENT = register("User-Agent");
    public static final Header VARY = register("Vary");
    private static final Parser<Iterable<Header>> VARY_PARSER = TRAILER_PARSER;
    public static final Header VIA = register("Via");  
    private static final Parser<Iterable<Via>> VIA_PARSER =
            Parsers.listParser(
                    Via.PARSER, 
                    Primitives.OWS_COMMA_OWS_PARSER,
                    Via.class);
    public static final Header WARNING = register("Warning");
    private static final Parser<Iterable<Warning>> WARNING_PARSER =
            Parsers.listParser(
                    Warning.PARSER, 
                    Primitives.OWS_COMMA_OWS_PARSER,
                    Warning.class);
    public static final Header WWW_AUTHENTICATE = register("WWW-Authenticate");
    private static Parser<Iterable<ChallengeMessage>> WWW_AUTHENTICATE_PARSER = PROXY_AUTHENTICATE_PARSER;

    public static boolean isStandardHeader(final Header header) {
        return _STANDARD_HEADERS.contains(header);
    }
    
    public static Iterable<Preference<MediaRange>> parseAccept(final CharSequence in) {
        return Parsers.parseWithParser(in, ACCEPT_PARSER);
    }
    
    public static Iterable<Preference<Charset>> parseAcceptCharset(final CharSequence in) {
        return Parsers.parseWithParser(in, ACCEPT_CHARSET_PARSER);
    }
    
    public static Iterable<Preference<ContentEncoding>> parseAcceptEncoding(final CharSequence in) {
        return Parsers.parseWithParser(in, ACCEPT_ENCODING_PARSER);
    }
    
    public static Iterable<Preference<Language>> parseAcceptLanguage(final CharSequence in) {
        return Parsers.parseWithParser(in, ACCEPT_LANGUAGE_PARSER);
    }
    
    public static Iterable<RangeUnit> parseAcceptRanges(final CharSequence in) {
        // FIXME: Implement me.
        throw new UnsupportedOperationException();
    }
    
    public static long parseAge(final CharSequence in) {
        // FIXME: Implement me.
        throw new UnsupportedOperationException();
    }
    
    public static Iterable<Method> parseAllow(final CharSequence in) {
        // FIXME: Implement me.
        throw new UnsupportedOperationException();
    }
    
    public static Iterable<CacheDirective> parseCacheControl(final CharSequence in) {
        return Parsers.parseWithParser(in, CACHE_CONTROL_PARSER);
    }
    
    public static Iterable<ConnectionOption> parseConnection(final CharSequence in) {
        return Parsers.parseWithParser(in, CONNECTION_PARSER);
    }
    
    public static Iterable<ContentEncoding> parseContentEncoding(final CharSequence in) {
        return Parsers.parseWithParser(in, ContentEncoding.LIST_PARSER);
    }
    
    public static Iterable<Language> parseContentLanguage(final CharSequence in) {
        return Parsers.parseWithParser(in, CONTENT_LANGUAGE_PARSER);
    }
    
    public static long parseContentLength(final CharSequence in) {
        return Parsers.parseWithParser(in, Parsers.LONG_PARSER);
    }
    
    public static Uri parseContentLocation(final CharSequence in) {
        return Uri.parse(in);
    }

    public static ContentRange parseContentRange(final CharSequence in) {
        // FIXME: Implement me.
        throw new UnsupportedOperationException();
    }
    
    public static MediaRange parseContentType(final CharSequence in) {
        return Parsers.parseWithParser(in, MediaRange.PARSER);
    }
    
    public static HttpDate parseDate(final CharSequence in) {
        return HttpDate.parse(in);
    }
    
    public static EntityTag parseEntityTag(final CharSequence in) {
        return Parsers.parseWithParser(in, EntityTag.PARSER); 
    }
    
    public static Iterable<Expectation> parseExpect(final CharSequence in) {
        return Parsers.parseWithParser(in, EXPECT_PARSER); 
    }
    
    public static HttpDate parseExpires(final CharSequence in) {
        return HttpDate.parse(in);
    }
    
    public static EmailAddress parseFrom(final CharSequence in) {
        return EmailAddress.parse(in);
    }
    
    public static Iterable<EntityTag> parseIfMatch(final CharSequence in) {
        return Parsers.parseWithParser(in, IF_MATCH_PARSER); 
    }
    
    public static HttpDate parseIfModifiedSince(final CharSequence in) {
        return HttpDate.parse(in);
    }
       
    public static Iterable<EntityTag> parseIfNoneMatch(final CharSequence in) {
        return Parsers.parseWithParser(in, IF_NONE_MATCH_PARSER); 
    }
    
    public static Object parseIfRange(final CharSequence in) {
        try {
            return HttpDate.parse(in);
        } catch (final IllegalArgumentException e) {
            return Parsers.parseWithParser(in, EntityTag.PARSER);
        }
    }
    
    public static HttpDate parseIfUnmodifiedSince(final CharSequence in) {
        return HttpDate.parse(in);
    }
    
    public static HttpDate parseLastModified(final CharSequence in) {
        return HttpDate.parse(in);
    }
    
    public static Uri parseLocation(final CharSequence in) {
        return Uri.parse(in);
    }
    
    public static Integer parseMaxForwards(final CharSequence in) {
        return Parsers.parseWithParser(in, Parsers.INTEGER_PARSER);
    }
    
    public static Iterable<CacheDirective> parsePragma(final CharSequence in) {
        return Parsers.parseWithParser(in, CACHE_CONTROL_PARSER);
    }
    
    private static <T extends Matcheable<T>> Parser<Iterable<Preference<T>>> parsePreferenceList(
            final Parser<T> itemParser, 
            final Parser<?> seperatorParser, 
            final Class<T> itemClass) {
        final Function<Object,Preference<T>> cast =
                new Function<Object,Preference<T>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Preference<T> apply(final Object input) {
                        return (Preference<T>) input;
                    }                        
                };
    
        final Parser<Iterable<?>> parser = 
                Parsers.untypedListParser(Preference.parser(itemParser, itemClass), seperatorParser, Preference.class);
            
        return new Parser<Iterable<Preference<T>>>() {          
            @Override
            public Optional<Iterable<Preference<T>>> parse(final CharBuffer buffer) {
                Preconditions.checkNotNull(buffer);
                final Optional<Iterable<?>> parsed = parser.parse(buffer);
                if (Optionals.isAbsent(parsed)) {
                    return Optional.absent();
                }
                return Optional.of(Iterables.transform(parsed.get(), cast));
            }      
        };
    }
    
    public static Iterable<ChallengeMessage> parseProxyAuthenticate(final CharSequence in) {
        return Parsers.parseWithParser(in, PROXY_AUTHENTICATE_PARSER);
    }
    
    public static ChallengeMessage parseProxyAuthorization(final CharSequence in) {
        return Parsers.parseWithParser(in, ChallengeMessage.PARSER);
    }
    
    public static Range parseRange(final CharSequence in) {
        return Parsers.parseWithParser(in, Range.PARSER);
    }
    
    public static Uri parseReferer(final CharSequence in) {
        return Uri.parse(in);
    }
    
    public static Object parseRetryAfter(final CharSequence in) {
        // FIXME: Implement me.
        throw new UnsupportedOperationException();
    }
    
    public static UserAgent parseServer(final CharSequence in) {
        return UserAgent.parse(in);
    }
    
    public static ChallengeMessage parsetAuthorization(final CharSequence in) {
        return Parsers.parseWithParser(in, ChallengeMessage.PARSER);
    }
    
    public static Iterable<Preference<TransferCoding>> parseTE(final CharSequence in) {
        return Parsers.parseWithParser(in, TE_PARSER);
    }
    
    public static Iterable<Header> parseTrailer(final CharSequence in) {
        return Parsers.parseWithParser(in, TRAILER_PARSER);
    }
    
    public static Iterable<TransferCoding> parseTransferEncoding(final CharSequence in) {
        return Parsers.parseWithParser(in, TRANSFER_ENCODING_PARSER);
    }
    
    public static Iterable<Protocol> parseUpgrade(final CharSequence in) {
        return Parsers.parseWithParser(in, UPGRADE_PARSER);
    }
    
    public static UserAgent parseUserAgent(final CharSequence in) {
        return UserAgent.parse(in);
    }
    
    public static Iterable<Header> parseVary(final CharSequence in) {
        return Parsers.parseWithParser(in, VARY_PARSER);
    }
    
    public static Iterable<Via> parseVia(final CharSequence in) {
        return Parsers.parseWithParser(in, VIA_PARSER);
    }
    
    public static Iterable<Warning> parseWarning(final CharSequence in) {
        return Parsers.parseWithParser(in, WARNING_PARSER);
    }
    
    public static Iterable<ChallengeMessage> parseWWWAuthenticate(final CharSequence in) {
        return Parsers.parseWithParser(in, WWW_AUTHENTICATE_PARSER);
    }
    
    private static Header register(final String header) {
        final Header retval = Header.register(Header.create(header));   
        _STANDARD_HEADERS.add(retval);
        return retval;       
    }

    private HttpHeaders() {
    }
}
