package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class FadeInEffect extends BaseEffect {
    private final int durationMs;
    private final int staggerMs;

    public FadeInEffect() {
        this(200, 50);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {}

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float localTime = this.activeTimeMs() - (long) setting.index * staggerMs;
        if (localTime < 0) return;
        float progress = Math.min(1f, localTime / (float) durationMs);
        setting.alpha *= progress;
    }

    @Override
    public boolean isFinished(int textLength) {
        return this.activeTimeMs() > (long) (textLength - 1) * staggerMs + durationMs;
    }

    @Override
    public int getHeadMs(int textLength) {
        return (textLength - 1) * staggerMs + durationMs;
    }
}
