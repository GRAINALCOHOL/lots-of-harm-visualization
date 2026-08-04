package grainalcohol.lhv.client.display.manager;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.DamageInfo;
import grainalcohol.lhv.common.source.SourceType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RendererManager {
    void handleDamage(@NotNull SourceType sourceType, @NotNull DamageInfo damageInfo);
    void handleText(@NotNull SourceType sourceType, @NotNull List<StyledText> texts);
    Vec3d getLatestWorldPos();
    void render(DrawContext drawContext);
    void render(DrawContext drawContext, Vec3d lerpedPos, float lerpedYaw);
    boolean isExpired();
}
