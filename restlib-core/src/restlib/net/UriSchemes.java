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

/**
 * Contains constant definitions for the iana assigned permanent URI schemes.
 * @see <a href="http://www.iana.org/assignments/uri-schemes.html">http://www.iana.org/assignments/uri-schemes.html</>
 *
 */
public final class UriSchemes {
    /**
     * Diameter Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3588.txt">RFC3588</a>
     */
    public static final String AAA = "aaa";
    
    /**
     * Diameter Protocol with secure transport
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3588.txt">RFC3588</a>
     */
    public static final String AAAS = "aaas";
    
    /**
     * Application configuration access protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2244.txt">RFC2244</a>
     */
    public static final String ACAP = "acap";
    
    /**
     * Calendar Access Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4324.txt">RFC4324</a>
     */
    public static final String CAP = "cap";
    
    /**
     * Content identifier
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2392.txt">RFC2392</a>
     */
    public static final String CID = "cid";
    
    /**
     * TV-Anytime Content Reference Identifier
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4078.txt">RFC4078</a>
     */
    public static final String CRID = "crid";
    
    /**
     * Data
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2397.txt">RFC2397</a>
     */
    public static final String DATA = "data";
    
    /**
     * dav
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4918.txt">RFC4918</a>
     */
    public static final String DAV = "dav";
    
    /**
     * Dictionary service protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2229.txt">RFC2229</a>
     */
    public static final String DICT = "dict";
    
    /**
     * Domain Name System
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4501.txt">RFC4501</a>
     */
    public static final String DNS = "dns";    
    
    /**
     * Host-specific file names
     * @see <a href="http://www.rfc-editor.org/rfc/rfc1738.txt">RFC1738</a>
     */
    public static final String FILE = "file";
    
    /**
     * File Transfer Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc1738.txt">RFC1738</a>
     */
    public static final String FTP = "ftp";
    
    /**
     * Geographic Locations
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5870.txt">RFC5870</a>
     */
    public static final String GEO = "geo";
    
    /**
     * Go
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3368.txt">RFC3368</a>
     */
    public static final String GO = "go";
    
    /**
     * Gopher
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4266.txt">RFC4266</a>
     */
    public static final String GOPHER = "gopher";
    
    /**
     * h323
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3508.txt">RFC3508</a>
     */
    public static final String H323 = "h323";
    
    /**
     * Hypertext Transfer Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2616.txt">RfC2616</a>
     */
    public static final String HTTP = "http";
    
    /**
     * Hypertext Transfer Protocol Secure
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2616.txt">RfC2818</a>
     */
    public static final String HTTPS = "https";
    
    /**
     * Inter-Asterisk eXchange Version 2
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5456.txt">RFC5456</a>
     */
    public static final String IAX = "iax";
    
    /**
     * Internet Content Adaptation Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3507.txt">RFC3507</a>
     */
    public static final String ICAP = "icap";
    
    /**
     * Instant Messaging
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3860.txt">RFC3860</a>
     */
    public static final String IM = "im";
    
    /**
     * Internet message access protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5092.txt">RFC5092</a>
     */
    public static final String IMAP = "imap";
    
    /**
     * Information Assets with Identifiers in Public Namespaces
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4452.txt">RFC4452</a>
     */
    public static final String INFO = "info";
    
    /**
     * Internet Printing Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3510.txt">RFC3510</a>
     */
    public static final String IPP = "ipp";
    
    /**
     * Internet Registry Information Service
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3981.txt">RFC3981</a>
     */
    public static final String IRIS = "iris";
    
    /**
     * iris.beep
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3983.txt">RFC3983</a>
     */
    public static final String IRIS_BEEP = "iris.beep";

    /**
     * iris.xpc
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4992.txt">RFC4992</a>
     */
    public static final String IRIS_XPC = "iris.xpc";
    
    /**
     * iris.xpcs
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4992.txt">RFC4992</a>
     */
    public static final String IRIS_XPCS = "iris.xpcs";

    /**
     * iris.lwz
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4993.txt">RFC4993</a>
     */
    public static final String IRIS_LWZ = "iris.lwz";
    
    /**
     * Lightweight Directory Access Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4516.txt">RFC4516</a>
     */
    public static final String LDAP = "ldap";
    
    /**
     * Electronic mail address
     * @see <a href="http://www.rfc-editor.org/rfc/rfc6068.txt">RFC6068</a>
     */
    public static final String MAIL_TO = "mailto";    
    
    /**
     * message identifier
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2392.txt">RFC2392</a>
     */
    public static final String MID = "mid";   
    
    /**
     * Message Session Relay Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4975.txt">RFC4975</a>
     */
    public static final String MSRP= "msrp";
    
    /**
     * Message Session Relay Protocol Secure
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4975.txt">RFC4975</a>
     */
    public static final String MSRPS = "msrps";
    
    /**
     * Message Tracking Query Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3887.txt">RFC3887</a>
     */
    public static final String MTQP = "mtqp";
    
    /**
     * Mailbox Update (MUPDATE) Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3656.txt">RFC3656</a>
     */
    public static final String MUPDATE = "mupdate";
    
    /**
     * USENET news
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5538.txt">RFC5538</a>
     */
    public static final String NEWS = "news";
    
    /**
     * network file system protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2224.txt">RFC2224</a>
     */
    public static final String NFS = "nfs";

    /**
     * USENET news using NNTP access
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5538.txt">RFC5538</a>
     */
    public static final String NNTP = "nntp";
    
    /**
     * opaquelocktoken
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4918.txt">RFC4918</a>
     */
    public static final String OPAQUE_LOCK_TOKEN = "opaquelocktoken";
    
    /**
     * Post Office Protocol v3
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2384.txt">RFC2384</a>
     */
    public static final String POP = "pop";
    
    /**
     * Presence
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3859.txt">RFC3859</a>
     */
    public static final String PRES = "pres";

    /**
     * real time streaming protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2326.txt">RFC2326</a>
     */
    public static final String RTSP = "rtsp";
    
    /**
     * service location
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2609.txt">RFC2609</a>
     */
    public static final String SERVICE = "service";
    
    /**
     * Secure Hypertext Transfer Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2660.txt">RFC2660</a>
     */
    public static final String SHTTP = "shttp";

    /**
     * ManageSieve Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5804.txt">RFC5804</a>
     */
    public static final String SIEVE = "sieve";
    
    /**
     * session initiation protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3261.txt">RFC3261</a>
     */
    public static final String SIP = "sip";
    
    
    /**
     * secure session initiation protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3261.txt">RFC3261</a>
     */
    public static final String SIPS = "sips";
    
    /**
     * Short Message Service
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5724.txt">RFC5724</a>
     */
    public static final String SMS = "sms";

    /**
     * Simple Network Management Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4088.txt">RFC4088</a>
     */
    public static final String SNMP = "snmp";
    
    /**
     * soap.beep
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4227.txt">RFC4227</a>
     */
    public static final String SOAP_BEEP= "soap.beep";
    
    /**
     * soap.beeps
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4227.txt">RFC4227</a>
     */
    public static final String SOAP_BEEPS = "soap.beeps";
    
    /**
     * tag 
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4151.txt">RFC4151</a>
     */
    public static final String TAG = "tag";
    
    /**
     * telephone 
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3966.txt">RFC3966</a>
     */
    public static final String TEL = "tel";
    
    /**
     * Reference to interactive sessions 
     * @see <a href="http://www.rfc-editor.org/rfc/rfc4248.txt">RFC4248</a>
     */
    public static final String TELNET = "telnet";
    
    /**
     * Trivial File Transfer Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3617.txt">RFC3617</a>
     */
    public static final String TFTP = "tftp";
    
    /**
     * multipart/related relative reference resolution
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2557.txt">RFC2557</a>
     */
    public static final String THISMESSAGE= "thismessage";
    
    /**
     * Interactive 3270 emulation sessions
     * @see <a href="http://www.rfc-editor.org/rfc/rfc6270.txt">RFC6270</a>
     */
    public static final String TN3270 = "tn3270";
    
    /**
     * Transaction Internet Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2371.txt">RFC2371</a>
     */
    public static final String TIP = "tip";
    
    /**
     * TV Broadcasts
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2838.txt">RFC2838</a>
     */
    public static final String TV = "tv";
    
    /**
     * Uniform Resource Names
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2141.txt">RFC2141</a>
     */
    public static final String URN = "urn";
    
    /**
     * versatile multimedia interface
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2122.txt">RFC2122</a>
     */
    public static final String VEMMI = "vemmi";
    
    /**
     * WebSocket connections
     * @see <a href="http://www.rfc-editor.org/rfc/rfc6455.txt">RFC6455</a>
     */
    public static final String WS = "ws";
    
    /**
     * Encrypted WebSocket connections
     * @see <a href="http://www.rfc-editor.org/rfc/rfc6455.txt">RFC6455</a>
     */
    public static final String WSS = "wss";
    
    /**
     * xcon
     * @see <a href="http://tools.ietf.org/html/draft-ietf-xcon-common-data-model-32">xcon-common-data-model-32</a>
     */
    public static final String XCON = "xcon";
    
    /**
     * xcon-userid
     * @see <a href="http://tools.ietf.org/html/draft-ietf-xcon-common-data-model-32">xcon-common-data-model-32</a>
     */
    public static final String XCON_USERID = "xcon-userid";
    
    /**
     * xmlrpc.beep
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3529.txt">RFC3529</a>
     */
    public static final String XMLRPC_BEEP = "xmlrpc.beep";
    
    /**
     * xmlrpc.beeps 
     * @see <a href="http://www.rfc-editor.org/rfc/rfc3529.txt">RFC3529</a>
     */
    public static final String XMLRPC_BEEPS = "xmlrpc.beeps";
    
    /**
     * Extensible Messaging and Presence Protocol
     * @see <a href="http://www.rfc-editor.org/rfc/rfc5122.txt">RFC5122</a>
     */
    public static final String XMPP = "xmpp";
    
    /**
     * Z39.50 Retrieval
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2056.txt">RFC2056</a>
     */
    public static final String Z39_50R = "z39.50r";
    
    /**
     * Z39.50 Session
     * @see <a href="http://www.rfc-editor.org/rfc/rfc2056.txt">RFC2056</a>
     */
    public static final String Z39_50S = "z39.50s";
    
    private UriSchemes(){
    }
}
