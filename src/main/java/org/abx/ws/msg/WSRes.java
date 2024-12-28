package org.abx.ws.msg;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.Frame;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class WSRes extends WSMsg{

    public WSRes(String id) {
        putHeader(ID,id);
    }
    protected WSRes(){

    }
    public static WSRes from(BinaryFrame frame) throws IOException {
        WSRes res = new WSRes();
        Pair<String, InputStream> processed = res.processHeaders(frame);
        res.body = StreamUtils.readByteArrayStream(processed.second);
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