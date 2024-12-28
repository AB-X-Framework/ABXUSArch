package org.abx.ws.msg;

import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.Frame;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

public class WSRes extends WSMsg{


    public WSRes() {
        headers = new HashMap<String,String>();
    }

    public static WSRes fromFrame(Frame frame) throws IOException {
        BinaryFrame binaryFrame = (BinaryFrame) frame;
        WSRes res = new WSRes();
        byte[] data = binaryFrame.getByteArray();
        int firstLine = StreamUtils.indexOf(data, Line);
        int index = StreamUtils.indexOf(data, DoubleLine,firstLine);
        firstLine +=2;
        String headers = new String(data, firstLine , index-firstLine);
        res.headers.put(headers, headers);

        ByteArrayInputStream in = new
                ByteArrayInputStream(data,index,data.length-index);
        res.body = StreamUtils.readByteArrayStream(in);
        return res;
    }



    public byte[] asByteArray() {
        return body;
    }

    public String asString() {
        return new String(body);
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
