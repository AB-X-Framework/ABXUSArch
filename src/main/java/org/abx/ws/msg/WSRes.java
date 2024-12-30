package org.abx.ws.msg;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.Frame;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

public class WSRes extends WSMsg {

    private byte[] status;

    public WSRes(String id) {
        putHeader(ID, id);
        status = OK;
    }

    protected WSRes() {
        status = OK;
    }

    public void setNotFound(){
        status = NotFound;
    }
    public WSRes setBody(byte[] bodySt) {
        body = bodySt;
        return this;
    }

    public boolean isNotFound() {
        return Arrays.equals(status, NotFound);
    }

    public String getStatus() {
        return new String(status);
    }

    public BinaryFrame toFrame() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(status);
        baos.write(Line);
        processHeaders(baos);
        if (body != null) {
            baos.write(body);
        }
        return BinaryFrame.from(baos.toByteArray());
    }

    public static WSRes from(byte[] data) throws IOException {
        WSRes res = new WSRes();
        Pair<String, InputStream> processed = res.processHeaders(data);
        res.status = processed.first.getBytes();
        res.body = StreamUtils.readByteArrayStream(processed.second);
        return res;
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

    public double asDouble() {
        return Double.parseDouble(asString());
    }

    public long asLong() {
        return Long.parseLong(asString());
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }
}