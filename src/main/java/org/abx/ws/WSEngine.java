package org.abx.ws;

import org.abx.util.ExceptionHandler;
import org.abx.util.Pair;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.Frame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSReq;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class WSEngine {
    protected HashMap<String, Pair<Object,HashMap<String, Method>>> context;
    protected HashMap<String, Semaphore> requests;

    public WSEngine() {
        context = new HashMap<>();
        requests = new HashMap<>();
    }

    public void addController(String name, Object obj) {
        HashMap<String, Method> methods = new HashMap<>();
        for (Method method : obj.getClass().getMethods()) {
            methods.put(method.getName(), method);
        }
        context.put(name, new Pair<>(obj, methods));
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

    private void process(BinaryFrame frame) throws Exception {
        WSReq req = WSReq.fromFrame(frame);
        String method = req.method;
        int classIndex = method.indexOf('/');
        String className = method.substring(0,classIndex);
        int methodIndex = method.indexOf('?',classIndex+1);
        String methodName = method.substring(classIndex+1,methodIndex);
        HashMap<String, String> params = params(method.substring(methodIndex+1));
        Pair<Object,HashMap<String, Method>> obj = context.get(className);
        process(obj.first, obj.second.get(methodName), params);
    }

    private void process(Object obj, Method method, HashMap<String, String> params) throws Exception {
        method.getP
    }

    private HashMap<String, String> params(String params) {
        HashMap<String, String> map = new HashMap<>();
        for (String param : params.split("&")) {
            int idx = param.indexOf('=');
            String key = URLDecoder.decode(param.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(param.substring(idx+1), StandardCharsets.UTF_8);
        }
        return map;
    }
}
