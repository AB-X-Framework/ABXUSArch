package org.abx.ws.msg;

import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.Frame;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

public class WSRes {
    private static byte[] line = "\r\n".getBytes();
    private static byte[] entry = "\r\n\r\n".getBytes();

    private byte[] response;
    private HashMap<String,String> headers;

    public WSRes() {
        headers = new HashMap<String,String>();
    }
    public static WSRes fromFrame(Frame frame) throws IOException {
        BinaryFrame binaryFrame = (BinaryFrame) frame;
        WSRes res = new WSRes();
        byte[] data = binaryFrame.getByteArray();
        int firstLine = StreamUtils.indexOf(data, line);
        int index = StreamUtils.indexOf(data, entry,firstLine);
        firstLine +=2;
        String headers = new String(data, firstLine , index-firstLine);


        ByteArrayInputStream in = new
                ByteArrayInputStream(data,index,data.length-index);
        res.response = StreamUtils.readByteArrayStream(in);
        return res;
    }

    private void setHeaders(String headersText) {
        for (String header : headersText.split("\r\n")) {
            int index = header.indexOf(':');
            String key = header.substring(0, index);
            String value = header.substring(index+1);
            headers.put(key,value);
        }
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public byte[] asByteArray() {
        return response;
    }

    public String asString() {
        return new String(response);
    }

    public JSONObject asJSONObject() {
        return new JSONObject(asString());
    }

    public int asInt() {
        return Integer.parseInt(asString());
    }

    public long asLong() {
        return Long.parseLong(asString());
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }
}
