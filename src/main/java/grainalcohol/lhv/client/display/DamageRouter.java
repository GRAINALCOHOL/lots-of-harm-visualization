package grainalcohol.lhv.client.display;

import grainalcohol.lhv.client.LHVModClient;
import grainalcohol.lhv.client.display.manager.RendererManager;
import grainalcohol.lhv.client.display.manager.RendererManagerImpl;
import grainalcohol.lhv.client.subtext.SubTextProviders;
import grainalcohol.lhv.common.dto.DamageContext;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.config.GlobalConfig;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class DamageRouter {
    private final Map<UUID, RendererManager> MANAGERS = new HashMap<>();

    public RendererManager getManager(UUID victimUuid) {
        return MANAGERS.get(victimUuid);
    }

    public RendererManager initForVictim(UUID victimUuid, float victimYaw, Vec3d worldPos) {
        return MANAGERS.computeIfAbsent(
                victimUuid,
                k -> new RendererManagerImpl(
                        LHVModClient.computeVerticalOffset(k),
                        victimYaw, worldPos)
        );
    }

    public void handleDamage(
            @NotNull final SourceType sourceType,
            @NotNull final UUID victimUuid,
            final float victimYaw,
            @NotNull final Vec3d worldPos,
            @NotNull final DamageInfo damageInfo
    ) {
        initForVictim(
                victimUuid,
                victimYaw,
                worldPos
        ).handleDamage(sourceType, victimYaw, worldPos, damageInfo);
    }

    public void handleDamage(@NotNull final DamageContext damageContext) {
        if (GlobalConfig.shouldIgnore(damageContext.getDamageTypeId())) return;

        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        SourceType sourceType = damageContext.getSourceType();

        var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(damageContext.getVictimUuid());
        if (entity == null) return;

        if (!sourceType.getGeneralConfig().isInReceiveRange(
                entity.getPos(),
                client.player.getPos()
        )) return;

        DamageInfo damageInfo = new DamageInfo(
                (GlobalConfig.getInstance().infinityTestMode ? Double.POSITIVE_INFINITY : damageContext.getDamageAmount() + (GlobalConfig.getInstance().bigNumberTestMode ? Math.random() * 10000000000L : 0)),
                damageContext.isCritical(),
                SubTextProviders.compute(damageContext),
                sourceType.getDisplayConfig().findColor(damageContext.getDamageTypeId())
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

        MANAGERS.values().removeIf(RendererManager::isExpired);
        if (MANAGERS.isEmpty()) return;

        computeRenderStream(client.options, client.player).forEach(entry -> {
            var manager = entry.getValue();
            var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(entry.getKey());
            if (entity != null) {
                manager.render(drawContext, entity.getLerpedPos(tickDelta), entity.getYaw(tickDelta));
            } else {
                manager.render(drawContext);
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private @NotNull Stream<Map.Entry<UUID, RendererManager>> computeRenderStream(@NotNull GameOptions options, @NotNull ClientPlayerEntity player) {
        var stream = MANAGERS.entrySet().stream().filter(entry -> !(options.getPerspective().isFirstPerson() && player.getUuid().equals(entry.getKey())));

        var comparator = Comparator.<Map.Entry<UUID, RendererManager>>comparingDouble(e -> e.getValue().getLatestWorldPos().squaredDistanceTo(player.getPos()));
        return switch (GlobalConfig.getInstance().entitySortMode) {
            case RANDOM -> stream;
            case NEAREST -> stream.sorted(comparator.reversed());
            case FARTHEST -> stream.sorted(comparator);
        };
    }

    public void clear(@NotNull final UUID victimUuid) {
        MANAGERS.remove(victimUuid);
    }

    public void clear() {
        MANAGERS.clear();
    }
}
