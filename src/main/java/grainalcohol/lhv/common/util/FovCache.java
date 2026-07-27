package grainalcohol.lhv.common.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

/**
 * FOV 缓存工具类。
 * <p>
 * 相机的 FOV 值很少变化（只在玩家修改设置或进入瞄准状态时改变），
 * 因此可以缓存 tan(halfFov) 的值，避免每次投影都从投影矩阵重新计算。
 */
public class FovCache {
    private static int cachedFov = -1;
    private static int cachedScreenWidth = -1;
    private static int cachedScreenHeight = -1;
    private static double cachedTanHalfFovX = -1;
    private static double cachedTanHalfFovY = -1;

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
        int currentFov = MinecraftClient.getInstance().options.getFov().getValue();

        // FOV 或屏幕尺寸变了才重新计算
        if (currentFov == cachedFov && screenWidth == cachedScreenWidth && screenHeight == cachedScreenHeight) {
            return;
        }

        cachedFov = currentFov;
        cachedScreenWidth = screenWidth;
        cachedScreenHeight = screenHeight;

        // 优先从投影矩阵中推导，若投影信息不可用则使用默认 FOV 回退
        Camera.Projection projection = camera.getProjection();
        if (projection != null) {
            // 从投影矩阵中提取近平面信息来推算 FOV
            // center:         近平面上的中心点
            // topCenter:      近平面上边缘的中心点 → 用于计算垂直半高
            // rightCenter:    近平面右边缘的中心点 → 用于计算水平半宽
            final Vec3d center = projection.getPosition(0, 0);
            final double nearPlaneDistance = center.length();
            final Vec3d topCenter = projection.getPosition(0, 1);
            final double halfHeightAtNear = topCenter.subtract(center).length();
            final Vec3d rightCenter = projection.getPosition(-1, 0);
            final double halfWidthAtNear = rightCenter.subtract(center).length();
            // tan(halfFov) = halfSize / nearDistance
            cachedTanHalfFovX = halfWidthAtNear / Math.max(nearPlaneDistance, 0.01);
            cachedTanHalfFovY = halfHeightAtNear / Math.max(nearPlaneDistance, 0.01);
        } else {
            // 回退方案：从客户端设置中读取玩家配置的 FOV（默认 70°）
            final MinecraftClient client = MinecraftClient.getInstance();
            final double fovDegrees = (client != null)
                    ? client.options.getFov().getValue()
                    : 70;
            final double halfFovRadians = Math.toRadians(fovDegrees / 2.0);
            final double aspectRatio = (double) screenWidth / screenHeight;
            final double tanHalfFovFromSettings = Math.tan(halfFovRadians);
            cachedTanHalfFovY = tanHalfFovFromSettings;
            cachedTanHalfFovX = tanHalfFovFromSettings * aspectRatio;
        }
    }
}
