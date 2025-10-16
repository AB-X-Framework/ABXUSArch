package org.abx.services;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;


public class NetworkClient {
    
    private HttpClient client;
  
    public NetworkClient() {
        resetCookies();
    }

    public void resetCookies(){
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL); //
        client = HttpClient.newBuilder().
                connectTimeout(Duration.ofSeconds(20)).
                cookieHandler(cookieManager).
                build();

    }

    public ServiceRequest get( String request) throws Exception {
        return create("GET",  request);
    }

    public ServiceRequest post( String request) throws Exception {
        return create("POST",  request);
    }

    public ServiceRequest put( String request) throws Exception {
        return create("PUT",  request);
    }

    public ServiceRequest patch( String request) throws Exception {
        return create("PATCH",  request);
    }

    public ServiceRequest delete( String request) throws Exception {
        return create("DELETE",  request);
    }

    public ServiceRequest create(String method,  String request) throws Exception {
        ServiceRequest req = new ServiceRequest(method, request);
        req.client = this.client;
        return req;
    }

}
