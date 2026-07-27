package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.enums.SourceType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class ListRenderer implements DamageRenderer {
    private final SourceType sourceType;
    private final TreeMap<Long, SingleRenderer> RENDERERS;

    public ListRenderer(SourceType sourceType) {
        this.sourceType = sourceType;
        this.RENDERERS = new TreeMap<>();
    }

    @Override
    public void handleDamage(DamageInfo damageInfo) {
        var renderer = new SingleRenderer(sourceType);
        renderer.handleDamage(damageInfo);
        RENDERERS.put(Util.getMeasuringTimeMs(), renderer);
    }

    @Override
    public void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta) {
        var iterator = RENDERERS.values().iterator();
        while (iterator.hasNext()) {
            var renderer = iterator.next();
            if (renderer.isExpired()) {
                iterator.remove();
            } else {
                renderer.render(drawContext, worldPos, yawDelta);
            }
        }
    }

    @Override
    public boolean isExpired() {
        return RENDERERS.isEmpty();
    }
}
