package org.abx.ws.msg;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.abx.ws.frames.BinaryFrame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WSReq extends WSMsg{

    public String method;

    public WSReq(String method) {
        this.method = method + "?";
        putHeader(ID,Math.random() + "");
    }

    protected WSReq(){

    }

    public WSRes createRes(){
        return new WSRes(getHeader(ID));
    }

    public WSReq set(String key, Object value) {
        if (value == null) {
            value = "";
        }
        method += URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" +
                URLEncoder.encode(value + "", StandardCharsets.UTF_8)+"&";
        return this;
    }

    public WSReq setBody(ByteArrayInputStream bodySt) throws IOException {
        body = StreamUtils.readByteArrayStream(bodySt);
        return this;
    }

    public WSReq setBody(byte[] bodySt) {
        body = bodySt;
        return this;
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
        processHeaders(baos);
        if (body != null) {
            baos.writeBytes(body);
        }
        return BinaryFrame.from(baos.toByteArray());
    }

    public static WSReq from(byte[] frame)throws IOException{
        WSReq req = new WSReq();
        Pair<String, InputStream> processed = req.processHeaders(frame);
        String firstLine = processed.first;
        int space = firstLine.indexOf(' ');
        req.method = firstLine.substring(0, space);
        if (firstLine.startsWith("POST")) {
            req.body = StreamUtils.readByteArrayStream(processed.second);
            //POST
            req.method = firstLine.substring(5);
        }else {
            //GET
            req.method = firstLine.substring(4);
        }
        return req;
    }
}
