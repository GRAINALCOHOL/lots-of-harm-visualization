package grainalcohol.lhv;

import committee.nova.mods.avaritia.common.item.tools.InfinitySwordItem;
import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.network.DamageS2CPacket;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.common.source.SourceTypes;
import grainalcohol.lhv.flag.FlagContext;
import grainalcohol.lhv.flag.FlagProviders;
import grainalcohol.lhv.internal.CriticalArgController;
import grainalcohol.lhv.internal.DamageTimeAccessor;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

public class LHVMod implements ModInitializer {
	public static final String MOD_ID = "lhv";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();

	public static final String DIED_FLAG = "Died";

	@Override
	public void onInitialize() {
		FlagProviders.init();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	public static void handleDamage(LivingEntity victim, DamageSource source, float amount) {
		if (victim.getWorld().isClient()) return;
		boolean isCritical = LHVMod.resolveCritical(source);

		FlagContext flagContext = new FlagContext(source, victim, amount, isCritical);
		Set<String> damageFlags = FlagProviders.compute(flagContext);

		@SuppressWarnings("ConstantConditions")
		String damageTypeId = source.getTypeRegistryEntry()
				.getKey()
				.map(RegistryKey::getValue)
				.orElse(Identifier.of("minecraft", "generic"))
				.toString();

		@Nullable UUID attackerUuid;
		@NotNull SourceType sourceType;
		double damageAmount = amount;

		if (source.getAttacker() == null) {
			attackerUuid = null;
			sourceType = SourceTypes.ENVIRONMENT;
		} else if (source.getAttacker() instanceof ServerPlayerEntity serverPlayerEntity) {
			attackerUuid = serverPlayerEntity.getUuid();
			sourceType = SourceTypes.PLAYER;
			if (attackWithInfinitySword(serverPlayerEntity, damageTypeId)) {
				damageAmount = Double.POSITIVE_INFINITY;
				damageFlags.add(LHVMod.DIED_FLAG);
			}
		} else if (source.getAttacker() instanceof LivingEntity livingEntity) {
			if (damageTypeId.equals("avaritia:infinity") && victim.getUuid().equals(livingEntity.getUuid())) {
				// Re:Avaritia中的寰宇支配之剑会在一般攻击（直接造成的伤害）后~
				// ~立刻追加一次受害者自己攻击自己的Infinity伤害以击杀受害者。
				// 因此这里需要做一次排除，以防显示多余的伤害数字。
				return;
			}
			attackerUuid = livingEntity.getUuid();
			sourceType = SourceTypes.ENTITY;
		} else {
			attackerUuid = null;
			sourceType = SourceTypes.ENVIRONMENT;
		}

		DamageContext damageContext = new DamageContext(
				sourceType, damageAmount, isCritical,
				attackerUuid, victim.getUuid(),
				damageTypeId, resolveKillTime(victim),
				damageFlags
		);

		DamageS2CPacket.sendToAllPlayers(victim.getWorld(), damageContext);
	}

	private static long resolveKillTime(LivingEntity victim) {
		var accessor = ((DamageTimeAccessor) victim);
		if (accessor.lhv$getFirstDamageTime() == -1L) {
			// 从未受伤
			accessor.lhv$setFirstDamageTime(victim.getWorld().getTime());
		} else if (victim.getWorld().getTime() - accessor.lhv$getFirstDamageTime() > 200) {
			// 10秒内未受伤，重置首次受伤时间
			accessor.lhv$setFirstDamageTime(victim.getWorld().getTime());
		}
		return victim.isDead() ? victim.getWorld().getTime() - accessor.lhv$getFirstDamageTime() : -1L;
	}

	private static boolean attackWithInfinitySword(ServerPlayerEntity player, String damageTypeId) {
		return FabricLoader.getInstance().isModLoaded("avaritia")
				&& (damageTypeId.equals("avaritia:infinity") || player.getMainHandStack().getItem() instanceof InfinitySwordItem);
	}

	private static boolean resolveCritical(DamageSource source) {
		boolean isCritical = ((CriticalArgController) source).lhv$isCriticalHit();
		if (FabricLoader.getInstance().isModLoaded("playerex") && source.getSource() instanceof ArrowEntity arrow) {
			isCritical = arrow.isCritical();
		}
		return isCritical;
	}
}
