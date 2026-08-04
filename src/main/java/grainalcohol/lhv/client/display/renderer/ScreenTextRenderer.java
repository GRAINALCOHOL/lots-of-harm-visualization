package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.client.wrapper.TextDisplaySlot;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.dto.ScreenPosition;
import grainalcohol.lhv.common.util.ScreenUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScreenTextRenderer {
    private final DisplayConfig displayConfig;
    private final Vec2f screenOffset;

    @Nullable
    private ScreenPosition latestScreenPos;
    private boolean hasBeenOnScreen;
    public final TextDisplaySlot textDisplaySlot;

    public ScreenTextRenderer(DisplayConfig displayConfig) {
        this.displayConfig = displayConfig;
        this.screenOffset = displayConfig.getOffsetSetting().computeScreenOffset();

        this.textDisplaySlot = TextDisplaySlot.empty(
                displayConfig.getDisplayDuration() * 50,
                displayConfig.getEffectTemplate().onCreated(),
                displayConfig.getEffectTemplate().onChanged()
        );
        // TODO: 配置项
        this.textDisplaySlot.multiplyWidth(0.9f);
        this.textDisplaySlot.outline(
                displayConfig.getOutlineSetting().isEnabled()
        );
    }

    public void setText(StyledText text) {
        this.textDisplaySlot.setText(text);
    }

    public void render(@NotNull DrawContext drawContext, Vec3d renderPos) {
        this.updateWorldPos(renderPos);
        this.textDisplaySlot.render(drawContext);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateWorldPos(@NotNull Vec3d worldPos) {
        ScreenPosition screenPosition = ScreenUtil.worldToScreen(worldPos);

        if (screenPosition != null) {
            screenPosition = screenPosition.offset(this.screenOffset);
            this.latestScreenPos = screenPosition;
            this.hasBeenOnScreen = true;
            this.textDisplaySlot.setScreenPosition(screenPosition);
            this.textDisplaySlot.setScale(screenPosition.depthToScale(
                    this.displayConfig.getDepthToScaleRef(),
                    this.displayConfig.getMinScale(),
                    this.displayConfig.getMaxScale()
            ));
            this.textDisplaySlot.setAlpha(screenPosition.depthToAlpha(
                    this.displayConfig.getDepthToAlphaRef(),
                    this.displayConfig.getMinAlpha(),
                    this.displayConfig.getMaxAlpha()
            ));
            // TODO: 转移配置项
        } else if (hasBeenOnScreen && displayConfig.isRetainWhenOffScreen()) {
            this.textDisplaySlot.setScreenPosition(latestScreenPos);
            this.textDisplaySlot.setScale(latestScreenPos.depthToScale(
                    this.displayConfig.getDepthToScaleRef(),
                    this.displayConfig.getMinScale(),
                    this.displayConfig.getMaxScale()
            ) * 0.6f);
            this.textDisplaySlot.setAlpha(latestScreenPos.depthToAlpha(
                    this.displayConfig.getDepthToAlphaRef(),
                    this.displayConfig.getMinAlpha(),
                    this.displayConfig.getMaxAlpha()
            ));
        } else {
            this.textDisplaySlot.setAlpha(0f);
        }
    }
}
