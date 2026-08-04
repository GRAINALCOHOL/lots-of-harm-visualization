package grainalcohol.lhv.client.display.renderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public interface WorldTextRenderer<S> {
    void setStatus(S status);
    void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta);
    boolean isExpired();
}
