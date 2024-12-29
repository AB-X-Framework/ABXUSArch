package org.abx.ws;

import org.abx.util.ExceptionHandler;
import org.abx.util.Pair;
import org.abx.ws.annotations.WSMethod;
import org.abx.ws.annotations.WSService;
import org.abx.ws.frames.BinaryFrame;
import org.abx.ws.frames.CloseFrame;
import org.abx.ws.frames.Frame;
import org.abx.ws.frames.WebSocketFrame;
import org.abx.ws.msg.WSMsg;
import org.abx.ws.msg.WSReq;
import org.abx.ws.msg.WSRes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class WSEngine {
    protected final HashMap<String, Pair<WSService, HashMap<String, Method>>> context;
    protected final HashMap<String, Semaphore> requests;
    protected final HashMap<String, WSRes> responses;
    private final WSServer server;

    public WSEngine() {
        context = new HashMap<>();
        requests = new HashMap<>();
        responses = new HashMap<>();
        server = null;
    }

    public WSEngine(WSServer server) {
        context = new HashMap<>();
        requests = new HashMap<>();
        responses = new HashMap<>();
        this.server = server;
    }

    public void addController(String name, WSService obj) {
        HashMap<String, Method> methods = new HashMap<>();
        for (Method method : obj.getClass().getMethods()) {
            methods.put(method.getName(), method);
        }
        context.put(name, new Pair<>(obj, methods));
    }

    public void handle(Socket client) {
        new Thread(() -> {
            try {
                OutputStream out = client.getOutputStream();
                while (true) {
                    Frame frame = WebSocketFrame.readFrame(client.getInputStream());
                    if (frame instanceof CloseFrame) {
                        client.close();
                        return;
                    }
                    if (frame instanceof BinaryFrame) {
                        new Thread(() -> {
                            try {
                                process((BinaryFrame) frame, out);
                            } catch (Exception e) {
                                ExceptionHandler.handleException(e);
                            }
                        }).start();
                    }
                }
            } catch (IOException e) {
                ExceptionHandler.handleException(e);
            }
        }).start();
    }

    private void process(BinaryFrame frame, OutputStream out) throws Exception {
        WSMsg msg = WSMsg.fromFrame(frame);
        if (msg instanceof WSReq) {
            process((WSReq) msg, out);
        } else {
            process((WSRes) msg);
        }

    }

    private void process(WSRes req) throws Exception {
        String id = req.getID();
        responses.put(id, req);
        Semaphore semaphore = requests.remove(id);
        if (semaphore == null) {
            System.out.println(new String(req.body));
        }
        semaphore.release();
    }

    protected void process(WSReq req, OutputStream out) throws Exception {
        if (server == null) {
            String method = req.method;
            int classIndex = method.indexOf('/');
            String className = method.substring(0, classIndex);
            int methodIndex = method.indexOf('?', classIndex + 1);
            String methodName = method.substring(classIndex + 1, methodIndex);
            HashMap<String, Object> params = params(method.substring(methodIndex + 1));
            params.put("body", req.body);
            Pair<WSService, HashMap<String, Method>> obj = context.get(className);
            Object result = process(obj.first, obj.second.get(methodName), params);
            WSRes res = req.createRes();
            if (result == null) {
                res.body = new byte[0];
            } else if (result instanceof byte[]) {
                res.body = (byte[]) result;
            } else {
                res.body = result.toString().getBytes(StandardCharsets.UTF_8);
            }
            WebSocketFrame.writeFrame(out, res.toFrame());
        } else {
            server.process(req, out);
        }
    }


    private Object process(Object obj, Method method,
                           HashMap<String, Object> params) throws Exception {
        WSMethod paramsAnnotation = method.getAnnotation(WSMethod.class);
        String[] paramNames = paramsAnnotation.params();
        int paramsLength = paramNames.length;
        Object[] newParams = new Object[paramsLength];
        for (int i = 0; i < paramsLength; i++) {
            newParams[i] = params.get(paramNames[i]);
        }
        Object[] castedParams = castParams(newParams, method.getParameterTypes(),
                method.getName(), paramNames);
        return method.invoke(obj, castedParams);

    }

    private Object[] castParams(Object[] objs, Class[] paramTypes, String methodName,
                                String[] paramNames) throws Exception {
        for (int i = 0; i < objs.length; i++) {
            objs[i] = castParameter(objs[i], paramTypes[i], methodName, paramNames[i]);
        }
        return objs;
    }

    /**
     * Cast one parameter
     *
     * @param arg
     * @param paramType
     * @return
     * @throws Exception
     */
    private Object castParameter(Object arg, Class paramType, String methodName, String paramName)
            throws Exception {
        if (isPrimitive(paramType)) {
            if (arg == null) {
                throw new Exception(
                        "The method " + methodName + " expects " + paramName + " but was not provided");
            }
            Class obj = castPrimitiveIntance(paramType);
            Method m = obj.getMethod("valueOf", String.class);
            return m.invoke(obj, arg);
        } else {
            if (JSONArray.class == paramType) {
                return new JSONArray(arg);
            }
            if (JSONObject.class == paramType) {
                return new JSONObject(arg);
            }
            return arg;
        }
    }

    /**
     * Return wether the class is primitive
     *
     * @param c
     * @return
     */
    private static boolean isPrimitive(Class c) {
        if (Integer.class == c || Long.class == c || Double.class == c ||
                Character.class == c || Short.class == c || Byte.class == c ||
                Float.class == c || Boolean.class == c || c.isEnum()) {
            return true;
        }
        return c.isPrimitive();
    }

    /**
     * Return primitives
     *
     * @param c
     * @return
     */
    private static Class castPrimitiveIntance(Class c) {
        if (int.class == c) {
            return Integer.class;
        }
        if (long.class == c) {
            return Long.class;
        }
        if (double.class == c) {
            return Double.class;
        }
        if (char.class == c) {
            return Character.class;
        }
        if (short.class == c) {
            return Short.class;
        }
        if (byte.class == c) {
            return Byte.class;
        }
        if (float.class == c) {
            return Float.class;
        }
        if (boolean.class == c) {
            return Boolean.class;
        }
        return c;//Integer and other classes
    }

    private HashMap<String, Object> params(String params) {
        HashMap<String, Object> map = new HashMap<>();
        if (params.isBlank()) {
            return map;
        }
        for (String param : params.split("&")) {
            int idx = param.indexOf('=');
            String key = URLDecoder.decode(param.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(param.substring(idx + 1), StandardCharsets.UTF_8);
            map.put(key, value);
        }
        return map;
    }
}
