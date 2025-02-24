package org.abx.services;

import jakarta.annotation.PostConstruct;
import org.apache.coyote.Request;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;

@Component
public class ServicesClient {
    @Value("${jwt.private}")
    private String privateKey;

    private HttpClient client;

    private HashMap<String, String> services;
    private @Value("${services.config}") String strServices;

    @PostConstruct
    public void setup() throws Exception {
        JSONObject obj = new JSONObject(strServices);
        this.services = new HashMap<>();
        for (String key : obj.keySet()) {
            this.services.put(key, obj.getString(key));
        }
        client = HttpClient.newBuilder().
                connectTimeout(Duration.ofSeconds(20)).
                build();
    }

    public JWTServicesClient withJWT(String token){
        return new JWTServicesClient(this,token);
    }

    public ServiceRequest get(String serviceName, String request) throws Exception {
        return create("GET", serviceName, request);
    }

    public ServiceRequest post(String serviceName, String request) throws Exception {
        return create("POST", serviceName, request);
    }

    public ServiceRequest put(String serviceName, String request) throws Exception {
        return create("PUT", serviceName, request);
    }

    public ServiceRequest patch(String serviceName, String request) throws Exception {
        return create("PATCH", serviceName, request);
    }

    public ServiceRequest delete(String serviceName, String request) throws Exception {
        return create("DELETE", serviceName, request);
    }

    public ServiceRequest create(String method, String serviceName, String request) throws Exception {
        if (!services.containsKey(serviceName)) {
            throw new Exception("Service " + serviceName + " not found");
        }
        String url = services.get(serviceName) + request;
        ServiceRequest req = new ServiceRequest(method, url);
        req.client = this;
        return req;
    }

    public ServiceResponse process(ServiceRequest req) throws Exception {
        HttpResponse<InputStream> res = client.send(req.compile(), HttpResponse.BodyHandlers.ofInputStream());
        return new ServiceResponse(res);
    }
}
