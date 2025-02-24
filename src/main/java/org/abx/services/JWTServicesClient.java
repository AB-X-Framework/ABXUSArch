package org.abx.services;

public class JWTServicesClient {

    private final String token;
    private final ServicesClient client;

    public JWTServicesClient(ServicesClient client, String token) {
        this.client = client;
        this.token = token;
    }

    public ServiceRequest get(String serviceName, String request) throws Exception {
        return client.get(serviceName, request).jwt(token);
    }

    public ServiceRequest post(String serviceName, String request) throws Exception {
        return client.post(serviceName, request).jwt(token);
    }

    public ServiceRequest put(String serviceName, String request) throws Exception {
        return client.put(serviceName, request).jwt(token);
    }

    public ServiceRequest patch(String serviceName, String request) throws Exception {
        return client.patch(serviceName, request).jwt(token);
    }

    public ServiceRequest delete(String serviceName, String request) throws Exception {
        return client.delete(serviceName, request).jwt(token);
    }

    public ServiceRequest create(String method, String serviceName, String request) throws Exception {
        return client.create(method,serviceName,request).jwt(token);
    }
}
