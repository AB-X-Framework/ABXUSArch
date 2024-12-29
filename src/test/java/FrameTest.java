import org.abx.ws.WSClient;
import org.abx.ws.WSClientListener;
import org.abx.ws.WSServer;
import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;
import org.junit.Assert;
import org.junit.Test;

import java.net.Socket;

public class FrameTest implements WSService, WSClientListener {

    private String incommingClient;
    @WSMethod(params = {"a","b"})
    public int add(int a,int b){
        return a+b;
    }

    @Test
    public void doTest()throws Exception {
        String clientName = "clientx";
        int port = 9898;
        WSServer server = new WSServer();
        server.addClientListener(this);
        server.addController("math",this);
        server.start(port);
        System.out.println("Server started");
        WSClient client = new WSClient(clientName,"localhost",port);
        client.addController("multiply",new ClientReq());
        System.out.println("Client started");
        WSRes res = client.process(new WSReq("math/add").set("a",2).set("b",3));
        System.out.println("Method processed");
        Assert.assertEquals(res.asInt(),5);
        WSClient incommingWSClient = server.getClient(clientName);
         res = incommingWSClient.process(new WSReq("multiply/multiply").set("a",0.5).set("b",3));
        Assert.assertEquals(res.asDouble(),1.5,0.1);
        Assert.assertEquals(incommingClient,clientName);


    }
    public void clientConnected(String clientId){
        incommingClient = clientId;
    }

    public void clientDisconnected(String clientId){
        incommingClient = null;
    }

}
