package grainalcohol.lhv.client;

import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.enums.SourceType;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LHVModAPI {
    public static void handleDamage(
            @NotNull final SourceType sourceType,
            @NotNull final UUID victimUuid,
            @NotNull final DamageInfo damageInfo
    ) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(victimUuid);
        if (entity == null) return;

        if (!sourceType.getConfig().isInReceiveRange(
                entity.getPos(),
                client.player.getPos()
        )) return;

        LHVModClient.ROUTER.handleDamage(
                sourceType,
                victimUuid,
                entity.getYaw(),
                entity.getPos(),
                damageInfo
        );
    }

    public static void handleDamage(@NotNull final DamageContext damageContext) {
        LHVModClient.ROUTER.handleDamage(damageContext);
    }

    public static void clearDamage() {
        LHVModClient.ROUTER.clear();
    }

    public static void clearDamage(@NotNull final UUID victimUuid) {
        LHVModClient.ROUTER.clear(victimUuid);
    }
}
