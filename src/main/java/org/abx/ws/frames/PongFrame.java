package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public class PongFrame extends Frame {

    private static final PongFrame INSTANCE = new PongFrame();

    private PongFrame() {
        super(FrameType.Pong);
    }

    public static PongFrame get() {
        return INSTANCE;
    }
}
