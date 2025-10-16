package org.abx.services;

import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceRequest {

    protected HttpClient client;
    private MultiPartBodyPublisher mbp;
    private byte[] body;
    private final String method;
    private final String uri;
    private final HashMap<String, ArrayList<String>> headers;

    public ServiceResponse process(ServiceRequest req) throws Exception {
        HttpResponse<InputStream> res = client.send(req.compile(), HttpResponse.BodyHandlers.ofInputStream());
        return new ServiceResponse(res);
    }

    public ServiceRequest(String method, String uri) {
        this.method = method;
        this.uri = uri;
        this.headers = new HashMap<>();
    }

    public ServiceRequest jwt(String token) {
        return addHeader("Authorization", "Bearer " + token);
    }

    public ServiceRequest addHeader(String name, String value) {
        if (!headers.containsKey(name)) {
            headers.put(name, new ArrayList<>());
        }
        headers.get(name).add(value);
        return this;
    }

    public ServiceRequest setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public ServiceRequest addPart(String key, File filename) throws Exception {
        addPart(key, new FileInputStream(filename), filename.getName(), MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return this;
    }

    public ServiceRequest addPart(String key, byte[] data, String filename) {
        addPart(key, new ByteArrayInputStream(data), filename, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return this;
    }

    public ServiceRequest addPart(String key, InputStream data, String filename) {
        addPart(key, data, filename, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return this;
    }

    public ServiceRequest addPart(String key, InputStream data, String filename, String contentType) {
        if (mbp == null) {
            mbp = new MultiPartBodyPublisher();
        }
        mbp.addStream(key, data, filename, contentType);
        return this;
    }

    public ServiceRequest addPart(String key, String value) {
        if (mbp == null) {
            mbp = new MultiPartBodyPublisher();
        }
        mbp.addPart(key, value);
        return this;
    }

    public HttpRequest compile() throws URISyntaxException {
        HttpRequest.Builder builder;
        switch (method) {
            case "GET": {
                builder = HttpRequest.newBuilder().GET();
                break;
            }
            case "DELETE": {
                builder = HttpRequest.newBuilder().DELETE();
                break;
            }
            case "POST": {
                if (body != null) {
                    builder = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofByteArray(body));
                } else {
                    builder = HttpRequest.newBuilder().POST(mbp.build()).setHeader("Content-Type",
                            "multipart/form-data; boundary=" + mbp.getBoundary());
                }
                break;
            }
            case "PUT": {
                if (body != null) {
                    builder = HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofByteArray(body));
                } else {
                    builder = HttpRequest.newBuilder().PUT(mbp.build()).setHeader("Content-Type",
                            "multipart/form-data; boundary=" + mbp.getBoundary());
                }
                break;
            }
            case "PATCH": {
                if (body != null) {
                    builder = HttpRequest.newBuilder().method("PATCH", HttpRequest.BodyPublishers.ofByteArray(body));
                } else {
                    builder = HttpRequest.newBuilder().method("PATCH", mbp.build()).setHeader("Content-Type",
                            "multipart/form-data; boundary=" + mbp.getBoundary());
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        for (Map.Entry<String, ArrayList<String>> header : headers.entrySet()) {
            for (String value : header.getValue()) {
                builder.header(header.getKey(), value);
            }
        }
        return builder.uri(new URI(uri)).build();
    }
}
