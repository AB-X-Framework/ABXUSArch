package org.abx.ws.msg;

import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class WSReq extends WSMsg{

    public String method;

    public WSReq(String method) {
        this.method = method + "?";
        putHeader("ID",Math.random() + "");
    }

    public void set(String key, Object value) {
        if (value == null) {
            value = "";
        }
        method += URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" +
                URLEncoder.encode(value + "", StandardCharsets.UTF_8);
    }

    public void setBody(ByteArrayInputStream bodySt) throws IOException {
        body = StreamUtils.readByteArrayStream(bodySt);
    }

    public BinaryFrame toFrame() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (body == null) {
            baos.writeBytes("GET ".getBytes());
        }else {
            baos.writeBytes("POST ".getBytes());
        }
        baos.writeBytes(method.getBytes(StandardCharsets.UTF_8));
        baos.writeBytes(Line);
        for (Map.Entry<String,String> header:headers.entrySet()){
            baos.writeBytes(header.getKey().getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(": ".getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(header.getValue().getBytes(StandardCharsets.UTF_8));
            baos.writeBytes(Line);
        }
        baos.writeBytes(DoubleLine);
        if (body != null) {
            baos.writeBytes(body);
        }
        return BinaryFrame.from(baos.toByteArray());
    }

    public static WSReq fromFrame(BinaryFrame frame) {

    }
}
