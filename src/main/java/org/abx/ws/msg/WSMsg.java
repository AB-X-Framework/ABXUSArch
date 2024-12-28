package org.abx.ws.msg;

import java.util.HashMap;

public class WSMsg {

    protected static byte[] Line = "\r\n".getBytes();
    protected static byte[] DoubleLine = "\r\n\r\n".getBytes();


    protected HashMap<String,String> headers;
    protected byte[] body;

    public WSMsg() {
        headers = new HashMap<>();
    }

    private void setHeaders(String headersText) {
        for (String header : headersText.split("\r\n")) {
            int index = header.indexOf(':');
            String key = header.substring(0, index);
            String value = header.substring(index+1);
            headers.put(key,value);
        }
    }

    public void putHeader(String key, String value) {
        headers.put(key,value);
    }
    public String getHeader(String key) {
        return headers.get(key);
    }

}
