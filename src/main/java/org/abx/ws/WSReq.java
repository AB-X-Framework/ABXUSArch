package org.abx.ws;

import org.abx.ws.frames.BinaryFrame;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;

public class WSReq {

    public String method;

    public byte[] data;

    public WSReq(String method) {
        this.method = method+"?";
    }

    public void set(String key,String value) {
        method+= URLEncoder.encode(key)+"="+URLEncoder.encode(value);
    }

    public void setBody(ByteArrayInputStream body) {}
    public BinaryFrame getBinaryFrame() {
        return BinaryFrame.from()
    }
}
