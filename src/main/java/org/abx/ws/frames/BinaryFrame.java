package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public class BinaryFrame extends Frame {

    private final byte[] data;

    private BinaryFrame(byte[] data) {
        super(FrameType.Binary);
        this.data = data;
    }

    public static BinaryFrame from(byte[] data) {
        return new BinaryFrame(data);
    }

    public byte[] getByteArray() {
        return data;
    }
}
