package grainalcohol.lhv.mixin;

import grainalcohol.lhv.internal.CriticalArgController;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements CriticalArgController {
    @Unique
    private boolean isCriticalHit = false;

    @Unique
    @Override
    public void lhv$setCriticalHit(boolean isCriticalHit) {
        this.isCriticalHit = isCriticalHit;
    }

    @Unique
    @Override
    public boolean lhv$isCriticalHit() {
        return this.isCriticalHit;
    }
}
