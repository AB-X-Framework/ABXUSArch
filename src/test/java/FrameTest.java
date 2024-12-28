import org.abx.ws.WSClient;
import org.abx.ws.WSServer;
import org.abx.ws.annotations.WSMethod;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;
import org.junit.Assert;
import org.junit.Test;

import java.net.Socket;

public class FrameTest {

    @WSMethod(params = {"a","b"})
    public int add(int a,int b){
        return a+b;
    }

    @Test
    public void doTest()throws Exception {
        int port = 9898;
        WSServer server = new WSServer();
        server.addController("math",this);
        server.start(port);
        System.out.println("Server started");
        WSClient client = new WSClient("clientx","localhost",port);
        System.out.println("Client started");
        WSRes res = client.process(new WSReq("math/add").set("a",2).set("b",3));
        System.out.println("Method processed");
        Assert.assertEquals(res.asInt(),5);
    }
}
