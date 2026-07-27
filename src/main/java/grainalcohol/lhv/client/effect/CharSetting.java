package grainalcohol.lhv.client.effect;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CharSetting {
    public float x;
    public float y;
    public float rot;
    public float widthScale;
    public float heightScale;
    public int codePoint;
    public float alpha;

    public QuadColorField colorField;

    public final int index;

    public CharSetting copy() {
        return new CharSetting(
                this.x,
                this.y,
                this.rot,
                this.widthScale,
                this.heightScale,
                this.codePoint,
                this.alpha,
                this.colorField.copy(),
                this.index
        );
    }
}
