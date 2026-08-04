package grainalcohol.lhv.common.dto.config;

import grainalcohol.lhv.client.display.renderer.damage.DamageRenderer;
import grainalcohol.lhv.client.display.renderer.damage.ListRenderer;
import grainalcohol.lhv.client.display.renderer.damage.MergeRenderer;
import grainalcohol.lhv.client.display.renderer.damage.SingleRenderer;
import grainalcohol.lhv.common.enums.DamageSortMode;
import grainalcohol.lhv.common.enums.RenderMode;
import grainalcohol.lhv.common.source.SourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;

@Getter
@AllArgsConstructor
public class BasicConfig {
    private final RenderMode renderMode;
    private final DamageSortMode damageSortMode;
    private final boolean trackEntity;
    private final double maxReceiveRange;
    private final double minVisibleRange;
    private final double maxVisibleRange;

    private BasicConfig(BasicConfig other) {
        this.renderMode = other.renderMode;
        this.damageSortMode = other.damageSortMode;
        this.trackEntity = other.trackEntity;
        this.maxReceiveRange = other.maxReceiveRange;
        this.minVisibleRange = other.minVisibleRange;
        this.maxVisibleRange = other.maxVisibleRange;
    }

    public DamageRenderer createRenderer(SourceType sourceType) {
        return switch (renderMode) {
            case MERGE -> new MergeRenderer(sourceType);
            case LATEST -> new SingleRenderer(sourceType);
            case ALL -> new ListRenderer(sourceType);
        };
    }

    public boolean isInRenderRange(Vec3d start, Vec3d end) {
        double sqDist = start.squaredDistanceTo(end);
        return sqDist >= this.getMinVisibleRange() * this.getMinVisibleRange() && sqDist <= this.getMaxVisibleRange() * this.getMaxVisibleRange();
    }

    public boolean isInReceiveRange(Vec3d start, Vec3d end) {
        double sqDist = start.squaredDistanceTo(end);
        return sqDist <= this.getMaxReceiveRange() * this.getMaxReceiveRange();
    }

    public BasicConfig copy() {
        return new BasicConfig(this);
    }
}
