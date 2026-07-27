package grainalcohol.lhv.mixin.client;

import grainalcohol.lhv.client.effect.CharSetting;
import grainalcohol.lhv.client.effect.QuadColorField;
import grainalcohol.lhv.internal.LHVGlyphRenderer;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GlyphRenderer.class)
public class GlyphRendererMixin implements LHVGlyphRenderer {
    @Shadow @Final private float minX;  // left
    @Shadow @Final private float maxX;  // right
    @Shadow @Final private float minY;  // up
    @Shadow @Final private float maxY;  // down
    @Shadow @Final private float minU;  // u0
    @Shadow @Final private float maxU;  // u1
    @Shadow @Final private float minV;  // v0
    @Shadow @Final private float maxV;  // v1

    @Unique
    @Override
    public void lhv$render(CharSetting setting, VertexConsumer vertexConsumer, Matrix4f matrix4f) {
        float x = setting.x;
        float y = setting.y;
        float alpha = setting.alpha;
        QuadColorField cf = setting.colorField;

        float centerX = (this.minX + this.maxX) * 0.5f;
        float centerY = (this.minY + this.maxY) * 0.5f;
        float halfWidth  = (this.maxX - this.minX) * 0.5f * setting.widthScale;
        float halfHeight = (this.maxY - this.minY) * 0.5f * setting.heightScale;

        float leftX  = x + centerX - halfWidth;
        float rightX = x + centerX + halfWidth;
        float upY    = y + centerY - halfHeight - 3f;
        float downY  = y + centerY + halfHeight - 3f;

        // 左上、坐下、右下、右上
        emit(vertexConsumer, matrix4f, leftX, upY,   minU, minV, cf.lt(), alpha);
        emit(vertexConsumer, matrix4f, leftX, downY, minU, maxV, cf.lb(), alpha);
        emit(vertexConsumer, matrix4f, rightX, downY,maxU, maxV, cf.rb(), alpha);
        emit(vertexConsumer, matrix4f, rightX, upY,  maxU, minV, cf.rt(), alpha);
    }

    @Unique
    private static void emit(
            VertexConsumer buf, Matrix4f matrix4f,
            float x, float y, float u, float v,
            int rgb, float alpha
    ) {
        buf.vertex(matrix4f, x, y, 0f)
           .color(((rgb >> 16) & 0xFF) / 255f,
                  ((rgb >> 8) & 0xFF) / 255f,
                  (rgb & 0xFF) / 255f, alpha)
           .texture(u, v)
           .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
           .next();
    }
}
