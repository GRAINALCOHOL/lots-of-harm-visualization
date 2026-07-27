package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BounceEffect extends BaseEffect {
    private final float peak;
    private final int duration;

    public BounceEffect() {
        this(1.1f, 200);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {
        float elapsed = this.activeTimeMs();
        if (elapsed > duration) return;

        float progress = elapsed / (float) duration;
        float scale = 1f + (peak - 1f) * (float) Math.sin(Math.PI * progress);

        var matrices = drawContext.getMatrices();
        matrices.translate(ctx.screenX, ctx.screenY, 0);
        matrices.scale(scale, scale, 1f);
        matrices.translate(-ctx.screenX, -ctx.screenY, 0);
    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {

    }

    @Override
    public boolean isFinished(int textLength) {
        return this.activeTimeMs() > duration;
    }
}
