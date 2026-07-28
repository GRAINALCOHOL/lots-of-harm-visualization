package grainalcohol.lhv.client.display;

import grainalcohol.lhv.client.LHVModClient;
import grainalcohol.lhv.client.display.manager.RendererManager;
import grainalcohol.lhv.client.display.manager.RendererManagerImpl;
import grainalcohol.lhv.client.subtext.SubTextProviders;
import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.enums.SourceType;
import grainalcohol.lhv.config.GlobalConfig;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageRouter {
    private final Map<UUID, RendererManager> MANAGERS = new HashMap<>();

    private void handleDamage(
            @NotNull final SourceType sourceType,
            @NotNull final UUID victimUuid,
            final float victimYaw,
            @NotNull final Vec3d worldPos,
            @NotNull final DamageInfo damageInfo
    ) {
        MANAGERS.computeIfAbsent(
                victimUuid,
                k -> new RendererManagerImpl(
                        LHVModClient.computeVerticalOffset(k),
                        victimYaw, worldPos)
        ).handleDamage(sourceType, victimYaw, worldPos, damageInfo);
    }

    public void handleDamage(@NotNull final DamageContext damageContext) {
        if (LHVModClient.isIgnoreType(damageContext.getDamageTypeId())) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        SourceType sourceType = damageContext.getSourceType();

        var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(damageContext.getVictimUuid());
        if (entity == null) return;

        if (!sourceType.getConfig().isInReceiveRange(
                entity.getPos(),
                client.player.getPos()
        )) return;

        DamageInfo damageInfo = new DamageInfo(
                (GlobalConfig.getInstance().infinityTestMode ? Double.POSITIVE_INFINITY : damageContext.getDamageAmount() + (GlobalConfig.getInstance().bigNumberTestMode ? Math.random() * 10000000000L : 0)),
                damageContext.isCritical(),
                SubTextProviders.compute(damageContext),
                LHVModClient.findDamageColor(sourceType.getConfig(), damageContext.getDamageTypeId())
        );

        handleDamage(
                damageContext.getSourceType(),
                damageContext.getVictimUuid(),
                entity.getYaw(),
                entity.getPos(),
                damageInfo
        );
    }

    public void render(DrawContext drawContext, float tickDelta) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        var iterator = MANAGERS.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var manager = entry.getValue();
            if (manager.isExpired()) {
                iterator.remove();
            } else {
                UUID uuid = entry.getKey();
                if (client.options.getPerspective().isFirstPerson() && client.player.getUuid().equals(uuid)) {
                    continue;
                }

                var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(uuid);
                if (entity != null) {
                    Vec3d lerpedPos = entity.getLerpedPos(tickDelta);
                    float lerpedYaw = entity.getYaw(tickDelta);

                    manager.render(drawContext, lerpedPos, lerpedYaw);
                } else {
                    manager.render(drawContext);
                }
            }
        }
    }

    public void clear(UUID victimUuid) {
        MANAGERS.remove(victimUuid);
    }

    public void clear() {
        MANAGERS.clear();
    }
}
