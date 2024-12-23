import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.TextFrame;
import org.abx.ws.frames.WebSocketFrame;
import org.junit.Test;

import java.net.Socket;

public class FrameTest {

    @Test
    public void doTest()throws Exception {
        Socket s = new Socket("localhost",8080);
        TextFrame t =  TextFrame.from("Hello");
        WebSocketFrame.writeFrame(s.getOutputStream(),t);
        WebSocketFrame.writeFrame(s.getOutputStream(), CloseFrame.get());
        s.close();
    }
}
