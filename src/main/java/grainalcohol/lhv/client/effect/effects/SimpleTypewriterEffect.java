package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SimpleTypewriterEffect extends BaseEffect {
    private final int speedMs;

    public SimpleTypewriterEffect() {
        this(25);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {

    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        int visible = (int) (this.activeTimeMs() / speedMs) + 1;
        if (setting.index >= visible) setting.alpha = 0;
    }

    @Override
    public boolean isFinished(int textLength) {
        return this.activeTimeMs() > textLength * speedMs;
    }
}
