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
    private final float plateauRadius;
    private final Direction direction;
    private final boolean loop;
    private final int sweepColor;

    public SweepEffect() {
        this(600, 6.0f, 0.5f, Direction.RIGHT, false, 0xFFFFFF);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {}

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float progress = MathHelper.clamp(this.activeTimeMs() / (float) durationMs, 0.0f, 1.0f);
        float halfWidth = bandWidth / 2.0f;
        float sweepDistance = ctx.textLength + bandWidth;

        float bandCenter = direction == Direction.RIGHT
                ? progress * sweepDistance - halfWidth
                : ctx.textLength + halfWidth - progress * sweepDistance;

        float distance = Math.abs(setting.index - bandCenter);
        float effectivePlateau = Math.min(plateauRadius, halfWidth);
        float raw = 1.0f - MathHelper.clamp(
                Math.max(0, distance - effectivePlateau) / Math.max(halfWidth - effectivePlateau, 0.001f),
                0.0f, 1.0f
        );
        float brightness = raw * raw * (3.0f - 2.0f * raw);

        if (brightness > 0.001f && setting.colorField != null) {
            setting.colorField.blend(sweepColor, brightness);
        }
    }

    @Override
    public boolean isFinished(int textLength) {
        return !loop && this.activeTimeMs() > durationMs;
    }
}
