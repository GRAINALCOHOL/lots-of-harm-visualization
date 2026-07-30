package grainalcohol.lhv.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@AllArgsConstructor
public class DisplayConfig {
    private final String killDisplay;
    private final String defaultColor;
    private final String criticalColor;
    private final String criticalFormatTemplate;
    private final boolean outlineEnable;
    private final String outlineColor;
    private final float outlineWidth;
    private final float depthToScaleRef;
    private final float minScale;
    private final float maxScale;
    private final float depthToAlphaRef;
    private final float minAlpha;
    private final float maxAlpha;
    private final Map<String, String> damageTypeColors;

    @Nullable
    public TextColor findColor(String damageTypeId) {
        String hex = getDamageTypeColors().get(damageTypeId);
        if (hex == null) return null;
        return TextColor.parse(hex);
    }
}
