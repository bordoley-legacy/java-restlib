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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class BoundaryInputStreamTest {
    @Test
    public void testSimpleBoundary() throws IOException {
        final String test = "himom\n\nhoware you\n\ni'm awesome";
        final InputStream is = new ByteArrayInputStream(test.getBytes());
        final byte[] boundary = {'\n','\n'};
        
        StringBuilder builder = new StringBuilder();
        BoundaryInputStream bis = BoundaryInputStream.wrap(is, boundary);
        
        int n;
        while ((n = bis.read()) != -1) {
            builder.append((char) n);
        }
        System.out.println(builder.toString());
        
        builder = new StringBuilder();
        bis = BoundaryInputStream.wrap(bis.getRemainingStream(), boundary);
        while ((n = bis.read()) != -1) {
            builder.append((char) n);
        }
        System.out.println(builder.toString());
        
        builder = new StringBuilder();
        bis = BoundaryInputStream.wrap(bis.getRemainingStream(), boundary);
        while ((n = bis.read()) != -1) {
            builder.append((char) n);
        }
        System.out.println(builder.toString());
        
    }
    
    @Test
    public void testComplexBoundary() throws IOException {
        final String test =
            "-----------------------------128933573517556999151623161625\r\n" +
            "Content-Disposition: form-data; name=\"name\"\r\n" +
            "\r\n" +
            "your name\r\n" +
            "-----------------------------128933573517556999151623161625\r\n" +
            "Content-Disposition: form-data; name=\"mail\"\r\n" +
            "\r\n" +
            "your email\r\n" +
            "-----------------------------128933573517556999151623161625\r\n" +
            "Content-Disposition: form-data; name=\"comment\"; filename=\"\"\r\n" +
            "Content-Type: application/octet-stream\r\n" +
            "\r\n"+
            "\r\n" +
            "-----------------------------128933573517556999151623161625--\r\n";

        final String boundary = "---------------------------128933573517556999151623161625";
        
        final InputStream is = new ByteArrayInputStream(test.getBytes());
        
        final BioMultiPartInput parts = 
            BioMultiPartInput.wrap(is, boundary.getBytes());
        StringBuilder builder = new StringBuilder();            

        for (final BodyPartInputStream part : parts){
            int n;
            while((n = part.read()) != -1){
                builder.append((char) n);
            }
            
            if (builder.length() > 0) {
                System.out.println(builder.toString());
            }
            builder = new StringBuilder();        
        }
        
        //System.out.println(builder.toString());
    }
    
    
}
