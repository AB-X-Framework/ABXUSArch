package org.abx.ws;

import org.abx.util.ExceptionHandler;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.Frame;
import org.abx.ws.frames.WebSocketFrame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerEngine {
    public void ws() throws Exception {
        ServerSocket  serverSocket = new ServerSocket(8080);
        Socket client = serverSocket.accept();
        new Thread(() -> {
            try{
                handle(client);
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }).start();
    }

    public void handle(Socket client) throws IOException {
        while (true) {
            Frame f =  WebSocketFrame.readFrame(client.getInputStream());
            if (f instanceof CloseFrame){
                client.close();
                return;
            }
            if (f instanceof BinaryFrame) {
                System.out.println("WebSocketFrame " + ((BinaryFrame)f).getByteArray()
                );
            }
        }

    }



}
