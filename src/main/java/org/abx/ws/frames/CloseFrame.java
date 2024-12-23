package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public class CloseFrame extends Frame {

    private static final CloseFrame INSTANCE = new CloseFrame();

    private CloseFrame() {
        super(FrameType.ConnectionClose);
    }

    public static CloseFrame get() {
        return INSTANCE;
    }
}
