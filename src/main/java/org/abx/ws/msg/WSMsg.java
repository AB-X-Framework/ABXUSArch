package org.abx.ws.msg;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.Frame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WSMsg {
    protected final static String ID = "ID";
    protected final static byte[] Line = "\r\n".getBytes();
    protected final static byte[] DoubleLine = "\r\n\r\n".getBytes();

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

    protected Pair<String, InputStream> processHeaders(BinaryFrame frame) throws IOException {
        byte[] data = frame.getByteArray();
        int firstLine = StreamUtils.indexOf(data, Line);
        int index = StreamUtils.indexOf(data, DoubleLine,firstLine);
        firstLine +=2;
        String headers = new String(data, firstLine , index-firstLine);
        setHeaders(headers);
        return new Pair<>(new String(data,0,firstLine),
                new ByteArrayInputStream(data,index+4,data.length-(index+4)));
    }

    protected void processHeaders(ByteArrayOutputStream baos) {
        for (Map.Entry<String,String> header:headers.entrySet()){
            baos.writeBytes(header.getKey().getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(": ".getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(header.getValue().getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(Line);
        }
        baos.writeBytes(DoubleLine);

    }

}
