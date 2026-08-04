package grainalcohol.lhv.client.effect.effects;

import grainalcohol.lhv.client.effect.BaseEffect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.DisplayContext;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SpringEffect extends BaseEffect {
    /**
     * 振幅强度，增大以强化，缩小以减弱（峰值额外缩放 = 1 + amplitude）
     */
    private final float amplitude;
    /**
     * 效果总时长（毫秒），最后一个字符也在该时长内完成弹跳
     */
    private final int durationMs;
    /**
     * 弹跳次数，上-下往复计一次（π 相位），可为小数
     */
    private final float bounces;
    /**
     * 衰减强度（无量纲），越大则振幅与频率在时长内衰减越剧烈，
     * 结束时振幅约为起始的 e^(-decay)
     */
    private final float decay;
    /**
     * 每字符的错峰延迟（毫秒）
     */
    private final int staggerMs;

    public SpringEffect() {
        this(1.4f, 1000, 6f, 2f, 25);
    }

    @Override
    public void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx) {}

    @Override
    public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
        float charDelay = setting.index * staggerMs;
        float localTime = this.activeTimeMs() - charDelay;

        if (localTime < 0) return;

        float u = MathHelper.clamp(localTime / durationMs, 0f, 1f);

        // 归一化 chirp 相位：瞬时频率按 e^(-decay·u) 递减，结束时相位恰好走完 bounces·π
        float norm = decay > 0.01f ? (1f - (float) Math.exp(-decay)) : 1f;
        float phaseNorm = decay > 0.01f
                ? (1f - (float) Math.exp(-decay * u)) / norm
                : u;
        float phase = (float) (bounces * Math.PI) * phaseNorm;

        // 包络：u=1 时精确归零，scale 回到 1，无弹跳
        float envelope = (amplitude - 1) * (1f - u) * (float) Math.exp(-decay * u);

        // 90° 相位差
        setting.heightScale *= MathHelper.clamp(1 + envelope * (float) Math.cos(phase), 0.3f, amplitude);
        setting.widthScale *= MathHelper.clamp(1 + envelope * (float) Math.sin(phase), 0.3f, amplitude);
    }

    @Override
    public boolean isFinished(int textLength) {
        long lastCharTime = this.activeTimeMs() - (long) (textLength - 1) * staggerMs;
        return lastCharTime >= durationMs;
    }

    // [OLD] 旧物理模型实现（构造参数为 amplitude/omega/damping/staggerMs，数值量级 0.001~0.01 不直观，
    // 时长由振幅阈值隐式决定约 680ms）：
    // private static final float AMPLITUDE_THRESHOLD = 0.01f;
    // private final float amplitude;
    // private final float omega;
    // private final float damping;
    // private final int staggerMs;
    // public SpringEffect() {
    //     this(0.6f, 0.06f, 0.006f, 25);
    // }
    //
    // @Override
    // public void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting) {
    //     float charDelay = setting.index * staggerMs;
    //     float localTime = this.activeTimeMs() - charDelay;
    //
    //     if (localTime < 0) return;
    //
    //     float envelope = amplitude * (float) Math.exp(-damping * localTime);
    //
    //     float phase = (omega / damping) * (1f - (float) Math.exp(-damping * localTime));
    //
    //     float heightOffset = envelope * (float) Math.cos(phase);
    //     float heightScale = Math.max(0.3f, Math.min(1 + amplitude, 1 + heightOffset));
    //
    //     // 45°相位差
    //     int widthDelayMs = (int) (Math.PI / 4 / omega);
    //     float widthLocalTime = localTime - widthDelayMs;
    //     float widthEnvelope = amplitude * (float) Math.exp(-damping * Math.max(0, widthLocalTime));
    //     float widthPhase = (omega / damping) * (1f - (float) Math.exp(-damping * Math.max(0, widthLocalTime)));
    //     float widthOffset = widthEnvelope * (float) Math.cos(widthPhase - (float) Math.PI / 2);
    //     float widthScale = Math.max(0.3f, Math.min(1 + amplitude, 1 + widthOffset));
    //
    //     setting.widthScale *= widthScale;
    //     setting.heightScale *= heightScale;
    // }
    //
    // @Override
    // public boolean isFinished(int textLength) {
    //     float lastCharTime = this.activeTimeMs() - (long) (textLength - 1) * staggerMs;
    //     if (lastCharTime < 0) return false;
    //     float envelope = amplitude * (float) Math.exp(-damping * lastCharTime);
    //     return envelope < AMPLITUDE_THRESHOLD;
    // }
}
