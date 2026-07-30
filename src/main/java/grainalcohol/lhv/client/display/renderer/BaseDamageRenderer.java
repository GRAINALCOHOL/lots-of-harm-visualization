package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.LHVModClient;
import grainalcohol.lhv.client.display.func.CriticalHandler;
import grainalcohol.lhv.client.display.func.DamageHandler;
import grainalcohol.lhv.client.effect.effects.*;
import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.client.wrapper.TextDisplaySlot;
import grainalcohol.lhv.common.dto.*;
import grainalcohol.lhv.common.format.DamageFormatter;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.common.util.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public abstract class BaseDamageRenderer implements DamageRenderer {
    // render setting
    private final GeneralConfig generalConfig;
    private final FormatConfig formatConfig;
    private final DisplayConfig displayConfig;
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
    protected BigDecimal damageAmount;
    protected boolean isCritical;
    // 无穷（Infinity）和 NaN 与任何数进行运算，结果仍为 Infinity 或 NaN。
    protected boolean hasInfinity;
    protected boolean hasNaN;

    // effect
    @NotNull
    private final BounceEffect bounceEffect = new BounceEffect();
    @NotNull
    private final SweepEffect sweepEffect = new SweepEffect();

    protected BaseDamageRenderer(SourceType sourceType) {
        this.generalConfig = sourceType.getGeneralConfig();
        this.formatConfig = sourceType.getFormatConfig();
        this.displayConfig = sourceType.getDisplayConfig();
        this.formatter = sourceType.getFormatConfig().getFormatMode().createFormatter();
        this.screenOffset = sourceType.getGeneralConfig().computeScreenOffset();
        this.worldOffset = sourceType.getGeneralConfig().computeWorldOffset();

        this.mainSlot = TextDisplaySlot.empty(
                sourceType.getGeneralConfig().getDisplayDuration() * 50,
                0.72f, 0.8f,
                textDisplay -> {
                    textDisplay
                            .addEffect(new FlashInEffect())
                            .addEffect(new PulseEffect())
                            .addEffect(new ShrinkEffect())
                            .addEffect(new SimpleTypewriterEffect());
                    if (sourceType.getGeneralConfig().isPunchyEffectEnable()) {
                        textDisplay.addEffect(new SpringEffect());
                    } else {
                        textDisplay.addEffect(new SettleEffect());
                    }
                },
                textDisplay -> {
                    textDisplay.addEffect(bounceEffect).addEffect(sweepEffect);
                    if (bounceEffect.isFinished(computeTextLength())) bounceEffect.restart();
                    if (sweepEffect.isFinished(computeTextLength())) sweepEffect.restart();
                }
        );
        this.mainSlot.outline();
        this.subSlot = TextDisplaySlot.empty(
                sourceType.getGeneralConfig().getDisplayDuration() * 50,
                0.72f, 0.8f,
                textDisplay -> textDisplay
                        .addEffect(new FlashInEffect())
                        .addEffect(new SpringEffect())
                        .addEffect(new PulseEffect())
                        .addEffect(new ShrinkEffect())
                        .addEffect(new SimpleTypewriterEffect())
                        .addEffect(new SweepEffect()),
                null,
                -16, 0.8f
        );
        this.subSlot.outline();

        this.damageAmount = BigDecimal.ZERO;
        this.hasInfinity = false;
        this.hasNaN = false;

        this.hasBeenOnScreen = false;
        this.initialized = false;
    }

    @Override
    public void handleDamage(DamageInfo damageInfo) {
        double amount = damageInfo.getDamageAmount();
        if (Double.isInfinite(amount)) {
            this.mainSlot.rainbow();
            this.hasInfinity = true;
        } else if (Double.isNaN(amount)) {
            this.hasNaN = true;
        } else {
            // 非特殊值才处理伤害回调，避免污染伤害量
            // 并且BigDecimal也无法表示Infinity和NaN
            getDamageHandler().accept(BigDecimal.valueOf(amount));
        }
        getCriticalHandler().accept(damageInfo.isCritical());

        this.mainSlot.setText(getStyledDamage(damageInfo.getDamageColor()));
        if (damageInfo.getSubText() != null) {
            this.subSlot.clear();
            this.subSlot.setText(damageInfo.getSubText());
        }

        this.initialized = true;
    }

    abstract DamageHandler getDamageHandler();

    abstract CriticalHandler getCriticalHandler();

    @Override
    public void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta) {
        if (!initialized) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        this.updateWorldPos(worldPos, yawDelta);
        if (this.generalConfig.isInRenderRange(worldPos, client.player.getPos())) {
            this.mainSlot.render(drawContext);
            this.subSlot.render(drawContext);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void updateWorldPos(@NotNull Vec3d worldPos, float yawDelta) {
        if (!initialized) return;

        ScreenPosition screenPosition = ScreenUtil.worldToScreen(getRenderPos(worldPos, yawDelta));

        if (screenPosition != null) {
            this.latestScreenPos = screenPosition;
            this.hasBeenOnScreen = true;
            this.setScreenPos(screenPosition);
            this.setScale(screenPosition.depthToScale(
                    this.displayConfig.getDepthToScaleRef(),
                    this.displayConfig.getMinScale(),
                    this.displayConfig.getMaxScale()
            ));
            this.setAlpha(screenPosition.depthToAlpha(
                    this.displayConfig.getDepthToAlphaRef(),
                    this.displayConfig.getMinAlpha(),
                    this.displayConfig.getMaxAlpha()
            ));
        } else if (hasBeenOnScreen && generalConfig.isRetainWhenOffScreen()) {
            this.setScreenPos(latestScreenPos);
            this.setScale(latestScreenPos.depthToScale(
                    this.displayConfig.getDepthToScaleRef(),
                    this.displayConfig.getMinScale(),
                    this.displayConfig.getMaxScale()
            ), 0.6f);
            this.mainSlot.setAlpha(latestScreenPos.depthToAlpha(
                    this.displayConfig.getDepthToAlphaRef(),
                    this.displayConfig.getMinAlpha(),
                    this.displayConfig.getMaxAlpha()
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
        String criticalFormat = isCritical ? displayConfig.getCriticalFormatTemplate() : "%s";

        if (this.hasInfinity) {
            return criticalFormat.formatted(formatConfig.getInfinityDisplay());
        }
        if (this.hasNaN) {
            return criticalFormat.formatted(formatConfig.getNanDisplay());
        }

        return criticalFormat.formatted(formatter.format(formatConfig, damageAmount));
    }

    private StyledText getStyledDamage(@Nullable TextColor typedColor) {
        if (typedColor != null) {
            return StyledText.literal(getFormattedDamage(), typedColor.getRgb());
        }

        String colorStr = isCritical ? displayConfig.getCriticalColor() : displayConfig.getDefaultColor();
        TextColor textColor = TextColor.parse(colorStr);
        if (textColor == null) {
            LHVModClient.LOGGER.warn("Invalid {} color: {}, using fallback color.",
                    (isCritical ? "critical" : "default"), colorStr);
            return StyledText.literal(getFormattedDamage(), (isCritical ? 0xFF0000 : 0x303030));
        }

        return StyledText.literal(getFormattedDamage(), textColor.getRgb());
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
