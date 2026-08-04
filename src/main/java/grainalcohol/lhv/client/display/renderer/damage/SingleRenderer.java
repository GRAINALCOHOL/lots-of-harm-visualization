package grainalcohol.lhv.client.display.renderer.damage;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.source.SourceType;

public class SingleRenderer extends BaseDamageRenderer {
    private final DecimalValue damageValue;
    private boolean isCritical;

    public SingleRenderer(SourceType sourceType) {
        super(sourceType);
        this.damageValue = new DecimalValue();
        this.isCritical = false;
    }

    @Override
    protected void handleDamage(double damageAmount, boolean isCritical) {
        this.damageValue.set(damageAmount);
        this.isCritical = isCritical;
    }

    @Override
    protected DecimalValue getDamageValue() {
        return this.damageValue;
    }

    @Override
    protected boolean isCritical() {
        return this.isCritical;
    }
}
