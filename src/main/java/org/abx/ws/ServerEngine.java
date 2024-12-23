package org.abx.ws;

import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.Frame;
import org.abx.ws.frames.TextFrame;
import org.abx.ws.frames.WebSocketFrame;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerEngine {
    public void ws() throws Exception {
        ServerSocket  serverSocket = new ServerSocket(8080);
        Socket client = serverSocket.accept();
        handle(client);
    }

    public void handle(Socket client) throws IOException {

        while (true) {
            Frame f =  WebSocketFrame.readFrame(client.getInputStream());
            if (f instanceof CloseFrame){
                client.close();
                return;
            }
            if (f instanceof TextFrame) {
                System.out.println("WebSocketFrame " + ((TextFrame)f).getText()
                );
            }
        }

    }



}
