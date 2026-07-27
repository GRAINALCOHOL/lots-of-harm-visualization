package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class FlashInEffect extends BaseEffect {
    private final int durationMs;

    public FlashInEffect() {
        this(500);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {

    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float elapsed = this.activeTimeMs();
        if (elapsed > durationMs) return;

        float inv = 1f - elapsed / (float) durationMs;
        if (setting.colorField != null) setting.colorField.blend(0xFFFFFF, inv);
    }

    @Override
    public boolean isFinished(int textLength) {
        return this.activeTimeMs() > durationMs;
    }
}
