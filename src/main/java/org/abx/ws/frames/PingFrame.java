
package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public class PingFrame extends Frame {

    private static final PingFrame INSTANCE = new PingFrame();

    private PingFrame() {
        super(FrameType.Ping);
    }

    public static PingFrame get() {
        return INSTANCE;
    }
}
