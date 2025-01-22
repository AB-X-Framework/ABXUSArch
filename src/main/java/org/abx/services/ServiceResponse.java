package org.abx.services;

import java.io.UnsupportedEncodingException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ServiceResponse {
    private final HttpResponse<byte[]> response;

    public ServiceResponse(HttpResponse<byte[]> response) {
        this.response = response;
    }

    public int statusCode() {
        return response.statusCode();
    }

    public byte[] asByteArray() {
        return response.body();
    }

    public String asString() throws UnsupportedEncodingException {
        return new String(response.body(), StandardCharsets.UTF_8);
    }
    public boolean asBoolean() throws UnsupportedEncodingException {
        return Boolean.parseBoolean( new String(response.body(), StandardCharsets.UTF_8));
    }


    public Map<String, List<String>> headers() {
        return response.headers().map();
    }
}
