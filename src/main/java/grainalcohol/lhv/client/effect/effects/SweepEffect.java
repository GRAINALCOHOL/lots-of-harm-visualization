package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SweepEffect extends BaseEffect {
    public enum Direction { LEFT, RIGHT }

    private final int durationMs;
    private final float bandWidth;
    private final Direction direction;
    private final boolean loop;
    private final int sweepColor;

    public SweepEffect() {
        // 0xFFD970
        this(100, 4.0f, Direction.RIGHT, false, 0xFFFFFF);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {

    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float progress = this.activeTimeMs() / (float) durationMs;
        float halfWidth = bandWidth / 2.0f;

        float bandCenter = direction == Direction.RIGHT
                ? progress
                : ctx.textLength - progress;

        float distance = Math.abs(setting.index - bandCenter);
        float brightness = 1.0f - MathHelper.clamp(
                distance / Math.max(halfWidth, 0.001f), 0.0f, 1.0f
        );
        brightness = brightness * brightness * (3.0f - 2.0f * brightness);

        if (brightness > 0.001f && setting.colorField != null) {
            setting.colorField.blend(sweepColor, brightness);
        }
    }

    @Override
    public boolean isFinished(int textLength) {
        float totalDuration = (textLength + bandWidth / 2.0f) * durationMs;
        return !loop && this.activeTimeMs() > totalDuration;
    }
}
