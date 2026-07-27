package grainalcohol.lhv.internal;

import grainalcohol.lhv.client.effect.CharSetting;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public interface LHVGlyphRenderer {
    void lhv$render(
            CharSetting setting,
            VertexConsumer vertexConsumer,
            Matrix4f matrix4f
    );
}
