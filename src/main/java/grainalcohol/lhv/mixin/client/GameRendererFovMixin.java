package grainalcohol.lhv.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import grainalcohol.lhv.common.util.FovCache;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererFovMixin {
    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(D)Lorg/joml/Matrix4f;",
                    ordinal = 0
            )
    )
    private void lhv$captureRenderFov(
            float tickDelta, long limitTime, MatrixStack matrices,
            // 不知道为什么，总之这里@Local不能指定name
            CallbackInfo ci, @Local double fov
    ) {
        FovCache.setRenderFov(fov);
    }
}
