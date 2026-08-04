package grainalcohol.lhv.client.display.renderer.damage;

import grainalcohol.lhv.client.display.renderer.BaseWorldTextRenderer;
import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.*;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.format.DamageFormatter;
import grainalcohol.lhv.common.source.SourceType;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

public abstract class BaseDamageRenderer extends BaseWorldTextRenderer<DamageInfo> implements DamageRenderer {
    private final DisplayConfig displayConfig;
    private final DamageFormatter formatter;

    public BaseDamageRenderer(SourceType sourceType) {
        super(sourceType);
        this.displayConfig = sourceType.getDisplayConfig();
        this.formatter = sourceType.getFormatConfig().createFormatter();
    }

    @Override
    public void setStatus(DamageInfo damageInfo) {
        this.handleDamage(damageInfo.getDamageAmount(), damageInfo.isCritical());

        if (this.getDamageValue().isInfinite()) {
            this.screenTextRenderer.textDisplaySlot.rainbow();
        } else {
            this.screenTextRenderer.textDisplaySlot.rainbow(false);
        }

        this.screenTextRenderer.setText(getStyledDamage(damageInfo.getDamageColor()));
        this.setInitialized();
    }

    protected abstract void handleDamage(double damageAmount, boolean isCritical);

    protected abstract DecimalValue getDamageValue();

    protected abstract boolean isCritical();

    private StyledText getStyledDamage(@Nullable TextColor typedColor) {
        if (typedColor != null) {
            return StyledText.literal(getFormattedDamage(), typedColor.getRgb());
        }

        int rgb = this.isCritical() ? displayConfig.getCriticalColor() : displayConfig.getDefaultColor();
        return StyledText.literal(getFormattedDamage(), TextColor.fromRgb(rgb));
    }

    private String getFormattedDamage() {
        String criticalFormat = this.isCritical() ? displayConfig.getCriticalFormatTemplate() : "%s";
        return criticalFormat.formatted(formatter.format(this.getDamageValue()));
    }
}
