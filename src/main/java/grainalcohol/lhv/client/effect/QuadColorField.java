package grainalcohol.lhv.client.effect;

import grainalcohol.lhv.common.util.ColorUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuadColorField {
    // 颜色是RGB，没有Alpha通道
    private int originalRgb;
    private int leftTop;
    private int rightTop;
    private int leftBottom;
    private int rightBottom;

    private QuadColorField(QuadColorField other) {
        this(other.originalRgb, other.leftTop, other.rightTop, other.leftBottom, other.rightBottom);
    }

    private QuadColorField(int rgb) {
        this.leftTop = rgb;
        this.rightTop = rgb;
        this.leftBottom = rgb;
        this.rightBottom = rgb;
    }

    public QuadColorField copy() {
        return new QuadColorField(this);
    }

    public static QuadColorField pure(int rgb) {
        return new QuadColorField(rgb);
    }

    public static QuadColorField verticalGradient(int rgb) {
        // 上面变亮40%
        // 下面变亮0%
        return new QuadColorField(
                rgb,
                ColorUtil.brightness(rgb, 0.4f),
                ColorUtil.brightness(rgb, 0.4f),
                rgb, rgb
        );
    }

    public void blend(QuadColorField colorField, float factor) {
        if (factor <= 0) return;
        this.leftTop = ColorUtil.lerp(this.leftTop, colorField.leftTop, factor);
        this.rightTop = ColorUtil.lerp(this.rightTop, colorField.rightTop, factor);
        this.leftBottom = ColorUtil.lerp(this.leftBottom, colorField.leftBottom, factor);
        this.rightBottom = ColorUtil.lerp(this.rightBottom, colorField.rightBottom, factor);
    }

    public void blend(int rgb, float factor) {
        if (factor <= 0) return;
        this.leftTop = ColorUtil.lerp(this.leftTop, rgb, factor);
        this.rightTop = ColorUtil.lerp(this.rightTop, rgb, factor);
        this.leftBottom = ColorUtil.lerp(this.leftBottom, rgb, factor);
        this.rightBottom = ColorUtil.lerp(this.rightBottom, rgb, factor);
    }

    public int lt() {
        return leftTop;
    }

    public int rt() {
        return rightTop;
    }

    public int lb() {
        return leftBottom;
    }

    public int rb() {
        return rightBottom;
    }
}
