package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.display.func.CriticalHandler;
import grainalcohol.lhv.client.display.func.DamageHandler;
import grainalcohol.lhv.common.enums.SourceType;

public class SingleRenderer extends BaseDamageRenderer {
    public SingleRenderer(SourceType sourceType) {
        super(sourceType);
    }

    @Override
    public DamageHandler getDamageHandler() {
        return damageAmount -> {
            this.hasNaN = false;
            this.hasInfinity = false;
            this.mainSlot.rainbow(false);
            this.damageAmount = damageAmount;
        };
    }

    @Override
    CriticalHandler getCriticalHandler() {
        return isCritical -> this.isCritical = isCritical;
    }
}
