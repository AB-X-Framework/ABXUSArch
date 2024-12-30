package org.abx.ws;

import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;

public class ClientReq implements WSService {

    @WSMethod(params = {"a","b"})
    public double multiply(double a, double b) {
        return a * b;
    }

    @WSMethod(params = {"body"})
    public byte[] plusOne(byte[] body){
        for (int i =0; i<body.length; ++i){
            body[i] += 1;
        }
        return body;
    }
}
