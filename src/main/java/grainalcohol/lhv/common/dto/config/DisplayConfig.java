package grainalcohol.lhv.common.dto.config;

import grainalcohol.lhv.common.template.EffectTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
@AllArgsConstructor
public class DisplayConfig {
    private final String killDisplay;
    private final String InstantKillDisplay;
    private final int defaultColor;
    private final int criticalColor;
    private final String criticalFormatTemplate;
    private final OutlineSetting outlineSetting;
    private final int displayDuration;
    private final boolean retainWhenOffScreen;
    private final float depthToScaleRef;
    private final float minScale;
    private final float maxScale;
    private final float depthToAlphaRef;
    private final float minAlpha;
    private final float maxAlpha;
    private final OffsetSetting offsetSetting;
    private final Map<String, String> damageTypeColors;
     private final EffectTemplate effectTemplate;

    private DisplayConfig(DisplayConfig other) {
        this.killDisplay = other.killDisplay;
        this.InstantKillDisplay = other.InstantKillDisplay;
        this.defaultColor = other.defaultColor;
        this.criticalColor = other.criticalColor;
        this.criticalFormatTemplate = other.criticalFormatTemplate;
        this.outlineSetting = other.outlineSetting.copy();
        this.displayDuration = other.displayDuration;
        this.retainWhenOffScreen = other.retainWhenOffScreen;
        this.depthToScaleRef = other.depthToScaleRef;
        this.minScale = other.minScale;
        this.maxScale = other.maxScale;
        this.depthToAlphaRef = other.depthToAlphaRef;
        this.minAlpha = other.minAlpha;
        this.maxAlpha = other.maxAlpha;
        this.offsetSetting = other.offsetSetting.copy();
        this.damageTypeColors = Map.copyOf(other.damageTypeColors);
        this.effectTemplate = other.effectTemplate;
    }

    @Nullable
    public TextColor findColor(String damageTypeId) {
        String hex = getDamageTypeColors().get(damageTypeId);
        if (hex == null) return null;
        return TextColor.parse(hex);
    }

    public DisplayConfig copy() {
        return new DisplayConfig(this);
    }
}
