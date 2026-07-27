package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.display.func.DamageHandler;
import grainalcohol.lhv.common.enums.SourceType;

public class SingleRenderer extends BaseDamageRenderer {
    public SingleRenderer(SourceType sourceType) {
        super(sourceType);
    }

    @Override
    public DamageHandler getHandler() {
        return (damageAmount, isCritical) -> {
            this.damageAmount = damageAmount;
            this.isCritical = isCritical;
        };
    }
}
