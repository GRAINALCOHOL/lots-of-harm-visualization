package grainalcohol.lhv.common.network;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.DamageContext;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class DamageS2CPacket {
    public static final PacketType<DamageContext> PACKET_TYPE = PacketType.create(LHVMod.id("damage_context"), DamageContext::read);

    public static void sendToPlayer(ServerPlayerEntity player, DamageContext damageContext) {
        ServerPlayNetworking.send(player, damageContext);
    }

    public static void sendToAllPlayers(ServerWorld serverWorld, DamageContext damageContext) {
        for (var player : serverWorld.getPlayers()) {
            ServerPlayNetworking.send(player, damageContext);
        }
    }

    public static void sendToAllPlayers(World world, DamageContext damageContext) {
        if (world instanceof ServerWorld serverWorld) {
            sendToAllPlayers(serverWorld, damageContext);
        }
    }
}
