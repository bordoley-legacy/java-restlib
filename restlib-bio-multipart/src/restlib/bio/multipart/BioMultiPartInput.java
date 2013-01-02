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


package restlib.bio.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;

/*
 * This class is not thread safe.
 */
public final class BioMultiPartInput implements Iterable<BodyPartInputStream> {  
    private static final byte[] DASH_DASH = "--".getBytes(Charsets.US_ASCII);
    private static final byte[] CRLF = "\r\n".getBytes(Charsets.US_ASCII);

    public static BioMultiPartInput wrap(final InputStream is, final byte[] boundary) {
        Preconditions.checkNotNull(is);
        Preconditions.checkNotNull(boundary);
        return new BioMultiPartInput(is, boundary);
    }

    private final BoundaryInputStream is;
    private final Iterator<BodyPartInputStream> partsIterator;
    
    // State variables
    private BoundaryInputStream currStream = null;
    private boolean preambleAvailable = true;
    private boolean partsIteratorAvailable = true;

    private BioMultiPartInput(final InputStream src, final byte[] boundary) {
        this.is = BoundaryInputStream.wrap(src, 
                    Bytes.concat(CRLF, DASH_DASH, boundary, DASH_DASH));
        
        // Initialize the currentStream to the preamble
        this.currStream = BoundaryInputStream.wrap(this.is, Bytes.concat(DASH_DASH, boundary, CRLF));
        this.partsIterator = new Iterator<BodyPartInputStream> () { 
            final byte[] delimeter = Bytes.concat(CRLF, DASH_DASH, boundary, CRLF);
            
            @Override
            public boolean hasNext() {
                if (preambleAvailable) {
                    return false;
                } else if (is.boundaryFound()) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public BodyPartInputStream next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                
                try {
                    final InputStream next = currStream.getRemainingStream();
                    currStream = BoundaryInputStream.wrap(next, delimeter);
                    return BodyPartInputStream.wrap(currStream);
                } catch (final IOException e) {
                    throw new NoSuchElementException(e.getMessage());
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();               
            }           
        };
    }

    public InputStream getEpilogue() throws IOException {
        this.preambleAvailable = false;
        this.partsIteratorAvailable = false;
        return this.is.getRemainingStream();
    }

    public InputStream getPreamble() {
        if (this.preambleAvailable) {
            this.preambleAvailable = false;
            return currStream;
        }
        throw new IllegalStateException();
    }

    public Iterator<BodyPartInputStream> iterator() {
        Preconditions.checkState(this.partsIteratorAvailable);
        if (this.preambleAvailable) {
            this.preambleAvailable = false;
            try {
                while(currStream.read() != -1);
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
        this.partsIteratorAvailable = false;
        return this.partsIterator;
    }
}
