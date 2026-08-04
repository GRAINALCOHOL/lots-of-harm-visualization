package grainalcohol.lhv.common.dto.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OutlineSetting {
    private final boolean enabled;
    private final int color;
    private final float width;

    public OutlineSetting(OutlineSetting other) {
        this.enabled = other.enabled;
        this.color = other.color;
        this.width = other.width;
    }

    public OutlineSetting copy() {
        return new OutlineSetting(this);
    }
}
