package grainalcohol.lhv.common.dto;

import grainalcohol.lhv.client.display.renderer.DamageRenderer;
import grainalcohol.lhv.client.display.renderer.ListRenderer;
import grainalcohol.lhv.client.display.renderer.MergeRenderer;
import grainalcohol.lhv.client.display.renderer.SingleRenderer;
import grainalcohol.lhv.common.enums.DamageSortMode;
import grainalcohol.lhv.common.enums.RenderMode;
import grainalcohol.lhv.common.source.SourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class GeneralConfig {
    private final RenderMode renderMode;
    private final DamageSortMode damageSortMode;
    private final int displayDuration;
    private final boolean trackEntity;
    private final boolean retainWhenOffScreen;
    private final boolean punchyEffectEnable;
    private final double maxReceiveRange;
    private final double minVisibleRange;
    private final double maxVisibleRange;
    private final double screenOffsetRangeX;
    private final double screenOffsetRangeY;
    private final double offsetRangeX;
    private final double offsetRangeY;
    private final double offsetRangeZ;

    public DamageRenderer createRenderer(SourceType sourceType) {
        return switch (renderMode) {
            case MERGE -> new MergeRenderer(sourceType);
            case LATEST -> new SingleRenderer(sourceType);
            case ALL -> new ListRenderer(sourceType);
        };
    }

    /**
     * @param range 随机范围
     * @return 服从二次分布的随机值
     */
    // TODO: 作为可配置内容
    private float shapedOffset(double range) {
        double t = Math.random() * 2 - 1;
        double s = Math.signum(t) * (t * t);
        return (float) (s * range / 2);
    }

    @Contract("-> new")
    public @NotNull Vec2f computeScreenOffset() {
        return new Vec2f(
                shapedOffset(getScreenOffsetRangeX()),
                shapedOffset(getScreenOffsetRangeY())
        );
    }

    @Contract("-> new")
    public @NotNull Vec3d computeWorldOffset() {
        return new Vec3d(
                (Math.random() - 0.5) * getOffsetRangeX(),
                (Math.random() - 0.5) * getOffsetRangeY(),
                (Math.random() - 0.5) * getOffsetRangeZ()
        );
    }

    public boolean isInRenderRange(Vec3d start, Vec3d end) {
        double sqDist = start.squaredDistanceTo(end);
        return sqDist >= this.getMinVisibleRange() * this.getMinVisibleRange() && sqDist <= this.getMaxVisibleRange() * this.getMaxVisibleRange();
    }

    public boolean isInReceiveRange(Vec3d start, Vec3d end) {
        double sqDist = start.squaredDistanceTo(end);
        return sqDist <= this.getMaxReceiveRange() * this.getMaxReceiveRange();
    }
}
