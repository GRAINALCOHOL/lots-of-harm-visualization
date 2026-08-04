package grainalcohol.lhv.common.dto.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class OffsetSetting {
    private final double screenOffsetRangeX;
    private final double screenOffsetRangeY;
    private final double offsetRangeX;
    private final double offsetRangeY;
    private final double offsetRangeZ;

    public OffsetSetting(OffsetSetting other) {
        this.screenOffsetRangeX = other.screenOffsetRangeX;
        this.screenOffsetRangeY = other.screenOffsetRangeY;
        this.offsetRangeX = other.offsetRangeX;
        this.offsetRangeY = other.offsetRangeY;
        this.offsetRangeZ = other.offsetRangeZ;
    }

    /**
     * @param range 随机范围
     * @return 服从二次分布的随机值
     */
    // TODO: 作为可配置内容
    private float normalOffset(double range) {
        double t = Math.random() * 2 - 1;
        double s = Math.signum(t) * (t * t);
        return (float) (s * range / 2);
    }

    @Contract("-> new")
    public @NotNull Vec2f computeScreenOffset() {
        return new Vec2f(
                normalOffset(getScreenOffsetRangeX()),
                normalOffset(getScreenOffsetRangeY())
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

    public OffsetSetting copy() {
        return new OffsetSetting(this);
    }
}
