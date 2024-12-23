package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public class TextFrame extends Frame {

    private final String text;

    private TextFrame(String text) {
        super(FrameType.Text);
        this.text = text;
    }

    public static TextFrame from(final String text) {
        return new TextFrame(text);
    }

    public String getText() {
        return text;
    }
}
