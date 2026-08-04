package grainalcohol.lhv.client.display;

import grainalcohol.lhv.client.effect.Effect;
import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.QuadColorField;
import grainalcohol.lhv.client.effect.DisplayContext;
import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.ScreenPosition;
import grainalcohol.lhv.internal.LHVGlyphRenderer;
import grainalcohol.lhv.mixin.client.FontAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TextDisplay {
    private final long creationTimeMs = Util.getMeasuringTimeMs();
    private long activeTimeMs = Util.getMeasuringTimeMs();

    private final int durationMs;
    @NotNull
    private final Identifier fontId;

    private float screenX;
    private float screenY;

    @NotNull
    private String text;
    private float textAlpha;
    private int textRgb;
    // 这两个scale是纯信息量，仅在变换矩阵时应用即可，否则变换将被重复应用两次
    private float textWidthScale;
    private float textHeightScale;

    // outline
    private boolean outline;
    private float outlineWidth;
    private int outlineRgb;

    // rainbow
    private boolean rainbow;
    private float rainbowSpeed; // Hz
    private float rainbowSpace;

    @NotNull
    private final Map<Class<? extends Effect>, Effect> effectMap = new HashMap<>();

    // cache
    @NotNull
    private OrderedText cachedOrderedText;

    private TextDisplay(StyledText text, int durationMs, @NotNull Identifier fontId) {
        this.text = text.getString();
        this.durationMs = durationMs;
        this.textAlpha = 1f;
        this.textRgb = text.getRgb();
        this.fontId = fontId;

        this.screenX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f;
        this.screenY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2f;
        this.textWidthScale = 1f;
        this.textHeightScale = 1f;

        this.cachedOrderedText = Text.literal(text.getString()).styled(
                style -> style.withFont(fontId)
        ).asOrderedText();
    }

    private TextDisplay(StyledText text, int durationMs) {
        this.text = text.getString();
        this.durationMs = durationMs;
        this.textAlpha = 1f;
        this.textRgb = text.getRgb();
        this.fontId = Style.DEFAULT_FONT_ID;

        this.screenX = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f;
        this.screenY = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2f;
        this.textWidthScale = 1f;
        this.textHeightScale = 1f;

        this.cachedOrderedText = Text.literal(text.getString()).styled(
                style -> style.withFont(fontId)
        ).asOrderedText();
    }

    public static TextDisplay create(StyledText text, int durationMs, Identifier fontId) {
        return new TextDisplay(text, durationMs, fontId);
    }

    public static TextDisplay create(StyledText text, int durationMs) {
        return new TextDisplay(text, durationMs);
    }

    public void render(DrawContext drawContext) {
        if (isExpired()) return;

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        @SuppressWarnings("resource")
        var fontStorage = ((FontAccess) textRenderer).callGetFontStorage(this.fontId);
        var vertexConsumers = drawContext.getVertexConsumers();
        float textWidth = textRenderer.getWidth(this.cachedOrderedText);
        float textHeight = textRenderer.fontHeight;

        // 屏幕边界夹紧
        var window = MinecraftClient.getInstance().getWindow();
        int screenW = window.getScaledWidth();
        int screenH = window.getScaledHeight();

        float clampedX = this.screenX;
        float clampedY = this.screenY;

        // 利用scale参数直接计算视觉效果值，而不是应用到实际值
        float halfW = textWidth * this.textWidthScale * 0.5f;
        float halfH = textHeight * this.textHeightScale * 0.5f;

        if (clampedX - halfW < 0)       clampedX = halfW;
        if (clampedX + halfW > screenW) clampedX = screenW - halfW;
        if (clampedY - halfH < 0)       clampedY = halfH;
        if (clampedY + halfH > screenH) clampedY = screenH - halfH;

        var matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(clampedX, clampedY, 0);
        matrices.scale(this.textWidthScale, this.textHeightScale, 1f);
        matrices.translate(-clampedX, -clampedY, 0);

        var ctx = new DisplayContext(
                clampedX, clampedY,
                textWidth, textHeight,
                this.getTotalLifetimeMs(),
                this.activeMs(),
                this.textLength()
        );

        for (Effect e : this.effectMap.values()) e.apply(drawContext, ctx);

        final float[] cursorX = {clampedX - textWidth / 2f};
        final float[] cursorY = {clampedY - textHeight / 2f};
        this.cachedOrderedText.accept((index, style, codePoint) -> {
            float advance = renderSingleChar(
                    index, codePoint,
                    drawContext, fontStorage,
                    vertexConsumers, ctx,
                    cursorX[0], cursorY[0]
            );
            cursorX[0] += advance;
            return true;
        });

        matrices.pop();
    }

    private float renderSingleChar(
            int index, int codePoint,
            DrawContext drawContext,
            FontStorage fontStorage,
            VertexConsumerProvider.Immediate vertexConsumers,
            DisplayContext ctx,
            float cursorX, float textY
    ) {
        QuadColorField cf = QuadColorField.verticalGradient(this.textRgb);
        var setting = new CharSetting(
                cursorX, textY,
                0f, 1f, 1f,
                codePoint, this.textAlpha,
                cf, index
        );

        for (Effect e : this.effectMap.values()) e.apply(ctx, setting);

        // 将覆盖原本的颜色设置
        if (this.rainbow) {
            // 被加数部分是时间 * 频率 = 相位
            // 加数部分是由 index * (1 / space) 简化而来的
            float hue = ((this.lifetimeMs() * 0.001f * this.rainbowSpeed) + (index / this.rainbowSpace)) % 1f;
            int rgb = MathHelper.hsvToRgb(hue, 1f, 1f);
            setting.colorField = QuadColorField.pure(rgb);
        }

        GlyphRenderer glyph = fontStorage.getGlyphRenderer(codePoint);
        if (!(glyph instanceof EmptyGlyphRenderer)) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(glyph.getLayer(TextRenderer.TextLayerType.NORMAL));
            var matrix = drawContext.getMatrices().peek().getPositionMatrix();
            if (this.outline) {renderCharOutline(
                    glyph, setting, vertexConsumer, matrix
            );}
            ((LHVGlyphRenderer) glyph).lhv$render(setting, vertexConsumer, matrix);
        }

        return fontStorage.getGlyph(codePoint, false).getAdvance(false);
    }

    private void renderCharOutline(
            GlyphRenderer glyph,
            CharSetting setting,
            VertexConsumer vertexConsumer,
            Matrix4f matrix
    ) {
        float[][] dirs = {
                { 1.000f,  0.000f}, { 0.924f,  0.383f},
                { 0.707f,  0.707f}, { 0.383f,  0.924f},
                { 0.000f,  1.000f}, {-0.383f,  0.924f},
                {-0.707f,  0.707f}, {-0.924f,  0.383f},
                {-1.000f,  0.000f}, {-0.924f, -0.383f},
                {-0.707f, -0.707f}, {-0.383f, -0.924f},
                { 0.000f, -1.000f}, { 0.383f, -0.924f},
                { 0.707f, -0.707f}, { 0.924f, -0.383f}
        };
        var copied = setting.copy();
        copied.colorField = QuadColorField.pure(this.outlineRgb);

        float step = 1f;
        int innerRings = Math.max(0, (int) Math.ceil(this.outlineWidth / step) - 1);

        int totalSamples = dirs.length * (1 + innerRings);
        copied.alpha = 1f - (float) Math.pow(
                1f - this.textAlpha * setting.alpha,
                4f / totalSamples
        );

        for (float[] dir : dirs) {
            copied.x = setting.x + dir[0] * this.outlineWidth;
            copied.y = setting.y + dir[1] * this.outlineWidth;
            ((LHVGlyphRenderer) glyph).lhv$render(copied, vertexConsumer, matrix);
        }

        for (int i = 1; i <= innerRings; i++) {
            float r = Math.min(i * step, this.outlineWidth);
            for (float[] dir : dirs) {
                copied.x = setting.x + dir[0] * r;
                copied.y = setting.y + dir[1] * r;
                ((LHVGlyphRenderer) glyph).lhv$render(copied, vertexConsumer, matrix);
            }
        }
    }

    public TextDisplay rainbow(boolean enable) {
        return this.rainbow(enable, 0.5f, 8f);
    }

    public TextDisplay rainbow() {
        return this.rainbow(true);
    }

    public TextDisplay rainbow(float speed, float space) {
        return this.rainbow(true, speed, space);
    }

    public TextDisplay rainbow(boolean enable, float speed, float space) {
        this.rainbow = enable;
        this.rainbowSpeed = speed;
        this.rainbowSpace = space;
        return this;
    }

    public TextDisplay outline(boolean enable) {
        return this.outline(enable, 0.8f, 0x000000);
    }

    public TextDisplay outline() {
        return this.outline(true);
    }

    public TextDisplay outline(float width, int rgb) {
        return this.outline(true, width, rgb);
    }

    public TextDisplay outline(
            boolean enable, float width, int rgb
    ) {
        this.outline = enable;
        this.outlineWidth = width;
        this.outlineRgb = rgb;
        return this;
    }

    public TextDisplay pauseAllEffects() {
        this.effectMap.values().forEach(Effect::pause);
        return this;
    }

    public TextDisplay addEffect(Effect effect) {
        return this.addEffect(effect, true);
    }

    public TextDisplay addEffect(Effect effect, boolean enable) {
        this.effectMap.put(effect.getClass(), effect);
        if (enable) effect.start();
        return this;
    }

    public <E extends Effect> Optional<Effect> getOrPutEffect(Class<E> effectClass, E effect) {
        return this.getOrPutEffect(effectClass, effect, true);
    }

    public <E extends Effect> Optional<Effect> getOrPutEffect(Class<E> effectClass, E effect, boolean enable) {
        if (this.effectMap.containsKey(effectClass)) {
            return Optional.of(this.effectMap.get(effectClass));
        } else {
            this.addEffect(effect, enable);
            return Optional.empty();
        }
    }

    public TextDisplay setCurrentText(StyledText text) {
        this.text = text.getString();
        this.textRgb = text.getRgb();

        this.cachedOrderedText = Text.literal(text.getString()).styled(
                style -> style.withFont(fontId)
        ).asOrderedText();
        return this;
    }

    public TextDisplay resetAge() {
        this.activeTimeMs = Util.getMeasuringTimeMs();
        return this;
    }

    public TextDisplay setAlpha(float alpha) {
        this.textAlpha = alpha;
        return this;
    }

    public TextDisplay multiplyAlpha(float multiplier) {
        this.textAlpha *= multiplier;
        return this;
    }

    public TextDisplay setScale(float scale) {
        return this.setScale(scale, scale);
    }

    public TextDisplay multiplyScale(float multiplier) {
        return this.multiplyScale(multiplier, multiplier);
    }

    public TextDisplay setScale(float widthScale, float heightScale) {
        this.textWidthScale = widthScale;
        this.textHeightScale = heightScale;
        return this;
    }

    public TextDisplay multiplyScale(float widthMultiplier, float heightMultiplier) {
        this.textWidthScale *= widthMultiplier;
        this.textHeightScale *= heightMultiplier;
        return this;
    }

    public TextDisplay setScreenPosition(ScreenPosition screenPosition) {
        this.screenX = screenPosition.x();
        this.screenY = screenPosition.y();
        return this;
    }

    public long getTotalLifetimeMs() {
        return this.getExtraHeadMs() + this.durationMs + this.getExtraTailMs();
    }

    private int getExtraHeadMs() {
        return effectMap.values()
                .stream()
                .mapToInt(effect -> effect.getHeadMs(this.textLength()))
                .max()
                .orElse(0);
    }

    private int getExtraTailMs() {
        return effectMap.values()
                .stream()
                .mapToInt(effect -> effect.getTailMs(this.textLength()))
                .max()
                .orElse(0);
    }

    public int textLength() {
        return this.text.length();
    }

    public long lifetimeMs() {
        return Util.getMeasuringTimeMs() - creationTimeMs;
    }

    public long activeMs() {
        return Util.getMeasuringTimeMs() - activeTimeMs;
    }

    public boolean isExpired() {
        return activeMs() > getTotalLifetimeMs();
    }
}
