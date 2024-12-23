package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public abstract class Frame {

    private final FrameType type;

    protected Frame(final FrameType type) {
        this.type = type;
    }

    public FrameType getType() {
        return type;
    }
}
