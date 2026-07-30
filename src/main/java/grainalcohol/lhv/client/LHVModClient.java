package grainalcohol.lhv.client;

import grainalcohol.lhv.client.display.DamageRouter;
import grainalcohol.lhv.client.subtext.SubTextProviders;
import grainalcohol.lhv.config.EntityConfig;
import grainalcohol.lhv.config.EnvConfig;
import grainalcohol.lhv.config.GlobalConfig;
import grainalcohol.lhv.config.PlayerConfig;
import grainalcohol.lhv.mixin.accessor.WorldEntityLookupInvoker;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    public static Set<String> getDefaultIgnoreTypes() {
        Set<String> set = new HashSet<>();
        set.add("minecraft:out_of_world");
        set.add("minecraft:in_wall");
        set.add("minecraft:cramming");
        return set;
    }

    public static Map<String, String> getDefaultColors() {
        Map<String, String> map = new HashMap<>();
        map.put("minecraft:in_fire", "#FE3622");
        map.put("minecraft:on_fire", "#FF3017");
        map.put("minecraft:lava", "#FF1B00");
        map.put("minecraft:hot_floor", "#FF4300");
        map.put("minecraft:lightning_bolt", "#EBD31C");
        map.put("minecraft:drown", "#090072");
        map.put("minecraft:starve", "#782531");
        map.put("minecraft:cactus", "#1F9300");
        map.put("minecraft:fall", "#303030");
        map.put("minecraft:fly_into_wall", "#303030");
        map.put("minecraft:out_of_world", "#D000FF");
        map.put("minecraft:dry_out", "#000CFF");
        map.put("minecraft:sweet_berry_bush", "#00FF4A");
        map.put("minecraft:freeze", "#001FFF");
        map.put("minecraft:stalagmite", "#4A4000");
        map.put("minecraft:falling_block", "#4A4000");
        map.put("minecraft:falling_anvil", "#030200");
        map.put("minecraft:falling_stalactite", "#4A4000");
        map.put("minecraft:sting", "#00FF12");
        map.put("minecraft:outside_border", "#000000");
        map.put("minecraft:mob_attack", "#FF0000");
        map.put("minecraft:mob_attack_no_aggro", "#FF0064");
        map.put("minecraft:player_attack", "#FF0000");
        map.put("minecraft:arrow", "#FF0000");
        map.put("minecraft:trident", "#000AFF");
        map.put("minecraft:mob_projectile", "#FF0000");
        map.put("minecraft:fireworks", "#00FF07");
        map.put("minecraft:fireball", "#FF6B00");
        map.put("minecraft:unattributed_fireball", "#FD6A00");
        map.put("minecraft:wither_skull", "#000000");
        map.put("minecraft:thrown", "#5C0062");
        map.put("minecraft:dragon_breath", "#5C0062");
        map.put("minecraft:sonic_boom", "#003E8D");
        map.put("minecraft:magic", "#6B008D");
        map.put("minecraft:indirect_magic", "#6D008D");
        map.put("minecraft:wither", "#000000");
        map.put("minecraft:thorns", "#004614");
        map.put("minecraft:explosion", "#FFC000");
        map.put("minecraft:player_explosion", "#FFC000");
        map.put("minecraft:generic", "#FF0058");
        map.put("minecraft:generic_kill", "#000000");
        map.put("minecraft:bad_respawn_point", "#000000");
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
}
