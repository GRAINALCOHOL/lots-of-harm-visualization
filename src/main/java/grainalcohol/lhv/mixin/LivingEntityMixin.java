package grainalcohol.lhv.mixin;

import committee.nova.mods.avaritia.common.item.tools.InfinitySwordItem;
import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.network.DamageS2CPacket;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.common.source.LHVSourceTypes;
import grainalcohol.lhv.internal.CriticalArgController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
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
        LivingEntity victim = (LivingEntity) (Object) this;
        if (victim.getWorld().isClient()) return;
        boolean isCritical = ((CriticalArgController) source).lhv$isCriticalHit();

        if (FabricLoader.getInstance().isModLoaded("playerex") && source.getSource() instanceof ArrowEntity arrow) {
            isCritical = arrow.isCritical();
        }

        String damageTypeId = source.getTypeRegistryEntry()
                .getKey()
                .map(RegistryKey::getValue)
                .orElse(Identifier.of("minecraft", "generic"))
                .toString();
        @NotNull SourceType sourceType = LHVSourceTypes.PLAYER;
        @Nullable ServerPlayerEntity target = null;
        double damage = amount;
        boolean isDead = victim.isDead();

        if (source.getAttacker() == null) {
            target = null;
            sourceType = LHVSourceTypes.ENVIRONMENT;
        } else if (source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity) {
            target = serverPlayerEntity;
            if (FabricLoader.getInstance().isModLoaded("avaritia")
                    && (damageTypeId.equals("avaritia:infinity")
                    || serverPlayerEntity.getMainHandStack().getItem() instanceof InfinitySwordItem)) {
                damage = Double.POSITIVE_INFINITY;
                isDead = true;
            }
        } else if (source.getAttacker() instanceof LivingEntity livingEntity) {
            if (damageTypeId.equals("avaritia:infinity") && victim.getUuid().equals(livingEntity.getUuid())) {
                // Re:Avaritia中的寰宇支配之剑会在一般攻击（直接造成的伤害）后~
                // ~立刻追加一次受害者自己攻击自己的Infinity伤害以击杀受害者。
                // 因此这里需要做一次排除，以防显示多余的伤害数字。
                return;
            }
            target = null;
            sourceType = LHVSourceTypes.ENTITY;
        }

        DamageContext damageContext = new DamageContext(
                sourceType, damage, isCritical,
                victim.getUuid(),
                damageTypeId,
                isDead
        );

        if (target == null) {
            DamageS2CPacket.sendToAllPlayers(victim.getWorld(), damageContext);
        } else {
            DamageS2CPacket.sendToPlayer(target, damageContext);
        }
    }
}
