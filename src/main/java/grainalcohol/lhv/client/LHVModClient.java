package grainalcohol.lhv.client;

import grainalcohol.lhv.client.display.DamageRouter;
import grainalcohol.lhv.client.text.TextProviders;
import grainalcohol.lhv.config.GlobalConfig;
import grainalcohol.lhv.config.EntityConfig;
import grainalcohol.lhv.config.EnvConfig;
import grainalcohol.lhv.config.PlayerConfig;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LHVModClient implements ClientModInitializer {
    public static final DamageRouter ROUTER = new DamageRouter();
    public static final Logger LOGGER = LoggerFactory.getLogger(LHVModClient.class);

    @Override
    public void onInitializeClient() {
        TextProviders.init();

        ClientEventListener.init();
        ClientPacketHandler.init();

        PlayerConfig.load();
        EntityConfig.load();
        EnvConfig.load();
        GlobalConfig.load();
    }

    public static Set<String> getDefaultIgnoreTypes() {
        Set<String> set = new HashSet<>();
        set.add("minecraft:out_of_world");
        set.add("minecraft:in_wall");
        set.add("minecraft:cramming");
        return set;
    }

    public static Map<String, String> getDefaultColors() {
        Map<String, String> map = new HashMap<>();
        map.put("minecraft:in_fire", "#FE5E4E");
        map.put("minecraft:on_fire", "#FF5945");
        map.put("minecraft:lava", "#FF4833");
        map.put("minecraft:hot_floor", "#FF6833");
        map.put("minecraft:lightning_bolt", "#EFDB49");
        map.put("minecraft:drown", "#3A338E");
        map.put("minecraft:starve", "#93505A");
        map.put("minecraft:cactus", "#4BA833");
        map.put("minecraft:fall", "#595959");
        map.put("minecraft:fly_into_wall", "#595959");
        map.put("minecraft:out_of_world", "#D933FF");
        map.put("minecraft:dry_out", "#333CFF");
        map.put("minecraft:sweet_berry_bush", "#33FF6E");
        map.put("minecraft:freeze", "#334BFF");
        map.put("minecraft:stalagmite", "#6E6633");
        map.put("minecraft:falling_block", "#6E6633");
        map.put("minecraft:falling_anvil", "#353433");
        map.put("minecraft:falling_stalactite", "#6E6633");
        map.put("minecraft:sting", "#33FF41");
        map.put("minecraft:outside_border", "#333333");
        map.put("minecraft:mob_attack", "#FF3333");
        map.put("minecraft:mob_attack_no_aggro", "#FF3383");
        map.put("minecraft:player_attack", "#FF3333");
        map.put("minecraft:arrow", "#FF3333");
        map.put("minecraft:trident", "#333BFF");
        map.put("minecraft:mob_projectile", "#FF3333");
        map.put("minecraft:fireworks", "#33FF38");
        map.put("minecraft:fireball", "#FF8833");
        map.put("minecraft:unattributed_fireball", "#FD8733");
        map.put("minecraft:wither_skull", "#333333");
        map.put("minecraft:thrown", "#7C3381");
        map.put("minecraft:dragon_breath", "#7C3381");
        map.put("minecraft:sonic_boom", "#3364A3");
        map.put("minecraft:magic", "#8833A3");
        map.put("minecraft:indirect_magic", "#8A33A3");
        map.put("minecraft:wither", "#333333");
        map.put("minecraft:thorns", "#336B43");
        map.put("minecraft:explosion", "#FFCC33");
        map.put("minecraft:player_explosion", "#FFCC33");
        map.put("minecraft:generic", "#FF3379");
        map.put("minecraft:generic_kill", "#333333");
        map.put("minecraft:bad_respawn_point", "#333333");
        return map;
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

    public static boolean attackFromOtherPlayer(@NotNull ClientWorld world, @NotNull ClientPlayerEntity player, @NotNull UUID attackerUuid) {
        var entity = ((WorldEntityLookupInvoker) world).invokeGetEntityLookup().get(attackerUuid);

        if (entity instanceof ClientPlayerEntity attacker) {
            return !attacker.getUuid().equals(player.getUuid());
        }
        return false;
    }
}
