package org.abx.ws.msg;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WSMsg {
    protected final static String ID = "ID";
    protected final static byte[] OK = "OK".getBytes(StandardCharsets.UTF_8);
    protected final static byte[] Line = "\r\n".getBytes();
    protected final static byte[] DoubleLine = "\r\n\r\n".getBytes();

    protected HashMap<String, String> headers;
    public byte[] body;

    public WSMsg() {
        headers = new HashMap<>();
    }

    public String getID() {
        return headers.get(ID);
    }

    private void setHeaders(String headersText) {
        for (String header : headersText.split("\r\n")) {
            int index = header.indexOf(": ");
            String key = header.substring(0, index);
            String value = header.substring(index + 2);
            headers.put(key, value);
        }
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public static WSMsg fromFrame(BinaryFrame frame) throws IOException {
        byte[] data = frame.getByteArray();
        if (StreamUtils.startsWith(data, OK)) {
            return WSRes.from(data);
        } else {
            return WSReq.from(data);
        }
    }

    protected Pair<String, InputStream> processHeaders(byte[] data) throws IOException {
        int firstLineIndex = StreamUtils.indexOf(data, Line);
        String firstLine = new String(data, 0,firstLineIndex);
        int index = StreamUtils.indexOf(data, DoubleLine, firstLineIndex);
        firstLineIndex += 2;
        String headers = new String(data, firstLineIndex, index - firstLineIndex);
        setHeaders(headers);
        return new Pair<>(firstLine,
                new ByteArrayInputStream(data, index + 4, data.length - (index + 4)));
    }

    protected void processHeaders(ByteArrayOutputStream baos) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            baos.writeBytes(header.getKey().getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(": ".getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(header.getValue().getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(Line);
        }
        baos.writeBytes(Line);

    }

}
