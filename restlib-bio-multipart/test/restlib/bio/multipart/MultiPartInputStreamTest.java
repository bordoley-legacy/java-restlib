package restlib.bio.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public final class MultiPartInputStreamTest {
    private static final String TEST = 
            "--AaB03x\r\n" +
            "Content-Disposition: form-data; name=\"submit-name\"\r\n\r\n" +
            "1234567890123" +
            "\r\n--AaB03x\r\n" +
            "Content-Disposition: form-data; name=\"files\"; filename=\"file1.txt\"\r\n" +
            "Content-Type: text/plain\r\n\r\n" +
            "File Contents" +
            "\r\n--AaB03x--";
    
    private static final byte[] BOUNDARY = "AaB03x".getBytes(Charsets.US_ASCII);
    
    @Test
    public void test() throws IOException {
        final InputStream is =
                new ByteArrayInputStream(
                        TEST.getBytes(Charsets.US_ASCII));
        final BioMultiPartInput mis = BioMultiPartInput.wrap(is, BOUNDARY);
        mis.getPreamble();
        for (final BodyPartInputStream bpis : mis) {
            System.out.println(bpis.headers());
           
            StringBuilder sb = new StringBuilder();
            for (int i = bpis.read(); i != -1; i = bpis.read()) {
                System.out.print((char)i);
                sb.append((char) i);
            }
            //System.out.println(sb.toString());
        }
        
        System.out.println();
        
        InputStream ep = mis.getEpilogue();
        StringBuilder sb = new StringBuilder();
        for (int i = ep.read(); i != -1; i = ep.read()) {
            System.out.print((char)i);

        }
        
    }
}
