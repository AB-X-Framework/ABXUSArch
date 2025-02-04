package org.abx.services;

import org.abx.util.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ServiceResponse {
    private final HttpResponse<InputStream> response;
    private byte[] data;
    private boolean processedAsArray;
    private boolean processedAsStream;

    public ServiceResponse(HttpResponse<InputStream> response) {
        this.response = response;
    }

    public int statusCode() {
        return response.statusCode();
    }

    public byte[] asByteArray() throws Exception {
        if (processedAsStream) {
            throw new IllegalStateException("Already processed as stream.");
        }
        if (processedAsArray) {
            return data;
        }
        processedAsArray = true;
        data = StreamUtils.readByteArrayStream(response.body());
        return data;
    }

    public InputStream asStream() throws Exception {
        if (processedAsStream) {
            throw new IllegalStateException("Already processed as stream.");
        }
        if (processedAsArray) {
            return new ByteArrayInputStream(data);
        }
        processedAsStream = true;
        return this.response.body();
    }

    public String asString() throws Exception {
        return new String(asByteArray(), StandardCharsets.UTF_8);
    }

    public int asInt() throws Exception {
        return Integer.parseInt(asString());
    }

    public long asLong() throws Exception {
        return Long.parseLong(asString());
    }

    public boolean asBoolean() throws Exception {
        return Boolean.parseBoolean(asString());
    }

    public JSONObject asJSONObject() throws Exception {
        return new JSONObject(asString());
    }

    public JSONArray asJSONArray() throws Exception {
        return new JSONArray(asString());
    }

    public Map<String, List<String>> headers() {
        return response.headers().map();
    }
}
