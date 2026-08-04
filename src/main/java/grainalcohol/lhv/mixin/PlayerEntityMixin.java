package grainalcohol.lhv.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.internal.CriticalArgController;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyArg(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            ),
            index = 0
    )
    private DamageSource modifyDamageSource(DamageSource source, @Local(index = 8) boolean isCritical) {
        if (isCritical) {
            ((CriticalArgController) source).lhv$setCriticalHit(true);
            return source;
        }
        return source;
    }

    @Inject(
            method = "applyDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        PlayerEntity victim = (PlayerEntity) (Object) this;
        LHVMod.handleDamage(victim, source, amount);
    }
}
