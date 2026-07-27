package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SpringEffect extends BaseEffect {
    private static final float AMPLITUDE_THRESHOLD = 0.01f;

    /**
     * 振幅强度，增大以强化，缩小以减弱
     */
    private final float amplitude;
    /**
     * 震动频率，增大以加快，缩小以减缓
     */
    private final float omega;
    /**
     * 阻尼系数，增大以加速消退，减小以减缓消退
     */
    private final float damping;
    private final int staggerMs;

    public SpringEffect() {
        this(1.4f, 0.03f, 0.01f, 50);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {

    }

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float charDelay = setting.index * staggerMs;
        float localTime = this.activeTimeMs() - charDelay;

        if (localTime < 0) return;

        float envelope = amplitude * (float) Math.exp(-damping * localTime);

        float heightOffset = envelope * (float) Math.cos(omega * localTime);
        float heightScale = Math.max(0.3f, Math.min(1 + amplitude, 1 + heightOffset));

        // 90°相位差
        int widthDelayMs = (int) (Math.PI / 2 / omega);
        float widthLocalTime = localTime - widthDelayMs;
        float widthEnvelope = amplitude * (float) Math.exp(-damping * Math.max(0, widthLocalTime));
        float widthOffset = widthEnvelope * (float) Math.cos(omega * widthLocalTime);
        float widthScale = Math.max(0.3f, Math.min(1 + amplitude, 1 + widthOffset));

        setting.widthScale *= widthScale;
        setting.heightScale *= heightScale;
    }

    @Override
    public boolean isFinished(int textLength) {
        float lastCharTime = this.activeTimeMs() - (long) (textLength - 1) * staggerMs;
        if (lastCharTime < 0) return false;
        float envelope = amplitude * (float) Math.exp(-damping * lastCharTime);
        return envelope < AMPLITUDE_THRESHOLD;
    }
}
