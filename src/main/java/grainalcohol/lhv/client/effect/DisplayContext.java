package grainalcohol.lhv.client.effect;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DisplayContext {
    public final float screenX;
    public final float screenY;
    public final float textWidth;
    public final float textHeight;
    public final long totalLifetimeMs;
    public final long elapsedMs;
    public final long remainingMs;
    public final int textLength;

    public DisplayContext(
            float screenX,
            float screenY,
            float textWidth,
            float textHeight,
            long totalLifetimeMs,
            long elapsedMs,
            int textLength
    ) {
        this(screenX, screenY, textWidth, textHeight, totalLifetimeMs, elapsedMs, totalLifetimeMs - elapsedMs, textLength);
    }
}
