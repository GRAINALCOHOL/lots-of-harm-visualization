package grainalcohol.lhv.client;

import grainalcohol.lhv.client.display.DamageRouter;
import grainalcohol.lhv.client.subtext.SubTextProviders;
import grainalcohol.lhv.config.EntityConfig;
import grainalcohol.lhv.config.EnvConfig;
import grainalcohol.lhv.config.GlobalConfig;
import grainalcohol.lhv.config.PlayerConfig;
import grainalcohol.lhv.common.dto.LHVConfig;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LHVModClient implements ClientModInitializer {
    public static final DamageRouter ROUTER = new DamageRouter();
    public static final Logger LOGGER = LoggerFactory.getLogger(LHVModClient.class);

    @Override
    public void onInitializeClient() {
        SubTextProviders.init();

        ClientEventListener.init();
        ClientPacketHandler.init();

        PlayerConfig.load();
        EntityConfig.load();
        EnvConfig.load();
        GlobalConfig.load();
    }

    public static boolean isIgnoreType(String damageTypeId) {
        return GlobalConfig.shouldIgnore(damageTypeId);
    }

    @Nullable
    public static TextColor findDamageColor(LHVConfig config, String damageTypeId) {
        return config.findColor(damageTypeId);
    }

    private static float shapedOffset(double range) {
        double t = Math.random() * 2 - 1;
        double s = Math.signum(t) * (t * t);
        return (float) (s * range / 2);
    }

    /**
     * 偏移量服从二次分布
     * @param config 配置信息
     * @return 二维偏移量
     * @see shapedOffset(double)
     */
    @Contract("_ -> new")
    public static @NotNull Vec2f computeScreenOffset(@NotNull LHVConfig config) {
        return new Vec2f(
                shapedOffset(config.getScreenOffsetRangeX()),
                shapedOffset(config.getScreenOffsetRangeY())
        );
    }

    @Contract("_ -> new")
    public static @NotNull Vec3d computeWorldOffset(@NotNull LHVConfig config) {
        return new Vec3d(
                (Math.random() - 0.5) * config.getOffsetRangeX(),
                (Math.random() - 0.5) * config.getOffsetRangeY(),
                (Math.random() - 0.5) * config.getOffsetRangeZ()
        );
    }

    public static double computeVerticalOffset(UUID victimUuid) {
        var client = MinecraftClient.getInstance();
        if (client.world == null) return 0;

        var entity = ((WorldEntityLookupInvoker) client.world).invokeGetEntityLookup().get(victimUuid);

        if (entity == null) return 1.618;
        double height = entity.getHeight();
        if (height < 1.0) {
            return entity.getEyeHeight(entity.getPose());
        }
        return height * 0.6;
    }
}
