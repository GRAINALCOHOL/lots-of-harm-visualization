package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SettleEffect extends BaseEffect {
    private final int durationMs;
    private final int staggerMs;
    private final float startScale;

    public SettleEffect() {
        this(1000, 25, 2.0f);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {}

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float localTime = this.activeTimeMs() - (long) setting.index * staggerMs;
        if (localTime < 0) return;

        float progress = Math.min(1f, localTime / (float) durationMs);
        float factor = (float) Math.pow(1f - progress, 6f);
        float scale = 1f + (startScale - 1f) * factor;

        setting.widthScale *= scale;
        setting.heightScale *= scale;
    }

    @Override
    public boolean isFinished(int textLength) {
        return this.activeTimeMs() > (long) (textLength - 1) * staggerMs + durationMs;
    }
}
