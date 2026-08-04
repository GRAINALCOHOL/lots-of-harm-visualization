package grainalcohol.lhv.client.display.manager;

import grainalcohol.lhv.client.display.renderer.ListTextRenderer;
import grainalcohol.lhv.client.display.renderer.WorldTextRenderer;
import grainalcohol.lhv.client.display.renderer.damage.DamageRenderer;
import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.config.GlobalConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RendererManagerImpl implements RendererManager {
    private final Map<SourceType, DamageRenderer> DAMAGES = new HashMap<>();
    private final Map<SourceType, ListTextRenderer> TEXTS = new HashMap<>();
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
            @NotNull SourceType sourceType,
            @NotNull DamageInfo damageInfo
    ) {
        var renderer = DAMAGES.get(sourceType);
        if (renderer == null || renderer.isExpired()) {
            renderer = sourceType.getBasicConfig().createRenderer(sourceType);
            DAMAGES.put(sourceType, renderer);
        }
        renderer.setStatus(damageInfo);

        RENDERER_ORDER.remove(sourceType);
        RENDERER_ORDER.add(sourceType);
    }

    @Override
    public void handleText(@NotNull SourceType sourceType, @NotNull List<StyledText> texts) {
        var renderer = TEXTS.get(sourceType);
        if (renderer == null || renderer.isExpired()) {
            renderer = new ListTextRenderer(sourceType);
            TEXTS.put(sourceType, renderer);
        }
        renderer.setStatus(texts);

        RENDERER_ORDER.remove(sourceType);
        RENDERER_ORDER.add(sourceType);
    }

    @Override
    public Vec3d getLatestWorldPos() {
        return latestWorldPos;
    }

    @Override
    public void render(DrawContext drawContext) {
        for (var st : computeRenderOrder()) {
            renderFor(drawContext, TEXTS.get(st), latestWorldPos, latestYaw, false);
            renderFor(drawContext, DAMAGES.get(st), latestWorldPos, latestYaw, false);
        }
    }

    @Override
    public void render(DrawContext drawContext, Vec3d lerpedPos, float lerpedYaw) {
        for (var st : computeRenderOrder()) {
            this.latestWorldPos = lerpedPos;
            this.latestYaw = lerpedYaw;
            boolean trackEntity = st.getBasicConfig().isTrackEntity();
            renderFor(drawContext, TEXTS.get(st), lerpedPos, lerpedYaw, trackEntity);
            renderFor(drawContext, DAMAGES.get(st), lerpedPos, lerpedYaw, trackEntity);
        }
    }

    private void renderFor(
            @NotNull DrawContext drawContext,
            @Nullable WorldTextRenderer<?> renderer,
            @NotNull Vec3d worldPos, float yaw,
            boolean trackPos
    ) {
        if (renderer == null) return;
        if (renderer.isExpired()) return;

        if (!trackPos) {
            renderer.render(drawContext, getRendererPos(latestWorldPos), getYawDelta(latestYaw));
        } else {
            renderer.render(drawContext, getRendererPos(worldPos), getYawDelta(yaw));
        }
    }

    private List<SourceType> computeRenderOrder() {
        return switch (GlobalConfig.getInstance().sourceSortMode) {
            case LATEST -> new ArrayList<>(RENDERER_ORDER);
            case OLDEST -> {
                var list = new ArrayList<>(RENDERER_ORDER);
                Collections.reverse(list);
                yield list;
            }
        };
    }

    private Vec3d getRendererPos(Vec3d worldPos) {
        return worldPos.add(0, this.verticalOffset, 0);
    }

    private float getYawDelta(float victimYaw) {
        return victimYawOnCreation - victimYaw;
    }

    @Override
    public boolean isExpired() {
        return DAMAGES.values().stream().allMatch(DamageRenderer::isExpired) &&
                TEXTS.values().stream().allMatch(ListTextRenderer::isExpired);
    }
}
