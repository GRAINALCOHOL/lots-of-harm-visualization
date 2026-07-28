package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.enums.SourceType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListRenderer implements DamageRenderer {
    private final SourceType sourceType;
    private final ArrayDeque<SingleRenderer> rendererPool;

    public ListRenderer(SourceType sourceType) {
        this.sourceType = sourceType;
        this.rendererPool = new ArrayDeque<>();
    }

    @Override
    public void handleDamage(DamageInfo damageInfo) {
        var renderer = new SingleRenderer(sourceType);
        renderer.handleDamage(damageInfo);
        rendererPool.add(renderer);
    }

    @Override
    public void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta) {
        List<SingleRenderer> renderList = computeRenderList(sourceType);
        if (renderList.isEmpty()) return;

        for (var renderer : renderList) {
            renderer.render(drawContext, worldPos, yawDelta);
        }
    }

    private List<SingleRenderer> computeRenderList(SourceType sourceType) {
        while (!rendererPool.isEmpty() && rendererPool.peekFirst().isExpired()) {
            rendererPool.pollFirst();
        }

        if (rendererPool.isEmpty()) return List.of();

        List<SingleRenderer> result = new ArrayList<>(rendererPool);
        return switch (sourceType.getConfig().getDamageSortMode()) {
            case LATEST -> result;
            case OLDEST -> {
                Collections.reverse(result);
                yield result;
            }
        };
    }

    @Override
    public boolean isExpired() {
        return rendererPool.isEmpty();
    }
}
