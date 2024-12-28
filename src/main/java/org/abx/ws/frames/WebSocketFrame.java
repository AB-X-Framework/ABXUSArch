
package org.abx.ws.frames;

import org.abx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author luis
 */
public class WebSocketFrame {

    private static final byte FRAME_OPCODE = 127;
    private static final byte FRAME_MASKED = Byte.MIN_VALUE;
    private static final byte FRAME_LENGTH = 127;
    private static final int LARGE_FRAME_LENGTH = 65535;

    private static Pair<FrameType, Boolean> getNextFrameType(InputStream inputStream) throws IOException {
        int firstByte = inputStream.read();
        boolean fin = (firstByte & 128) > 1;
        FrameType ft;
        switch (firstByte & FRAME_OPCODE) {
            case 0x00:
                ft = FrameType.Continuation;
                break;
            case 0x01:
                ft = FrameType.Text;
                break;
            case 0x02:
                ft = FrameType.Binary;
                break;
            case 0x08:
                ft = FrameType.ConnectionClose;
                break;
            case 0x09:
                ft = FrameType.Ping;
                break;
            case 0x0A:
                ft = FrameType.Pong;
                break;
            default:
                ft = FrameType.Unknown;
        }
        return new Pair<>(ft, fin);
    }

    public static Frame readFrame(InputStream inputStream) throws IOException {
        Pair<FrameType, Boolean> frame = getNextFrameType(inputStream);
        switch (frame.first) {
            case Binary:
            case Continuation:
                return BinaryFrame.from(readBinaryFrame(inputStream,frame.second));
            case ConnectionClose:
                return CloseFrame.get();
            default:
                throw new IOException("Unknown frame type: " + frame.first);
        }
    }

    private static int getPayloadSize(InputStream inputStream, int b) throws IOException {
        int payloadLength = (b & FRAME_LENGTH);
        if (payloadLength == 126) {
            payloadLength = ((inputStream.read() & 0xFF) << 8)
                    + (inputStream.read() & 0xFF);
        } else if (payloadLength == 127) {
            // ignore the first 4-bytes. We can't deal with 64-bit ints right now anyways.
            inputStream.read();
            inputStream.read();
            inputStream.read();
            inputStream.read();
            payloadLength = ((inputStream.read() & 0xFF) << 24)
                    + ((inputStream.read() & 0xFF) << 16)
                    + ((inputStream.read() & 0xFF) << 8)
                    + ((inputStream.read() & 0xFF));
        }

        return payloadLength;
    }

    private static String readTextFrame(InputStream inputStream, boolean fin) throws IOException {
        int b = inputStream.read();
        final boolean frameMasked = (b & FRAME_MASKED) != 0;
        int payloadLength = getPayloadSize(inputStream, b);

        final byte[] frameMaskingKey = new byte[4];

        if (frameMasked) {
            inputStream.read(frameMaskingKey);
        }

        final StringBuilder payloadBuffer = new StringBuilder(payloadLength);

        int read = 0;
        if (frameMasked) {
            do {
                payloadBuffer.append(((char) ((inputStream.read() ^ frameMaskingKey[read % 4]) & 127)));
            } while (++read < payloadLength);
        } else {
            // support unmasked frames for testing.

            do {
                payloadBuffer.append((char) inputStream.read());
            } while (++read < payloadLength);
        }

        if (!fin) {
            payloadBuffer.append(new String(((BinaryFrame) readFrame(inputStream)).getByteArray()));
        }
        return payloadBuffer.toString();
    }

    public static byte[] readBinaryFrame(InputStream inputStream, boolean fin) throws IOException {
        int b = inputStream.read();
        final boolean frameMasked = (b & FRAME_MASKED) != 0;
        int payloadLength = getPayloadSize(inputStream, b);

        final byte[] frameMaskingKey = new byte[4];

        if (frameMasked) {
            inputStream.read(frameMaskingKey);
        }

        final byte[] buf = new byte[payloadLength];

        int read = 0;
        if (frameMasked) {
            do {
                buf[read] = (byte) ((inputStream.read() ^ frameMaskingKey[read % 4]));
            } while (++read < payloadLength);
        } else {
            // support unmasked frames for testing.

            do {
                buf[read] = (byte) inputStream.read();
            } while (++read < payloadLength);
        }

        if (!fin) {
            byte[] rest = ((BinaryFrame) readFrame(inputStream)).getByteArray();
            byte[] combined = new byte[buf.length + rest.length];
            System.arraycopy(buf, 0, combined, 0, buf.length);
            System.arraycopy(rest, 0, combined, buf.length, rest.length);
            return combined;
        }
        return buf;
    }

    private static void writeTextFrame(OutputStream outputStream, final String txt) throws IOException {
        final byte[] strBytes = txt.getBytes(StandardCharsets.UTF_8);
        final int len = strBytes.length;
        outputStream.write(0b10000001);
        writePayloadSize(outputStream, len);
        for (byte strByte : strBytes) {
            outputStream.write(strByte);
        }
        outputStream.flush();
    }

    /**
     * Sets the payload size based on the byte2 (byte after the opcode). When
     * byte2 is 127 indicates that the next 8 bytes will define the payload size
     * When byte2 is 126 indicates that the next 2 bytes will define the pauload
     * size When byte2 is 125 or lower, then, byte2 is defining the payload size
     */
    private static void writePayloadSize(OutputStream outputStream, final int payloadSize) throws IOException {
        if (payloadSize > LARGE_FRAME_LENGTH) {
            outputStream.write(127);
            // pad the first 4 bytes of 64-bit context length. If this frame is larger than 2GB, you're in trouble. =)
            outputStream.write(0);
            outputStream.write(0);
            outputStream.write(0);
            outputStream.write(0);
            outputStream.write(((payloadSize >> 24) & 0xFF));
            outputStream.write(((payloadSize >> 16) & 0xFF));
            outputStream.write(((payloadSize >> 8) & 0xFF));
            outputStream.write((payloadSize & 0xFF));
        } else if (payloadSize > 125) {
            outputStream.write(126);
            outputStream.write(((payloadSize >> 8) & 0xFF));
            outputStream.write(((payloadSize) & 0xFF));
        } else {
            outputStream.write((payloadSize & 127));
        }
    }

    public static void writeFrame(OutputStream outputStream, Frame frame) throws IOException {
        switch (frame.getType()) {
            case Binary:
                writeBinaryFrame(outputStream, ((BinaryFrame) frame).getByteArray());
                break;
            case ConnectionClose:
                sendConnectionClose(outputStream);
                break;
            case Ping:
                sendPing(outputStream);
                break;
            case Pong:
                sendPong(outputStream);
                break;
            default:
                throw new IOException("unable to handle frame type: " + frame.getType());
        }
    }

    private static void writeBinaryFrame(OutputStream outputStream, final byte[] data) throws IOException {
        final int len = data.length;
        outputStream.write(-126);
        writePayloadSize(outputStream, len);
        outputStream.write(data);
        outputStream.flush();
    }

    private static void sendPing(OutputStream outputStream) throws IOException {
        outputStream.write(-119);
        outputStream.write(125);
        outputStream.flush();
    }

    private static void sendPong(OutputStream outputStream) throws IOException {
        outputStream.write(-118);
        outputStream.write(125);
        outputStream.flush();
    }

    private static void sendConnectionClose(OutputStream outputStream) throws IOException {
        outputStream.write(-120);
        outputStream.write(125);
        outputStream.flush();
    }
}
