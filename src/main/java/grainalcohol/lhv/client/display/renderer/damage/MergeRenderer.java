package grainalcohol.lhv.client.display.renderer.damage;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.source.SourceType;

public class MergeRenderer extends BaseDamageRenderer {
    private final DecimalValue damageValue;
    private boolean hasCritical;

    public MergeRenderer(SourceType sourceType) {
        super(sourceType);
        this.damageValue = new DecimalValue();
        this.hasCritical = false;
    }

    @Override
    protected void handleDamage(double damageAmount, boolean isCritical) {
        this.damageValue.add(damageAmount);
        this.hasCritical |= isCritical;
    }

    @Override
    protected DecimalValue getDamageValue() {
        return this.damageValue;
    }

    @Override
    protected boolean isCritical() {
        return this.hasCritical;
    }
}
