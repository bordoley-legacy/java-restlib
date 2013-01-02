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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Bytes;

class BoundaryInputStream extends FilterInputStream{
    static BoundaryInputStream wrap(final InputStream is, final byte[] boundary) {
        return new BoundaryInputStream(is, boundary);
    }
    
    private final byte[] boundary;
    private final int bufferSize;
    private final PushbackInputStream is;
    private final byte[] readByte = new  byte[1];
    
    // State variables
    private boolean boundaryReached = false;    // True only if a boundary is found and all data leading
                                                // up to it has been read by the caller;
    private int boundaryPos = -1;                // The position of the boundary in the buffer
    private byte[] buffer = null;
    private int bufferIndex = 0;                // The position of the next byte in the buffer
                                                // to be read with read();
    private boolean eof = false;                // Whether eof has been reached in the src InputStream
    private int numBytesInBuffer = 0;
    
    private BoundaryInputStream(final InputStream is, final byte[] boundary) {
        super(is);
        this.boundary = boundary;
        this.bufferSize = this.boundary.length * 2;
        this.is = new PushbackInputStream (is, bufferSize);
    }

    public boolean boundaryFound() {
        return boundaryReached;
    }
    
    // Finds the boundary and unreads any data after it back into the PushbackInputStream
    private void findBoundary() throws IOException {
        // FIXME: Guava's search algorithm appears to be O(n^2) might want to optimize here
        this.boundaryPos = Bytes.indexOf(this.buffer, this.boundary);
        
        if (this.boundaryPos >= 0) {
            int boundaryEndPos = this.boundaryPos + this.boundary.length;
        
            // Highly unlikely, but if by some random chance the buffer includes the boundary
            // even though the full buffer isn't populated ignore the found boundary
            if (boundaryEndPos > this.numBytesInBuffer) {
                this.boundaryPos = -1;
                
            // Unread bytes after the boundary    
            } else { 
                int numBytesToUnread = this.numBytesInBuffer - boundaryEndPos;
                this.is.unread(this.buffer, boundaryEndPos, numBytesToUnread);
            }
        }
    }
    
    public InputStream getRemainingStream() throws IOException {
        // finish reading the stream
        while (this.read() != -1);
        return this.is;
    }
    
    @Override
    public int read() throws IOException {
        // FIXME: Can avoid the byte copy here
        
        if (this.read(readByte) < 0) {
            return -1;
        } else {
            return readByte[0];
        }
    }
    
    @Override
    public int read(final byte[] dstBuf, final int dstOff, final int len) throws IOException {
        Preconditions.checkArgument(dstOff < dstBuf.length);
        Preconditions.checkArgument((len - dstOff) <= dstBuf.length);
        
        // Return -1 if the boundary has been reached.
        if (this.boundaryReached) {
            return -1;
        } 
        
        // Return -1 if eof has been reached in the src InputStream
        // and there is no more data in the buffer to return to the caller
        if (this.eof) {
            if (this.bufferIndex >= this.numBytesInBuffer) {
                return -1;
            }
        }
        
        // If this is the first time read is being called fill the buffer
        if (null == this.buffer) {
            this.buffer = new byte[this.bufferSize];
            this.bufferIndex = 0;
            this.numBytesInBuffer = this.is.read(this.buffer);
            
            // return -1 if no data is read from the InputStream
            // garbage collect the buffer
            if (this.numBytesInBuffer < 0) {
                this.buffer = null;
                this.eof = true;
                return -1;
            }
            
            // Search the buffer for the boundary
            this.findBoundary();
        }
        
        
        int numBytesRead = 0;
        while (!this.boundaryReached && (numBytesRead < len)) { 
            if (this.boundaryPos >= 0) {
                // Copy the maximum number of bytes possible into the
                // destination buffer either the number of bytes requested
                // by the caller (len) or all the bytes up to the boundary
                final int remainingBytesInBuffer = this.boundaryPos - this.bufferIndex;
                final int bytesToRead = Math.min(len - numBytesRead, remainingBytesInBuffer);
                
                System.arraycopy(this.buffer, this.bufferIndex, dstBuf, dstOff + numBytesRead, bytesToRead);
                numBytesRead += bytesToRead;
                
                // Move the buffer index to the in there are remaining 
                // bytes left before the boundary
                if (numBytesRead < remainingBytesInBuffer) {
                    this.bufferIndex += bytesToRead;
                
                // Otherwise the boundary has been reached    
                } else {
                    this.boundaryReached = true;
                }
                
            // Only read from the first half of the buffer as the boundary
            // could overlap from the second half into the yet to be read
            // stream    
            } else if (this.bufferIndex >= (this.bufferSize - this.boundary.length)) {             
                // Copy bytes from the second have of the buffer to the first half and update the number
                // of bytes in the buffer
                System.arraycopy(this.buffer, this.bufferIndex, this.buffer, 0, this.numBytesInBuffer - this.bufferIndex);
                this.numBytesInBuffer = this.numBytesInBuffer - this.bufferIndex;
                
                
                // Populate the rest of the buffer
                final int count = this.is.read(this.buffer, this.numBytesInBuffer,  this.bufferSize - this.numBytesInBuffer);
                
                // If there are no more bytes in the buffer mark the input stream as eof
                if (count < 0) {
                    this.eof = true;
                } else  {
                    // Update the size of the buffer and search for the boundary
                    this.numBytesInBuffer += count;
                    this.findBoundary();
                }
                
                this.bufferIndex = 0;
            } else if (this.eof){
                // If we've reached eof copy everything left
                final int bytesToRead = Math.min(len - numBytesRead, this.numBytesInBuffer - this.bufferIndex);
                System.arraycopy(this.buffer, this.bufferIndex, dstBuf, dstOff + numBytesRead, bytesToRead);
                numBytesRead += bytesToRead;
                
                this.bufferIndex += bytesToRead;
  
                if (this.bufferIndex >= this.numBytesInBuffer) {
                    // Garbage collect the buffer
                    this.buffer = null;
                    this.numBytesInBuffer = 0;
                    this.boundaryPos = -1;    
                    break;
                }
            } else {
                // Only copy data from the first half of the buffer as the boundary
                // could overlap from the second half into the yet to be read stream
                final int bytesToRead = Math.min(len - numBytesRead, 
                                            (Math.min(this.bufferSize - this.boundary.length , this.numBytesInBuffer) - this.bufferIndex));
                System.arraycopy(this.buffer, this.bufferIndex, dstBuf, dstOff + numBytesRead, bytesToRead);
                numBytesRead += bytesToRead;
                
                this.bufferIndex += bytesToRead;
            }
        } 
        return numBytesRead;
    }
}
