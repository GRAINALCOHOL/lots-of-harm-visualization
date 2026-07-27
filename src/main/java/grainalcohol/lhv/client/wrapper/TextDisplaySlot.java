package grainalcohol.lhv.client.wrapper;

import grainalcohol.lhv.client.display.TextDisplay;
import grainalcohol.lhv.client.display.func.TextDisplayHandler;
import grainalcohol.lhv.common.dto.ScreenPosition;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextDisplaySlot {
    @Nullable
    private TextDisplay textDisplay;
    private final int durationMs;
    private final boolean outline;
    private final float widthMultiplier;
    private final float heightMultiplier;
    @Nullable
    private final TextDisplayHandler onTextCreated;
    @Nullable
    private final TextDisplayHandler onTextChanged;
    private final float verticalOffset;
    private final float scaleMultiplier;

    private TextDisplaySlot(
            @Nullable StyledText styledText,
            int durationMs, boolean outline,
            float widthMultiplier,
            float heightMultiplier,
            @Nullable TextDisplayHandler onTextCreated,
            @Nullable TextDisplayHandler onTextChanged,
            float verticalOffset,
            float scaleMultiplier
    ) {
        this.durationMs = durationMs;
        this.outline = outline;
        this.widthMultiplier = widthMultiplier;
        this.heightMultiplier = heightMultiplier;
        this.onTextCreated = onTextCreated;
        this.onTextChanged = onTextChanged;
        this.verticalOffset = verticalOffset;
        this.scaleMultiplier = scaleMultiplier;
        if (styledText == null) {
            this.textDisplay = null;
        } else {
            this.createTextDisplay(styledText);
        }
    }

    public static TextDisplaySlot empty(
            int durationMs, boolean outline,
            float widthMultiplier,
            float heightMultiplier,
            @Nullable TextDisplayHandler onTextCreated,
            @Nullable TextDisplayHandler onTextChanged
    ) {
        return empty(durationMs, outline, widthMultiplier, heightMultiplier, onTextCreated, onTextChanged, 0.0f, 1.0f);
    }

    public static TextDisplaySlot empty(
            int durationMs, boolean outline,
            float widthMultiplier,
            float heightMultiplier,
            @Nullable TextDisplayHandler onTextCreated,
            @Nullable TextDisplayHandler onTextChanged,
            float verticalOffset,
            float scaleMultiplier
    ) {
        return new TextDisplaySlot(null, durationMs, outline, widthMultiplier, heightMultiplier, onTextCreated, onTextChanged, verticalOffset, scaleMultiplier);
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

    public void setScale(float horizontalScale, float verticalScale, float... globalMultipliers) {
        if (this.textDisplay == null) return;

        float combinedMultiplier = 1.0f;
        for (float multiplier : globalMultipliers) {
            combinedMultiplier *= multiplier;
        }
        this.textDisplay.setScale(
                horizontalScale * widthMultiplier * scaleMultiplier * combinedMultiplier,
                verticalScale * heightMultiplier * scaleMultiplier * combinedMultiplier
        );
    }

    public void setScreenPosition(@NotNull ScreenPosition screenPosition, float... globalMultipliers) {
        if (this.textDisplay == null) return;

        float combinedMultiplier = 1.0f;
        for (float multiplier : globalMultipliers) {
            combinedMultiplier *= multiplier;
        }
        this.textDisplay.setScreenPosition(
                screenPosition.offsetWithDepth(0.0f, this.verticalOffset * combinedMultiplier)
        );
    }

    public void setAlpha(float alpha, float... globalMultipliers) {
        if (this.textDisplay == null) return;

        float combinedMultiplier = 1.0f;
        for (float multiplier : globalMultipliers) {
            combinedMultiplier *= multiplier;
        }

        this.textDisplay.setAlpha(alpha * combinedMultiplier);
    }

    public void setScreenPositionWithScale(@NotNull ScreenPosition screenPosition, float... globalMultipliers) {
        if (this.textDisplay == null) return;

        float combinedMultiplier = 1.0f;
        for (float multiplier : globalMultipliers) {
            combinedMultiplier *= multiplier;
        }
        this.textDisplay.setScreenPosition(
                screenPosition.offsetWithDepth(0.0f, this.verticalOffset * combinedMultiplier)
        );
        float horizontalScale = (float) (screenPosition.depthToScale() * widthMultiplier * this.scaleMultiplier * combinedMultiplier);
        float verticalScale = (float) (screenPosition.depthToScale() * heightMultiplier * this.scaleMultiplier * combinedMultiplier);
        this.textDisplay.setScale(horizontalScale, verticalScale);
    }

//    public void setInvisible() {
//        if (this.textDisplay == null) return;
//        this.textDisplay.setScale(0f, 0f);
//    }

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
                .outline(outline);
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
