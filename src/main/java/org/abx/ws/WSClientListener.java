package org.abx.ws;

public interface WSClientListener {

    public void clientConnected(String clientId);

    public void clientDisconnected(String clientId);
}
