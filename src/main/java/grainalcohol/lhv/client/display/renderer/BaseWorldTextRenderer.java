package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.common.dto.config.BasicConfig;
import grainalcohol.lhv.common.source.SourceType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public abstract class BaseWorldTextRenderer<S> implements WorldTextRenderer<S> {
    private final BasicConfig basicConfig;
    private final Vec3d worldOffset;

    protected final ScreenTextRenderer screenTextRenderer;

    private boolean initialized;

    public BaseWorldTextRenderer(SourceType sourceType) {
        this.basicConfig = sourceType.getBasicConfig();
        var displayConfig = sourceType.getDisplayConfig();
        this.worldOffset = displayConfig.getOffsetSetting().computeWorldOffset();

        this.screenTextRenderer = new ScreenTextRenderer(displayConfig);

        this.initialized = false;
    }

    /**
     * 标记初始化完成，否则 {@code isExpired()} 会一直返回false，{@code render()} 不会生效
     */
    protected void setInitialized() {
        this.initialized = true;
    }

    @Override
    public void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta) {
        if (!this.initialized) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        if (this.basicConfig.isInRenderRange(worldPos, client.player.getPos())) {
            this.screenTextRenderer.render(drawContext, getRenderPos(worldPos, yawDelta));
        }
    }

    private Vec3d getRenderPos(Vec3d worldPos, float yawDelta) {
        double radians = Math.toRadians(yawDelta);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return worldPos.add(
                worldOffset.x * cos + worldOffset.z * sin,
                worldOffset.y,
                -worldOffset.x * sin + worldOffset.z * cos
        );
    }

    @Override
    public boolean isExpired() {
        if (!initialized) return false;
        return this.screenTextRenderer.textDisplaySlot.isExpired();
    }
}
