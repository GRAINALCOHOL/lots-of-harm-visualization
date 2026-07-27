package grainalcohol.lhv.common.util;

public class ColorUtil {
    /**
     * 判断给定的 RGB 颜色是否在视觉上偏亮（偏白）。
     * <p>
     * 使用 ITU-R BT.709 标准的相对亮度公式计算：
     * <pre>
     * L = 0.2126 × R + 0.7152 × G + 0.0722 × B
     * </pre>
     * 该系数来源于人眼对不同波长光的敏感度差异：
     * <ul>
     *   <li>绿色（G）权重最高（0.7152），因为人眼对绿光最敏感</li>
     *   <li>红色（R）次之（0.2126）</li>
     *   <li>蓝色（B）最低（0.0722），因为人眼对蓝光最不敏感</li>
     * </ul>
     * 此公式也是 WCAG 2.0/2.1 无障碍标准中计算对比度的基础算法。
     *
     * @param rgb 24位 RGB 颜色值，格式为 0xRRGGBB
     * @return 如果计算出的亮度值大于 128（0-255 范围的中点），则认为颜色偏亮，返回 {@code true}
     * @see <a href="https://www.w3.org/TR/WCAG20/#relativeluminancedef">WCAG 2.0 相对亮度定义</a>
     * @see <a href="https://en.wikipedia.org/wiki/Rec._709">ITU-R BT.709</a>
     */
    public static boolean isLightColor(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return luminance > 128;
    }

    public static int lerp(int p1, int p2, float t) {
        int ar = (p1 >> 16) & 0xFF, ag = (p1 >> 8) & 0xFF, ab = p1 & 0xFF;
        int br = (p2 >> 16) & 0xFF, bg = (p2 >> 8) & 0xFF, bb = p2 & 0xFF;
        int r = Math.round(ar + (br - ar) * t);
        int g = Math.round(ag + (bg - ag) * t);
        int b = Math.round(ab + (bb - ab) * t);
        return (r << 16) | (g << 8) | b;
    }

    public static int brightness(int rgb, float factor) {
        if (factor < -1f || factor > 1f) {
            throw new IllegalArgumentException("Factor must be between -1 and 1");
        }

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        float newR = r + (255 - r) * factor;
        float newG = g + (255 - g) * factor;
        float newB = b + (255 - b) * factor;

        return ((int) newR << 16) | ((int) newG << 8) | (int) newB;
    }
}
