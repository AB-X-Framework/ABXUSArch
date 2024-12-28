package org.abx.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    public static String readResource(String resource) throws IOException {
        return readStream(StreamUtils.class.getClassLoader().getResourceAsStream(resource));
    }


    public static byte[] readByteArrayResource(String resource) throws IOException {
        return readByteArrayStream(StreamUtils.class.getClassLoader().getResourceAsStream(resource));
    }

    public static byte[] readByteArrayStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int size;
        while ((size = is.read(data)) >= 0) {
            baos.write(data, 0, size);
        }
        is.close();
        return baos.toByteArray();
    }

    public static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] data = new byte[1024];

        int size;
        while ((size = is.read(data)) >= 0) {
            sb.append(new String(data, 0, size, "UTF-8"));
        }
        is.close();
        return sb.toString();
    }

    public static int indexOf(byte[] source, byte[] target) {
        if (source == null || target == null || target.length == 0) {
            return -1;
        }
        int sourceLength = source.length;
        int targetLength = target.length;
        // If target is longer than source, it's not possible to find
        if (targetLength > sourceLength) {
            return -1;
        }
        for (int i = 0; i <= sourceLength - targetLength; i++) {
            int j;
            for (j = 0; j < targetLength; j++) {
                if (source[i + j] != target[j]) {
                    break;
                }
            }
            // If the loop completed, we found a match
            if (j == targetLength) {
                return i;
            }
        }
        return -1; // No match found
    }

}