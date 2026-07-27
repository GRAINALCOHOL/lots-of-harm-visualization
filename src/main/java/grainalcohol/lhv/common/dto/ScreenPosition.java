package grainalcohol.lhv.common.dto;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public record ScreenPosition(
        float x,
        float y,
        float cameraDepth
) {
    public ScreenPosition random(float rangeX, float rangeY) {
        float dx = (float) ((Math.random() - 0.5) * rangeX);
        float dy = (float) ((Math.random() - 0.5) * rangeY);
        return new ScreenPosition(x + dx, y + dy, cameraDepth);
    }

    public ScreenPosition random(float range) {
        return random(range, range);
    }

    /**
     * 偏移当前屏幕位置，返回新的 ScreenPosition，保留原对象的cameraDepth
     * @param other 其他屏幕位置对象
     * @return 应用偏移后的屏幕位置
     */
    public ScreenPosition offset(ScreenPosition other) {
        return new ScreenPosition(x + other.x, y + other.y, cameraDepth);
    }

    public ScreenPosition offset(float dx, float dy) {
        return new ScreenPosition(x + dx, y + dy, cameraDepth);
    }

    public ScreenPosition offset(Vec2f vec2f) {
        return new ScreenPosition(x + vec2f.x, y + vec2f.y, cameraDepth);
    }

    /**
     * 偏移当前屏幕位置，偏移量随深度线性衰减。
     * <p>
     * 距离越远（cameraDepth 越大），偏移量越小。
     * 衰减公式：{@code factor = clamp(refDist / cameraDepth, 0, 1)}
     *
     * @param dx      参考距离下的水平偏移量（像素）
     * @param dy      参考距离下的垂直偏移量（像素）
     * @param refDist 参考距离，此距离下偏移量为 100%
     * @return 应用衰减偏移后的屏幕位置
     */
    public ScreenPosition offsetWithDepth(float dx, float dy, float refDist) {
        if (dx == 0f && dy == 0f) return this;
        float factor = (float) MathHelper.clamp(refDist / Math.max(cameraDepth, 0.01), 0f, 1f);
        return new ScreenPosition(x + dx * factor, y + dy * factor, cameraDepth);
    }

    public ScreenPosition offsetWithDepth(float dx, float dy) {
        return offsetWithDepth(dx, dy, 4f);
    }

    public ScreenPosition offsetWithDepth(Vec2f vec2f, float refDist) {
        return offsetWithDepth(vec2f.x, vec2f.y, refDist);
    }

    public ScreenPosition offsetWithDepth(Vec2f vec2f) {
        return offsetWithDepth(vec2f.x, vec2f.y);
    }

    public float distanceTo(ScreenPosition other) {
        return Math.abs(cameraDepth - other.cameraDepth);
    }

    /**
     * 根据相机深度计算缩放倍率。
     * <p>
     * 使用反比映射：{@code setScale = clamp(refDist / cameraDepth, minScale, maxScale)}
     *
     * @param refDist  参考距离，此距离下 setScale = 1.0
     * @param minScale 最小缩放（远处下限）
     * @param maxScale 最大缩放（近处上限）
     * @return 缩放倍率
     */
    public float depthToScale(float refDist, float minScale, float maxScale) {
        float raw = (float) (refDist / Math.max(cameraDepth, 0.01));
        return MathHelper.clamp(raw, minScale, maxScale);
    }

    public float depthToScale(float refDist) {
        return depthToScale(refDist, 0.6f, 2.5f);
    }

    public float depthToScale() {
        return depthToScale(10f);
    }

    public Vec2f asVec2f() {
        return new Vec2f(x, y);
    }

    public float depthToAlpha(float disToAlphaRef, float minAlpha, float maxAlpha) {
        float raw = (float) (Math.max(cameraDepth, 0.01) / disToAlphaRef);
        return MathHelper.clamp(MathHelper.clamp(raw, 0f, 1f), minAlpha, maxAlpha);
    }

    public float depthToAlpha(float disToAlphaRef) {
        return depthToAlpha(disToAlphaRef, 0.8f, 1.0f);
    }

    public float depthToAlpha() {
        return depthToAlpha(2f);
    }
}
