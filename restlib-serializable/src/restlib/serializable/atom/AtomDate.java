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


package restlib.serializable.atom;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import restlib.data.DateTime;

import com.google.common.base.Preconditions;

// Adopted from:
// http://svn.apache.org/repos/asf/abdera/java/trunk/core/src/main/java/org/apache/abdera/model/AtomDate.java
// with major modification
public final class AtomDate extends DateTime { 
    private static ThreadLocal<Calendar> CALENDAR = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        }
    };

    private static final Pattern PATTERN =
        Pattern
            .compile("(\\d{4})(?:-(\\d{2}))?(?:-(\\d{2}))?(?:([Tt])?(?:(\\d{2}))?(?::(\\d{2}))?(?::(\\d{2}))?(?:\\.(\\d{3}))?)?([Zz])?(?:([+-])(\\d{2}):(\\d{2}))?");
    
    public static AtomDate copyOf(final DateTime date) {
        Preconditions.checkNotNull(date);
        return (date instanceof AtomDate) ? (AtomDate) date : AtomDate.of(date.time());
    }
    
    public static AtomDate of(final long date) {    
        return new AtomDate(date);
    }
    
    // Fixme: remove regex logic from here and hide in a parsing factory.
    public static AtomDate parse(final String date) {
        Preconditions.checkNotNull(date);        
        final Matcher m = PATTERN.matcher(date);
        
        Preconditions.checkArgument(m.find() && (m.group() != null));

        final Calendar c = CALENDAR.get();
        c.clear();
        
        int hoff = 0, moff = 0, doff = -1;
        
        if (m.group(10) != null) {
            doff = m.group(10).equals("-") ? 1 : -1;
            hoff = doff *
                    (m.group(11) != null ? Integer.parseInt(m.group(11)) : 0);
            moff = doff *
                    (m.group(12) != null ? Integer.parseInt(m.group(12)) : 0);
        }
        
        c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
        c.set(Calendar.MONTH, m.group(2) != null ? Integer.parseInt(m.group(2)) - 1 : 0);
        c.set(Calendar.DATE, m.group(3) != null ? Integer.parseInt(m.group(3)) : 1);
        c.set(Calendar.HOUR_OF_DAY, m.group(5) != null ? Integer.parseInt(m.group(5)) + hoff : 0);
        c.set(Calendar.MINUTE, m.group(6) != null ? Integer.parseInt(m.group(6)) + moff : 0);
        c.set(Calendar.SECOND, m.group(7) != null ? Integer.parseInt(m.group(7)) : 0);
        c.set(Calendar.MILLISECOND, m.group(8) != null ? Integer.parseInt(m.group(8)) : 0);
        
        return new AtomDate(c.getTimeInMillis());
    }

    private AtomDate(long date) {
        super(date);
    }
    
    @Override
    public String toString() {        
        final StringBuilder sb = new StringBuilder();
        
        final Calendar c = CALENDAR.get();  
        c.clear();
        c.setTimeInMillis(this.time());
        
        sb.append(c.get(Calendar.YEAR));
        sb.append('-');
        
        int f = c.get(Calendar.MONTH);   
        if (f < 9) { sb.append('0'); }
        sb.append(f + 1);
        sb.append('-');
        
        f = c.get(Calendar.DATE);
        
        if (f < 10) { sb.append('0'); }
        sb.append(f);
        sb.append('T');
        
        f = c.get(Calendar.HOUR_OF_DAY);
        if (f < 10) { sb.append('0'); }
        sb.append(f);
        sb.append(':');
        
        f = c.get(Calendar.MINUTE);       
        if (f < 10) { sb.append('0'); }
        sb.append(f);
        sb.append(':');
        
        f = c.get(Calendar.SECOND);
        if (f < 10) { sb.append('0'); }
        sb.append(f);
        sb.append('.');
        
        f = c.get(Calendar.MILLISECOND);    
        if (f < 100) { sb.append('0'); }      
        if (f < 10) { sb.append('0'); }       
        sb.append(f);
        sb.append('Z');
        
        return sb.toString();
    }
}