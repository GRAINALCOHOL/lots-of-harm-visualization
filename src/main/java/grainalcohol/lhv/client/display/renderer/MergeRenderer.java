package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.display.func.CriticalHandler;
import grainalcohol.lhv.client.display.func.DamageHandler;
import grainalcohol.lhv.common.enums.SourceType;

import java.math.BigDecimal;

public class MergeRenderer extends BaseDamageRenderer {
    public MergeRenderer(SourceType sourceType) {
        super(sourceType);
    }

    @Override
    public DamageHandler getDamageHandler() {
        return damageAmount -> this.damageAmount = this.damageAmount.add(damageAmount);
    }

    @Override
    CriticalHandler getCriticalHandler() {
        return isCritical -> this.isCritical |= isCritical;
    }
}
