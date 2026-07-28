package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class PulseEffect extends BaseEffect {
    private final float peak;
    private final float upRatio;
    private final int durationMs;
    private final int staggerMs;

    public PulseEffect() {
        this(1.6f, 0.1f, 100, 25);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {

    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float charDelay = setting.index * staggerMs;
        float localTime = this.activeTimeMs() - charDelay;

        if (localTime < 0 || localTime > durationMs) return;

        float progress = localTime / durationMs;
        float scale;
        if (progress < upRatio) {
            float t = progress / upRatio;
            scale = setting.widthScale + (peak - setting.widthScale) * t;
        } else {
            float t = (progress - upRatio) / (1f - upRatio);
            scale = peak + (setting.widthScale - peak) * t;
        }
        setting.widthScale = scale;
        setting.heightScale = scale;
    }

    @Override
    public boolean isFinished(int textLength) {
        int totalDuration = durationMs + (textLength - 1) * staggerMs;
        return this.activeTimeMs() > totalDuration;
    }
}
