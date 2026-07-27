package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.LHVModClient;
import grainalcohol.lhv.client.display.func.DamageHandler;
import grainalcohol.lhv.client.effect.effects.*;
import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.client.wrapper.TextDisplaySlot;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.dto.ScreenPosition;
import grainalcohol.lhv.common.enums.SourceType;
import grainalcohol.lhv.common.format.DamageFormatter;
import grainalcohol.lhv.common.util.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseDamageRenderer implements DamageRenderer {
    // render setting
    private final SourceType sourceType;
    private final Vec2f screenOffset;
    private final Vec3d worldOffset;

    // render status
    @Nullable
    private ScreenPosition latestScreenPos;
    private boolean hasBeenOnScreen;
    private boolean initialized;

    // text
    protected final TextDisplaySlot mainSlot;
    protected final TextDisplaySlot subSlot;

    // damage
    private final DamageFormatter formatter;
    protected double damageAmount;
    protected boolean isCritical;

    // effect
    @NotNull
    private final BounceEffect bounceEffect = new BounceEffect();
    @NotNull
    private final SweepEffect sweepEffect = new SweepEffect();

    protected BaseDamageRenderer(SourceType sourceType) {
        this.sourceType = sourceType;
        this.formatter = sourceType.getConfig().getFormatMode().createFormatter();
        this.screenOffset = LHVModClient.computeScreenOffset(sourceType.getConfig());
        this.worldOffset = LHVModClient.computeWorldOffset(sourceType.getConfig());

        this.mainSlot = TextDisplaySlot.empty(
                sourceType.getConfig().getDisplayDuration() * 50,
                true, 0.72f, 0.8f,
                textDisplay -> {
                    textDisplay
                            .addEffect(new FadeInEffect())
                            .addEffect(new FlashInEffect())
                            .addEffect(new ShrinkEffect())
                            .addEffect(new SimpleTypewriterEffect())
                            .addEffect(sweepEffect);
                    if (sourceType.getConfig().isPunchyEffectEnable()) {
                        textDisplay.addEffect(new SpringEffect());
                    } else {
                        textDisplay.addEffect(new SettleEffect());
                    }
                },
                textDisplay -> {
                    textDisplay.addEffect(bounceEffect);
                    if (bounceEffect.isFinished(computeTextLength())) bounceEffect.restart();
                    if (sweepEffect.isFinished(computeTextLength())) sweepEffect.restart();
                }
        );
        this.subSlot = TextDisplaySlot.empty(
                sourceType.getConfig().getDisplayDuration() * 50,
                true, 0.72f, 0.8f,
                textDisplay -> textDisplay
                        .addEffect(new FadeInEffect())
                        .addEffect(new FlashInEffect())
                        .addEffect(new SpringEffect())
                        .addEffect(new PulseEffect())
                        .addEffect(new ShrinkEffect())
                        .addEffect(new SimpleTypewriterEffect())
                        .addEffect(new SweepEffect()),
                null,
                -16, 0.8f
        );

        this.hasBeenOnScreen = false;
        this.initialized = false;
    }

    @Override
    public void handleDamage(DamageInfo damageInfo) {
        getHandler().accept(damageInfo.getDamageAmount(), damageInfo.isCritical());

        this.mainSlot.setText(getStyledDamage(damageInfo.getDamageColor()));
        if (damageInfo.getSubText() != null) {
            this.subSlot.clear();
            this.subSlot.setText(damageInfo.getSubText());
        }

        this.initialized = true;
    }

    abstract DamageHandler getHandler();

    @Override
    public void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta) {
        if (!initialized) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        this.updateWorldPos(worldPos, yawDelta);
        if (sourceType.getConfig().isInRenderRange(worldPos, client.player.getPos())) {
            this.mainSlot.render(drawContext);
            this.subSlot.render(drawContext);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void updateWorldPos(@NotNull Vec3d worldPos, float yawDelta) {
        if (!initialized) return;

        ScreenPosition screenPosition = ScreenUtil.worldToScreen(getRenderPos(worldPos, yawDelta));

        var config = sourceType.getConfig();

        if (screenPosition != null) {
            latestScreenPos = screenPosition;
            hasBeenOnScreen = true;
            this.setScreenPos(screenPosition);
            this.setScale(screenPosition.depthToScale(
                    config.getDepthToScaleRef(), config.getMinScale(), config.getMaxScale()
            ));
            this.setAlpha(screenPosition.depthToAlpha(
                    config.getDepthToAlphaRef(), config.getMinAlpha(), config.getMaxAlpha()
            ));
        } else if (hasBeenOnScreen && sourceType.getConfig().isRetainWhenOffScreen()) {
            this.setScreenPos(latestScreenPos);
            this.setScale(latestScreenPos.depthToScale(
                    config.getDepthToScaleRef(), config.getMinScale(), config.getMaxScale()
            ), 0.6f);
            this.mainSlot.setAlpha(latestScreenPos.depthToAlpha(
                    config.getDepthToAlphaRef(), config.getMinAlpha(), config.getMaxAlpha()
            ));
            this.subSlot.setAlpha(0f);
        } else {
            this.setInvisible();
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

    private String getFormattedDamage() {
        return (isCritical ? sourceType.getConfig().getCriticalFormat() : "%s").formatted(formatter.format(sourceType, damageAmount));
    }

    private StyledText getStyledDamage(@Nullable TextColor typedColor) {
        var config = sourceType.getConfig();
        if (typedColor == null) {
            if (isCritical) {
                var textColor = TextColor.parse(config.getCriticalColor());
                int rgb = textColor != null ? textColor.getRgb() : 0xFF0000;
                return StyledText.literal(getFormattedDamage(), rgb);
            } else {
                var textColor = TextColor.parse(config.getDefaultColor());
                int rgb = textColor != null ? textColor.getRgb() : 0x303030;
                return StyledText.literal(getFormattedDamage(), rgb);
            }
        } else {
            return StyledText.literal(getFormattedDamage(), typedColor.getRgb());
        }
    }

    private int computeTextLength() {
        return getFormattedDamage().length();
    }

    private void setInvisible() {
        this.mainSlot.setAlpha(0f);
        this.subSlot.setAlpha(0f);
    }

    private void setScreenPos(ScreenPosition screenPosition, float... globalMultipliers) {
        this.mainSlot.setScreenPosition(screenPosition.offsetWithDepth(this.screenOffset), globalMultipliers);
        this.subSlot.setScreenPosition(screenPosition.offsetWithDepth(this.screenOffset), globalMultipliers);
    }

    private void setScale(float scale, float... globalMultipliers) {
        this.mainSlot.setScale(scale, scale, globalMultipliers);
        this.subSlot.setScale(scale, scale, globalMultipliers);
    }

    private void setAlpha(float alpha, float... globalMultipliers) {
        this.mainSlot.setAlpha(alpha, globalMultipliers);
        this.subSlot.setAlpha(alpha, globalMultipliers);
    }

    @Override
    public boolean isExpired() {
        if (!initialized) return false;
        return mainSlot.isExpired() && subSlot.isExpired();
    }
}
