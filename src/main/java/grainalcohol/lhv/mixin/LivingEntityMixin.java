package grainalcohol.lhv.mixin;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.internal.DamageTimeAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements DamageTimeAccessor {
    @Unique
    private long lhv$firstDamageTime = -1L;

    @Override
    public long lhv$getFirstDamageTime() {
        return lhv$firstDamageTime;
    }

    @Override
    public void lhv$setFirstDamageTime(long time) {
        this.lhv$firstDamageTime = time;
    }

    @Inject(
            method = "applyDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity victim = (LivingEntity) (Object) this;
        LHVMod.handleDamage(victim, source, amount);
    }
}
