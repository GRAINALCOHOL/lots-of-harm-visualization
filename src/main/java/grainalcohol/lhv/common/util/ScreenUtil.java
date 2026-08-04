package grainalcohol.lhv.common.util;

import grainalcohol.lhv.common.dto.ScreenPosition;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Predicate;

public class ScreenUtil {
    public static final Predicate<BlockState> WILL_BLOCK_RAY_CAST = state -> {
        if (state.isAir()) return false;
        if (state.isIn(BlockTags.LEAVES)) return true;
        if (state.isOpaque()) return true;
        if (!state.getFluidState().isEmpty()) return false;
        return false;
    };

    @NotNull
    public static ScreenPosition applyOffset(@NotNull ScreenPosition screenPosition, final Vec3d... vec3ds) {
        for (var v : vec3ds) {
            var sp = worldToScreen(v);
            if (sp != null) screenPosition = sp;
        }
        return screenPosition;
    }

    @NotNull
    public static ScreenPosition applyOffset(@NotNull ScreenPosition screenPosition, final ScreenPosition... others) {
        for (var sp : others) {
            screenPosition = screenPosition.offset(sp);
        }
        return screenPosition;
    }

    @NotNull
    public static ScreenPosition applyOffset(@NotNull ScreenPosition screenPosition, final Vec2f... vec2fs) {
        for (var v : vec2fs) {
            screenPosition = screenPosition.offset(v);
        }
        return screenPosition;
    }

    /**
     * 将实体在世界坐标系中的位置（取眼睛高度）转换为屏幕像素坐标。
     *
     * @param camera       游戏相机，用于获取观察者位置和朝向
     * @param entity       目标实体
     * @param screenWidth  屏幕宽度（像素）
     * @param screenHeight 屏幕高度（像素）
     * @return 屏幕坐标，若目标在相机后方或参数无效则返回 null
     */
    @Nullable
    public static ScreenPosition worldToScreen(
            final Camera camera,
            final Entity entity,
            final int screenWidth,
            final int screenHeight
    ) {
        return worldToScreen(camera, entity, screenWidth, screenHeight, false);
    }

    /**
     * 将实体在世界坐标系中的位置（取眼睛高度）转换为屏幕像素坐标。
     *
     * @param camera         游戏相机，用于获取观察者位置和朝向
     * @param entity         目标实体
     * @param screenWidth    屏幕宽度（像素）
     * @param screenHeight   屏幕高度（像素）
     * @param checkOcclusion 是否进行遮挡检测（射线检测相机与目标之间是否有方块阻挡）
     * @return 屏幕坐标，若目标在相机后方、被方块遮挡或参数无效则返回 null
     */
    @Nullable
    public static ScreenPosition worldToScreen(
            final Camera camera,
            final Entity entity,
            final int screenWidth,
            final int screenHeight,
            final boolean checkOcclusion
    ) {
        if (camera == null || entity == null || !camera.isReady()) {
            return null;
        }
        if (checkOcclusion && isOccluded(camera, entity)) {
            return null;
        }
        return project(camera, entity.getX(), entity.getY(), entity.getZ(),
                screenWidth, screenHeight);
    }

    /**
     * 将世界坐标系中的任意点转换为屏幕像素坐标。
     *
     * @param camera     游戏相机
     * @param worldPos  世界坐标点
     * @param screenWidth  屏幕宽度（像素）
     * @param screenHeight 屏幕高度（像素）
     * @return 屏幕坐标，若目标在相机后方或参数无效则返回 null
     */
    @Nullable
    public static ScreenPosition worldToScreen(
            final Camera camera,
            final Vec3d worldPos,
            final int screenWidth,
            final int screenHeight
    ) {
        if (camera == null || !camera.isReady()) {
            return null;
        }
        return project(camera, worldPos.x, worldPos.y, worldPos.z, screenWidth, screenHeight);
    }

    /**
     * 将世界坐标系中的任意点转换为屏幕像素坐标。
     *
     * @param camera     游戏相机
     * @param worldX     世界坐标 X
     * @param worldY     世界坐标 Y
     * @param worldZ     世界坐标 Z
     * @param screenWidth  屏幕宽度（像素）
     * @param screenHeight 屏幕高度（像素）
     * @return 屏幕坐标，若目标在相机后方或参数无效则返回 null
     */
    @Nullable
    public static ScreenPosition worldToScreen(
            final Camera camera,
            final double worldX, final double worldY, final double worldZ,
            final int screenWidth,
            final int screenHeight
    ) {
        if (camera == null || !camera.isReady()) {
            return null;
        }
        return project(camera, worldX, worldY, worldZ, screenWidth, screenHeight);
    }

    /**
     * 使用当前客户端的相机和屏幕尺寸，将实体位置转换为屏幕像素坐标。
     *
     * @param entity 目标实体
     * @return 屏幕坐标，若不在游戏内或目标不可见则返回 null
     */
    @Nullable
    public static ScreenPosition worldToScreen(final Entity entity) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return null;
        return worldToScreen(
                client.gameRenderer.getCamera(), entity,
                client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight()
        );
    }

    /**
     * 使用当前客户端的相机和屏幕尺寸，将世界坐标点转换为屏幕像素坐标。
     *
     * @param worldPos 世界坐标点
     * @return 屏幕坐标，若不在游戏内或目标不可见则返回 null
     */
    @Nullable
    public static ScreenPosition worldToScreen(final Vec3d worldPos) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return null;
        return worldToScreen(
                client.gameRenderer.getCamera(), worldPos,
                client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight()
        );
    }

    public static ScreenPosition worldToScreen(final double worldX, final double worldY, final double worldZ) {
        var client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return null;
        return worldToScreen(
                client.gameRenderer.getCamera(), worldX, worldY, worldZ,
                client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight()
        );
    }

    /**
     * 检测从相机位置到目标实体之间是否有不透明方块遮挡。
     *
     * @param camera  相机
     * @param target  目标实体
     * @return true 表示被遮挡
     */
    private static boolean isOccluded(Camera camera, Entity target) {
        final Vec3d cameraPos = camera.getPos();
        final Vec3d targetPos = new Vec3d(target.getX(), target.getEyeY(), target.getZ());
        final BlockStateRaycastContext context = new BlockStateRaycastContext(
                cameraPos, targetPos,
                WILL_BLOCK_RAY_CAST
        );
        final BlockHitResult hit = target.getWorld().raycast(context);
        return hit.getType() == HitResult.Type.BLOCK
                && hit.getPos().squaredDistanceTo(cameraPos) < cameraPos.squaredDistanceTo(targetPos);
    }

    /**
     * 核心投影方法：将世界坐标转换为屏幕像素坐标。
     * <p>
     * 投影流程：<br>
     * 1. 计算目标点相对于相机的位置向量<br>
     * 2. 将相对位置分别投影到相机的左、上、前三个方向，得到相机空间坐标<br>
     * 3. 利用投影矩阵（或回退的默认 FOV）计算半视角正切值<br>
     * 4. 通过透视除法得到归一化设备坐标（NDC），再映射到屏幕像素
     *
     * @param camera       游戏相机
     * @param worldX       目标点世界坐标 X
     * @param worldY       目标点世界坐标 Y
     * @param worldZ       目标点世界坐标 Z
     * @param screenWidth  屏幕宽度（像素）
     * @param screenHeight 屏幕高度（像素）
     * @return 屏幕坐标，若目标在相机后方则返回 null
     */
    @Nullable
    private static ScreenPosition project(
            final Camera camera,
            final double worldX, final double worldY, final double worldZ,
            final int screenWidth, final int screenHeight
    ) {
        // —— 第一步：计算目标点相对于相机的偏移向量 ——
        final Vec3d cameraPosition = camera.getPos();
        final double relativeX = worldX - cameraPosition.x;
        final double relativeY = worldY - cameraPosition.y;
        final double relativeZ = worldZ - cameraPosition.z;

        // —— 第二步：获取相机的三个基向量（水平面、垂直面、对角面）——
        // forward: 相机前方向量（水平面法线）
        // up:      相机上方向量（垂直面法线）
        // left:    相机左方向量（对角面法线）
        final Vector3f forward = camera.getHorizontalPlane();
        final Vector3f up = camera.getVerticalPlane();
        final Vector3f left = camera.getDiagonalPlane();

        // —— 第三步：将相对偏移向量投影到相机空间的三个轴上 ——
        // cameraSpaceX: 相机空间中的水平分量（左正右负）
        // cameraSpaceY: 相机空间中的垂直分量（上正下负）
        // cameraDepth:  相机空间中的深度分量（前方为正，越大越远）
        final double cameraSpaceX = relativeX * left.x() + relativeY * left.y() + relativeZ * left.z();
        final double cameraSpaceY = relativeX * up.x() + relativeY * up.y() + relativeZ * up.z();
        final double cameraDepth = relativeX * forward.x() + relativeY * forward.y() + relativeZ * forward.z();

        // 若目标点在相机后方，视为不可见
        if (cameraDepth < 0) {
            return null;
        }

        // —— 第四步：获取水平和垂直方向的半视角正切值 ——
        final double tanHalfFovX = FovCache.getTanHalfFovX(camera, screenWidth, screenHeight);
        final double tanHalfFovY = FovCache.getTanHalfFovY(camera, screenWidth, screenHeight);

        // —— 第五步：透视除法，转换为归一化设备坐标（NDC）——
        // NDC 范围：X 和 Y 均在 [-1, 1] 之间，其中 0 表示屏幕中心
        final double normalizedDeviceX = (cameraSpaceX / cameraDepth) / Math.max(tanHalfFovX, 0.01);
        final double normalizedDeviceY = (cameraSpaceY / cameraDepth) / Math.max(tanHalfFovY, 0.01);

        // —— 第六步：将 NDC 映射到屏幕像素坐标 ——
        // NDC -1 对应屏幕左/上边缘，NDC +1 对应右/下边缘
        // 公式：screenPos = halfSize * (1 - ndc)
        final double halfScreenWidth = screenWidth / 2.0;
        final double halfScreenHeight = screenHeight / 2.0;

        final double screenX = halfScreenWidth * (1.0 - normalizedDeviceX);
        final double screenY = halfScreenHeight * (1.0 - normalizedDeviceY);

        // 若屏幕坐标超出屏幕范围，则视为不可见
        if (screenX < 0 || screenX > screenWidth || screenY < 0 || screenY > screenHeight) {
            return null;
        }

        // 把 FOV 缩放直接烘焙进 cameraDepth（除以放大系数），下游自动感知，无需再单独乘放大系数
        // cameraDepth 语义随之变为"缩放后的可见深度"
        return new ScreenPosition((float) screenX, (float) screenY,
                (float) (cameraDepth / FovCache.getZoomMagnification()));
    }
}
