package grainalcohol.lhv.client.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Formatting;

@AllArgsConstructor
public class StyledText {
    private final String text;
    @Getter
    private final int rgb;

    public static StyledText literal(String text) {
        return new StyledText(text, 0xFFFFFF);
    }

    @SuppressWarnings("ConstantConditions")
    public static StyledText literal(String text, Formatting formatting) {
        return new StyledText(text, formatting.isColor() ? formatting.getColorValue() : 0xFFFFFF);
    }

    public static StyledText literal(String text, int rgb) {
        return new StyledText(text, rgb);
    }

    public static StyledText empty() {
        return new StyledText("", 0xFFFFFF);
    }

    public boolean isEmpty() {
        return text.isEmpty();
    }

    public boolean isBlank() {
        return text.isBlank();
    }

    public String getString() {
        return text;
    }

    public int length() {
        return text.length();
    }
}
