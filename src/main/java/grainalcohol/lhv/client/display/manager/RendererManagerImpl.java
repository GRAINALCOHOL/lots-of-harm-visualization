package grainalcohol.lhv.client.display.manager;

import grainalcohol.lhv.client.display.renderer.DamageRenderer;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.enums.SourceType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class RendererManagerImpl implements RendererManager {
    private final EnumMap<SourceType, DamageRenderer> RENDERERS = new EnumMap<>(SourceType.class);
    private final LinkedHashSet<SourceType> RENDERER_ORDER = new LinkedHashSet<>();

    // per entity
    private final float victimYawOnCreation;
    private final double verticalOffset;
    private Vec3d latestWorldPos;
    private float latestYaw;

    public RendererManagerImpl(
            double verticalOffset,
            float victimYaw,
            Vec3d worldPos
    ) {
        this.victimYawOnCreation = victimYaw;
        this.verticalOffset = verticalOffset;

        this.latestWorldPos = worldPos;
        this.latestYaw = victimYaw;
    }

    @Override
    public void handleDamage(
            SourceType sourceType,
            float victimYaw,
            Vec3d worldPos,
            DamageInfo damageInfo
    ) {
        this.latestWorldPos = worldPos;
        this.latestYaw = victimYaw;

        RENDERERS.computeIfAbsent(
                sourceType, k -> k.getConfig().createRenderer()
        ).handleDamage(damageInfo);

        RENDERER_ORDER.remove(sourceType);
        RENDERER_ORDER.add(sourceType);
    }

    @Override
    public void render(DrawContext drawContext) {
        for (var st : RENDERER_ORDER) {
            var renderer = RENDERERS.get(st);
            if (renderer == null) continue;

            if (renderer.isExpired()) {
                RENDERERS.replace(st, null);
                continue;
            }
            renderer.render(drawContext, getRendererPos(latestWorldPos), getYawDelta(latestYaw));
        }
    }

    @Override
    public void render(DrawContext drawContext, Vec3d lerpedPos, float lerpedYaw) {
        for (var st : RENDERER_ORDER) {
            var renderer = RENDERERS.get(st);
            if (renderer == null) continue;

            if (renderer.isExpired()) {
                RENDERERS.replace(st, null);
                continue;
            }

            if (!st.getConfig().isTrackEntity()) {
                renderer.render(drawContext, getRendererPos(latestWorldPos), getYawDelta(latestYaw));
            } else {
                this.latestWorldPos = lerpedPos;
                this.latestYaw = lerpedYaw;
                renderer.render(drawContext, getRendererPos(lerpedPos), getYawDelta(lerpedYaw));
            }
        }
    }

    private Vec3d getRendererPos(Vec3d worldPos) {
        return worldPos.add(0, this.verticalOffset, 0);
    }

    private float getYawDelta(float victimYaw) {
        return victimYawOnCreation - victimYaw;
    }

    @Override
    public boolean isExpired() {
        return RENDERERS.values().stream()
                .filter(Objects::nonNull)
                .allMatch(DamageRenderer::isExpired);
    }
}
