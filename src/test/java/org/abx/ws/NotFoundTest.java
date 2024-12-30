package org.abx.ws;

import org.abx.ws.annotations.WSService;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotFoundTest implements WSService {

    @Test
    public void doTest() throws Exception {
        String clientName = "clientx";
        int port = 9898;
        WSServer server = new WSServer();
        server.addController("math", this);
        server.start(port);
        try {
            System.out.println("Server started");
            WSClient client = new WSClient(clientName, "localhost", port);
            client.addController("multiply", new ClientReq());
            System.out.println("Client started");
            boolean error = false;
            try {
                client.process(new WSReq("math/add").set("a", 2).set("b", 3));
            } catch (Exception e) {
                error = true;
                Assertions.assertEquals("Method not found add", e.getMessage());
            }
            Assertions.assertTrue(error);
            System.out.println("Method processed");

            error = false;
            try {
                client.process(new WSReq("mathNo/add").set("a", 2).set("b", 3));
            } catch (Exception e) {
                error = true;
                Assertions.assertEquals("Class not found mathNo", e.getMessage());
            }
            Assertions.assertTrue(error);
            System.out.println("Method processed");
        } finally {
            server.stop();
        }

    }
}
