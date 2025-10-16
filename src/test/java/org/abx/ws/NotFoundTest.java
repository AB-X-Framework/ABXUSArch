package org.abx.ws;

import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;
import org.abx.ws.msg.WSReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotFoundTest implements WSService {

    @WSMethod(params = {})
    public void throwError ()throws Exception{
        throw new Exception("Expected error");
    }

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
                client.process(new WSReq("math/throwError").set("a", 2).set("b", 3));
            } catch (Exception e) {
                error = true;
                Assertions.assertTrue( e.getMessage().contains("java.lang.Exception: Expected error"),e.getMessage());
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
