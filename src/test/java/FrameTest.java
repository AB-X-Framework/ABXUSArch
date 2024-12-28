import org.abx.ws.WSServer;
import org.abx.ws.annotations.WSMethod;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.WebSocketFrame;
import org.junit.Test;

import java.net.Socket;

public class FrameTest {

    @WSMethod(params = {"a","b"})
    public int add(int a,int b){
        return a+b;
    }

    @Test
    public void doTest()throws Exception {
        WSServer server = new WSServer();
        server.addController("math",this);
    }
}
