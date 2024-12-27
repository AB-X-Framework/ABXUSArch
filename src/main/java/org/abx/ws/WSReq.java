package org.abx.ws;

import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WSReq {

    public String method;

    public byte[] data;

    public WSReq(String method) {
        this.method = method + "?";
    }

    public void set(String key, Object value) {
        if (value == null) {
            value = "";
        }
        method += URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" +
                URLEncoder.encode(value + "", StandardCharsets.UTF_8);
    }

    public void setBody(ByteArrayInputStream body) throws IOException {
        data = StreamUtils.readByteArrayStream(body);
    }

    public BinaryFrame getBinaryFrame() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (data == null) {
            baos.writeBytes("GET ".getBytes());
        }else {
            baos.writeBytes("POST ".getBytes());
        }
        baos.writeBytes(method.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes("\r\n\n".getBytes(StandardCharsets.UTF_8));
        if (data != null) {
            baos.writeBytes(data);
        }
        return BinaryFrame.from(baos.toByteArray());
    }
}
