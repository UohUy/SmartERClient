package monash.smarterclient;

import java.net.InetAddress;

public class LocalHostIPAddress {
    public LocalHostIPAddress(){}

    public String getIPAddress() throws Exception{
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
    }
}
