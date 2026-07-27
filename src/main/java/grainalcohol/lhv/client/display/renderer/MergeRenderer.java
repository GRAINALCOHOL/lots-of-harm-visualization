package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.display.func.DamageHandler;
import grainalcohol.lhv.common.enums.SourceType;

public class MergeRenderer extends BaseDamageRenderer {
    public MergeRenderer(SourceType sourceType) {
        super(sourceType);
    }

    @Override
    public DamageHandler getHandler() {
        return (damageAmount, isCritical) -> {
            this.damageAmount += damageAmount;
            this.isCritical |= isCritical;
        };
    }
}
