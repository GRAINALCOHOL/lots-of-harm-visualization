package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.common.dto.DamageInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public interface DamageRenderer {
    void handleDamage(DamageInfo damageInfo);
    void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta);
    boolean isExpired();
}
