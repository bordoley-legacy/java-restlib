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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * Implementation of {@link DateTime} that provides methods to parse and serializers valid HTTP-date strings.
 * <p> Note: The current implementation only supports parsing and serializing RFC-1123 compliant date strings. Future revisions
 * may support rfc850 and asctime representations for parsing only.
 */
@Immutable
public final class HttpDate extends DateTime {
    private static final ThreadLocal<DateFormat> FORMATTER = 
            new ThreadLocal<DateFormat>(){
                private static final String HTTP_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";
        
                @Override
                protected DateFormat initialValue() {
                    final DateFormat df = new SimpleDateFormat(HTTP_FORMAT);
                    df.setTimeZone(TimeZone.getTimeZone("GMT"));
                    return df;
                }
            };
    
    /**
     * Returns an instance of HttpDate that is equivalent to {@code date}.
     * @param date an instance of {@link DateTime}.
     * @throws NullPointerException if {@code date} is null.
     */
    public static HttpDate copyOf(final DateTime date) {
        Preconditions.checkNotNull(date);
        return (date instanceof HttpDate) ? (HttpDate) date : HttpDate.create(date.time());
    }
    
    /**
     * Returns an instance of HttpDate wrapping the time specified by {@code date}.
     * @param date the number of milliseconds from 1970-01-01T00:00:00Z
     */
    public static HttpDate create(final long date) {    
        return new HttpDate(date);
    }
    
    /**
     * Returns an instance of HttpDate using the exact current time.
     */
    public static HttpDate now() {
        return create(System.currentTimeMillis());
    }
            
    static HttpDate parse(final CharSequence date)  {
        Preconditions.checkNotNull(date);
        try {
            return create(FORMATTER.get().parse(date.toString()).getTime());
        } catch (final ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private HttpDate(final long date) {
        super(date);
    }

    @Override
    public String toString() {
        return FORMATTER.get().format(this.time());
    }
}
