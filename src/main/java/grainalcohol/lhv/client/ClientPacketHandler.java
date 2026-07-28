package grainalcohol.lhv.client;

import grainalcohol.lhv.common.network.DamageS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientPacketHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(DamageS2CPacket.PACKET_TYPE, (damageContext, player, sender) ->
                LHVModAPI.handleDamage(damageContext));
    }
}
