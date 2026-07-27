package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

public class ShrinkEffect extends BaseEffect {
    private final int durationMs;
    private final int staggerMs;
    private long shrinkStartMs = -1;

    public ShrinkEffect(int durationMs, int staggerMs) {
        this.durationMs = durationMs;
        this.staggerMs = staggerMs;
    }

    public ShrinkEffect() {
        this(100, 50);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {

    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        int totalWindow = (ctx.textLength - 1) * staggerMs + durationMs;

        if (ctx.remainingMs > totalWindow) {
            shrinkStartMs = -1;
            return;
        }

        if (shrinkStartMs < 0) shrinkStartMs = this.activeTimeMs();

        float elapsed = this.activeTimeMs() - shrinkStartMs;
        float charElapsed = elapsed - setting.index * staggerMs;
        if (charElapsed < 0) return;

        float progress = Math.min(1f, charElapsed / (float) durationMs);
        float factor = 1f - progress;
        setting.widthScale *= factor;
        setting.heightScale *= factor;
    }

    @Override
    public boolean isFinished(int textLength) {
        int totalWindow = (textLength - 1) * staggerMs + durationMs;
        return shrinkStartMs >= 0 && (this.activeTimeMs() - shrinkStartMs) > totalWindow;
    }

    @Override
    public int getTailMs(int textLength) {
        return (textLength - 1) * staggerMs + durationMs;
    }

    @Override
    public void reset() {
        super.reset();
        this.shrinkStartMs = -1;
    }
}
