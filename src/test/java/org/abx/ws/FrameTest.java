package org.abx.ws;

import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FrameTest implements WSService, WSClientListener {

    private boolean clientAdded;
    private String incommingClient;
    private String disconnectedClient;

    @WSMethod(params = {"a", "b"})
    public int add(int a, int b) {
        return a + b;
    }


    @Test
    public void doTest() throws Exception {
        String clientName = "clientx";
        int port = 9898;
        WSServer server = new WSServer();
        server.addClientListener(this);
        server.addController("math", this);
        server.start(port);
        try {
            System.out.println("Server started");
            WSClient client = new WSClient(clientName, "localhost", port);
            client.addController("multiply", new ClientReq());
            System.out.println("Client started");
            WSRes res = client.process(new WSReq("math/add").set("a", 2).set("b", 3));
            System.out.println("Method processed");
            Assertions.assertEquals(5, res.asInt());
            while (!clientAdded){
                Thread.yield();
            }
            WSClient incommingWSClient = server.getClient(clientName);
            res = incommingWSClient.process(new WSReq("multiply/multiply").set("a", 0.5).set("b", 3));
            Assertions.assertEquals(1.5, res.asDouble(), 0.001);
            Assertions.assertEquals(clientName, incommingClient);
            byte[] data = incommingWSClient.process(new WSReq("multiply/plusOne").setBody(bytes())).getBody();
            Assertions.assertArrayEquals(new byte[]{1, 2, 3}, data);
            client.disconnect();
            System.out.println("Client disconnected");
            Assertions.assertEquals(clientName, disconnectedClient);
        } finally {
            server.stop();
        }
    }

    public void clientConnected(String clientId) {
        clientAdded=true;
        incommingClient = clientId;
    }

    public void clientDisconnected(String clientId) {
        disconnectedClient = clientId;
    }

    private byte[] bytes() {
        return new byte[]{0, 1, 2};
    }
}
