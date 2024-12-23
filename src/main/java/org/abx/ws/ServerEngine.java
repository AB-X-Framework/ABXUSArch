package org.abx.ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerEngine {
    public void ws() throws Exception {
        ServerSocket  serverSocket = new ServerSocket(8080);
        Socket client = serverSocket.accept();

    }

    public void handle(Socket client){


    }



}
