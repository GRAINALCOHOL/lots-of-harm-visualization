package grainalcohol.lhv.client;

import grainalcohol.lhv.client.display.manager.RendererManager;
import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.source.LHVSourceTypes;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LHVModAPI {
    @NotNull
    public static SourceType registerSourceType(SourceType sourceType) {
        return LHVSourceTypes.register(sourceType);
    }

    @Nullable
    public static SourceType findSourceType(Identifier identifier) {
        return LHVSourceTypes.getSourceType(identifier);
    }

    public static boolean initForVictim(UUID victimUuid) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return false;

        var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(victimUuid);
        if (entity == null) return false;

        LHVModClient.ROUTER.initForVictim(victimUuid, entity.getYaw(), entity.getPos());
        return true;
    }

    public static void initForVictim(UUID victimUuid, float victimYaw, @NotNull Vec3d worldPos) {
        LHVModClient.ROUTER.initForVictim(victimUuid, victimYaw, worldPos);
    }

    public static RendererManager getRendererManager(UUID victimUuid) {
        return LHVModClient.ROUTER.getManager(victimUuid);
    }

    public static void handleDamage(
            @NotNull final SourceType sourceType,
            @NotNull final UUID victimUuid,
            @NotNull final DamageInfo damageInfo
    ) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(victimUuid);
        if (entity == null) return;

        if (!sourceType.getGeneralConfig().isInReceiveRange(
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
