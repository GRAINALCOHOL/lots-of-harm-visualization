package grainalcohol.lhv.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class ClientEventListener {
    public static void init() {
        HudRenderCallback.EVENT.register(LHVModClient.ROUTER::render);
    }
}
