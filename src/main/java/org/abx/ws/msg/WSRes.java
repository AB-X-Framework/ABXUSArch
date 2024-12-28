package org.abx.ws.msg;

import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.Frame;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WSRes {

    private static byte[] entry = "\r\n\r\n".getBytes();

    private byte[] response;
    public static WSRes fromFrame(Frame frame) throws IOException {
        BinaryFrame binaryFrame = (BinaryFrame) frame;
        WSRes res = new WSRes();
        byte[] data = binaryFrame.getByteArray();;
        int index = StreamUtils.indexOf(data, entry);

        ByteArrayInputStream in = new
                ByteArrayInputStream(data,index,data.length-index);
        res.response = StreamUtils.readByteArrayStream(in);
        return res;
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
