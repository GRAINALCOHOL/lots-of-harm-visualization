package grainalcohol.lhv.client.wrapper;

import grainalcohol.lhv.client.display.TextDisplay;
import grainalcohol.lhv.client.display.func.TextDisplayHandler;
import grainalcohol.lhv.common.dto.ScreenPosition;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextDisplaySlot {
    private final int durationMs;
    @Nullable
    private final TextDisplayHandler onTextCreated;
    @Nullable
    private final TextDisplayHandler onTextChanged;

    @Nullable
    private TextDisplay textDisplay;
    private boolean outline;
    private boolean rainbow;
    private float widthMultiplier = 1f;
    private float heightMultiplier = 1f;

    private TextDisplaySlot(
            @Nullable StyledText styledText,
            int durationMs,
            @Nullable TextDisplayHandler onTextCreated,
            @Nullable TextDisplayHandler onTextChanged
    ) {
        this.durationMs = durationMs;
        this.onTextCreated = onTextCreated;
        this.onTextChanged = onTextChanged;
        if (styledText == null) {
            this.textDisplay = null;
        } else {
            this.createTextDisplay(styledText);
        }
    }

    public static TextDisplaySlot create(
            @NotNull StyledText styledText,
            int durationMs,
            @Nullable TextDisplayHandler onTextCreated,
            @Nullable TextDisplayHandler onTextChanged
    ) {
        return new TextDisplaySlot(styledText, durationMs, onTextCreated, onTextChanged);
    }

    public static TextDisplaySlot empty(
            int durationMs,
            @Nullable TextDisplayHandler onTextCreated,
            @Nullable TextDisplayHandler onTextChanged
    ) {
        return new TextDisplaySlot(null, durationMs, onTextCreated, onTextChanged);
    }

    public void rainbow(boolean enable) {
        this.rainbow = enable;
        if (this.textDisplay != null) {
            this.textDisplay.rainbow(enable);
        }
    }

    public void rainbow() {
        this.rainbow(true);
    }

    public void outline(boolean enable) {
        this.outline = enable;
        if (this.textDisplay != null) {
            this.textDisplay.outline(enable);
        }
    }

    public void outline() {
        this.outline(true);
    }

    public boolean isEmpty() {
        return this.textDisplay == null;
    }

    public void render(DrawContext drawContext) {
        if (this.textDisplay != null) {
            this.textDisplay.render(drawContext);
        }
    }

    public void resetAge() {
        if (this.textDisplay != null) {
            this.textDisplay.resetAge();
        }
    }

    public void multiplyScale(float scaleMultiplier) {
        this.multiplyWidth(scaleMultiplier);
        this.multiplyHeight(scaleMultiplier);
    }

    public void multiplyHeight(float heightMultiplier) {
        this.heightMultiplier *= heightMultiplier;
    }

    public void multiplyWidth(float widthMultiplier) {
        this.widthMultiplier *= widthMultiplier;
    }

    public void setScale(float scale) {
        this.setScale(scale, scale);
    }

    public void setScale(float widthScale, float heightScale) {
        if (this.textDisplay == null) return;
        this.textDisplay.setScale(
                widthScale * widthMultiplier,
                heightScale * heightMultiplier
        );
    }

    public void setScreenPosition(@NotNull ScreenPosition screenPosition) {
        if (this.textDisplay == null) return;
        this.textDisplay.setScreenPosition(screenPosition);
    }

    public void setAlpha(float alpha) {
        if (this.textDisplay == null) return;
        this.textDisplay.setAlpha(alpha);
    }

    public void multiplyAlpha(float multiplier) {
        if (this.textDisplay == null) return;
        this.textDisplay.multiplyAlpha(multiplier);
    }

    public void setText(@NotNull StyledText styledText) {
        if (styledText.isBlank()) return;

        if (textDisplay == null) {
            this.createTextDisplay(styledText);
        } else {
            textDisplay.setCurrentText(styledText);
            textDisplay.resetAge();
            if (onTextChanged != null) {
                onTextChanged.accept(textDisplay);
            }
        }
    }

    private void createTextDisplay(@NotNull StyledText styledText) {
        this.textDisplay = TextDisplay
                .create(styledText, durationMs)
                .outline(outline)
                .rainbow(rainbow);
        if (onTextCreated != null) {
            onTextCreated.accept(textDisplay);
        }
    }

    public void clear() {
        this.textDisplay = null;
    }

    public boolean isExpired() {
        return textDisplay == null || textDisplay.isExpired();
    }
}
