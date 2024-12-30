import org.abx.ws.WSClient;
import org.abx.ws.WSClientListener;
import org.abx.ws.WSServer;
import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FrameTest implements WSService, WSClientListener {

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
        System.out.println("Server started");
        WSClient client = new WSClient(clientName, "localhost", port);
        client.addController("multiply", new ClientReq());
        System.out.println("Client started");
        WSRes res = client.process(new WSReq("math/add").set("a", 2).set("b", 3));
        System.out.println("Method processed");
        Assertions.assertEquals(5, res.asInt());
        WSClient incommingWSClient = server.getClient(clientName);
        res = incommingWSClient.process(new WSReq("multiply/multiply").set("a", 0.5).set("b", 3));
        Assertions.assertEquals(1.5, res.asDouble(), 0.001);
        Assertions.assertEquals(clientName, incommingClient);
        client.disconnect();
        System.out.println("Client disconnected");
        Assertions.assertEquals(clientName, disconnectedClient);
    }

    public void clientConnected(String clientId) {
        incommingClient = clientId;
    }

    public void clientDisconnected(String clientId) {
        disconnectedClient = clientId;
    }

}
