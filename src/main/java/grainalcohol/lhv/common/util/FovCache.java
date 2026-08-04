package grainalcohol.lhv.common.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.jetbrains.annotations.Nullable;

/**
 * FOV 缓存工具类。
 * <p>
 * 相机的 FOV 值很少变化（只在玩家修改设置、进入缩放/瞄准状态或冲刺等动态 FOV 变化时改变），
 * 因此可以缓存 tan(halfFov) 的值，避免每次投影都重新计算。
 * <p>
 * 实际渲染 FOV 由 {@code GameRendererFovMixin} 在 {@code renderWorld} 中捕获
 * （含望远镜/TACZ 瞄准镜缩放，在 getFov 返回值上二次改写的 mod 也覆盖到），
 * 捕获值为 null 时回退到玩家设置的 FOV。
 */
public class FovCache {
    private static double cachedFov = -1;
    private static int cachedScreenWidth = -1;
    private static int cachedScreenHeight = -1;
    private static double cachedTanHalfFovX = -1;
    private static double cachedTanHalfFovY = -1;

    // 由 GameRendererFovMixin 捕获的当前实际渲染 FOV（度，可含小数），未捕获时为 null
    @Nullable
    private static Double renderFov;

    // 缩放放大系数的次线性压缩指数（<1）：单调递增保证与视觉缩放动画同步（不会提前到顶），
    // 同时压住极端值——望远镜 mag 11.4× → 5.5×（FOV 70），近距离+缩放时数字不会过大
    private static final float ZOOM_COMPRESSION_EXPONENT = 0.7f;

    public static void setRenderFov(double fov) {
        renderFov = fov;
    }

    /**
     * 获取当前实际渲染 FOV（度）；未捕获时回退到玩家设置值。
     */
    public static double getRenderFov() {
        if (renderFov != null) return renderFov;
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null ? client.options.getFov().getValue() : 70;
    }

    /**
     * 获取缩放放大系数 = 设置 FOV 半角正切 / 当前实际渲染 FOV 半角正切的次线性压缩值。
     * <p>
     * 对原始放大系数取 pow(mag, 0.7) 压缩：单调递增，因此望远镜/瞄准镜缩放动画期间
     * 数字持续增长、与视觉同步（硬上限会截断动画导致数字提前放大到位），
     * 同时压住极端值（mag 11.4× → 5.5×，近距离+缩放时数字不会过大）。
     * 正常/冲刺/飞行（原始系数 &lt; 1）时结果恒为 1.0，数字不会被压小。
     * 垂直与水平方向的纵横比在比值中约掉，因此无需屏幕尺寸。
     * <p>
     * 该值只影响数字尺寸/偏移的缩放，投影仍使用 {@link #getTanHalfFovX}/{@link #getTanHalfFovY}
     * 的真实 FOV，因此实体屏幕位置始终完整跟随缩放。
     */
    public static float getZoomMagnification() {
        MinecraftClient client = MinecraftClient.getInstance();
        double baseFov = client != null ? client.options.getFov().getValue() : 70;
        double baseTanHalf = Math.tan(Math.toRadians(baseFov / 2.0));
        double currentTanHalf = Math.tan(Math.toRadians(getRenderFov() / 2.0));
        float mag = (float) (baseTanHalf / Math.max(currentTanHalf, 0.01));
        float compressed = (float) Math.pow(mag, ZOOM_COMPRESSION_EXPONENT);
        return Math.max(1.0f, compressed);
    }

    /**
     * 获取水平方向的 tan(halfFov)。
     * 如果 FOV 或屏幕尺寸没变，直接返回缓存值。
     */
    public static double getTanHalfFovX(Camera camera, int screenWidth, int screenHeight) {
        updateCache(camera, screenWidth, screenHeight);
        return cachedTanHalfFovX;
    }

    /**
     * 获取垂直方向的 tan(halfFov)。
     * 如果 FOV 或屏幕尺寸没变，直接返回缓存值。
     */
    public static double getTanHalfFovY(Camera camera, int screenWidth, int screenHeight) {
        updateCache(camera, screenWidth, screenHeight);
        return cachedTanHalfFovY;
    }

    /**
     * 手动使缓存失效（比如玩家修改了 FOV 设置后调用）。
     */
    public static void invalidate() {
        cachedFov = -1;
    }

    private static void updateCache(Camera camera, int screenWidth, int screenHeight) {
        double currentFov = getRenderFov();

        // FOV 或屏幕尺寸变了才重新计算
        if (currentFov == cachedFov && screenWidth == cachedScreenWidth && screenHeight == cachedScreenHeight) {
            return;
        }

        cachedFov = currentFov;
        cachedScreenWidth = screenWidth;
        cachedScreenHeight = screenHeight;

        // 直接从捕获的实际渲染 FOV 推导 tan(halfFov)。FOV 为垂直 FOV，
        // 与 GameRenderer.getBasicProjectionMatrix 中 createPerspectiveMatrix 的 fovY 语义一致。
        double halfFovRadians = Math.toRadians(currentFov / 2.0);
        double aspectRatio = (double) screenWidth / screenHeight;
        cachedTanHalfFovY = Math.tan(halfFovRadians);
        cachedTanHalfFovX = cachedTanHalfFovY * aspectRatio;
    }
}
