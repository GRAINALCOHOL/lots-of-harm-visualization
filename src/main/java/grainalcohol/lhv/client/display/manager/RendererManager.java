package grainalcohol.lhv.client.display.manager;

import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.source.SourceType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public interface RendererManager {
    void handleDamage(
            SourceType sourceType, float victimYaw,
            Vec3d worldPos, DamageInfo damageInfo
    );
    Vec3d getLatestWorldPos();
    void render(DrawContext drawContext);
    void render(DrawContext drawContext, Vec3d lerpedPos, float lerpedYaw);
    boolean isExpired();
}
