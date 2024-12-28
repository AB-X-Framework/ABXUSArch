package org.abx.ws;

import org.abx.util.ExceptionHandler;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.Frame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSReq;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class WSEngine {
    protected HashMap<String, Object> context;
    protected HashMap<String, Semaphore> requests;

    public WSEngine() {
        context = new HashMap<>();
        requests = new HashMap<>();
    }

    public void addController(String name, Object obj) {
        context.put(name, obj);
    }

    public void handle(Socket client) {
        try {
            while (true) {
                Frame frame = WebSocketFrame.readFrame(client.getInputStream());
                if (frame instanceof CloseFrame) {
                    client.close();
                    return;
                }
                if (frame instanceof BinaryFrame) {
                    new Thread(() -> {
                        try {
                            process((BinaryFrame) frame);
                        } catch (IOException e) {
                            ExceptionHandler.handleException(e);
                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void process(BinaryFrame frame) throws IOException {
        WSReq req = WSReq.fromFrame(frame);
    }
}
