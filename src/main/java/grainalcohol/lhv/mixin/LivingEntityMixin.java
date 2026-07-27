package grainalcohol.lhv.mixin;

import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.enums.SourceType;
import grainalcohol.lhv.common.network.DamageS2CPacket;
import grainalcohol.lhv.internal.CriticalArgController;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "applyDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V",
                    shift = At.Shift.AFTER
            )
    )
    @SuppressWarnings("ConstantConditions")
    private void afterApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.getWorld().isClient()) return;
        boolean isCritical = ((CriticalArgController) source).lhv$isCriticalHit();

        String damageTypeId = source.getTypeRegistryEntry().getKey().map(RegistryKey::getValue).orElse(Identifier.of("minecraft", "generic")).toString();

        @NotNull SourceType sourceType = SourceType.PLAYER;
        @Nullable ServerPlayerEntity target = null;

        if (source.getAttacker() == null) {
            target = null;
            sourceType = SourceType.ENVIRONMENT;
        } else if (source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity) {
            target = serverPlayerEntity;
        } else if (source.getAttacker() instanceof LivingEntity) {
            target = null;
            sourceType = SourceType.ENTITY;
        }

        DamageContext damageContext = new DamageContext(
                sourceType, amount, isCritical,
                self.getUuid(),
                damageTypeId,
                self.isDead()
        );

        if (target == null) {
            DamageS2CPacket.sendToAllPlayers(self.getWorld(), damageContext);
        } else {
            DamageS2CPacket.sendToPlayer(target, damageContext);
        }
    }
}
