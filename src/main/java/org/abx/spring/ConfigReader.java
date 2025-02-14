package org.abx.spring;

import org.abx.util.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.stream.IntStream;

public class ConfigReader {
    public static String[] checkArgs(String[] args) throws Exception {
        if (args.length != 3) {
            return args;
        }
        if (!"config".equals(args[0])) {
            return args;
        }
        if (!new File(args[1]).isFile()) {
            return args;
        }
        String config = StreamUtils.readStream(new FileInputStream(args[1]));
        JSONObject jsonConfig = new JSONObject(config);
        JSONObject serviceConfig = jsonConfig.getJSONObject("config").getJSONObject(args[2]);
        JSONArray jsonParams = serviceConfig.getJSONArray("params");
        return IntStream.range(0, jsonParams.length())
                .mapToObj(jsonParams::getString)
                .toArray(String[]::new);

    }
}
