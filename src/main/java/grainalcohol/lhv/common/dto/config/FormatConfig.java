package grainalcohol.lhv.common.dto.config;

import grainalcohol.lhv.common.enums.FormatMode;
import grainalcohol.lhv.common.enums.UnitSystem;
import grainalcohol.lhv.common.format.DamageFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public class FormatConfig {
    private final FormatMode formatMode;
    private final UnitSystem unitSystem;
    private final RoundingMode roundingMode;
    private final int retainDecimalPlaces;
    private final String infinityDisplay;
    private final String nanDisplay;
    private final SymbolConfig symbolConfig;

    private FormatConfig(FormatConfig other) {
        this.formatMode = other.formatMode;
        this.unitSystem = other.unitSystem;
        this.roundingMode = other.roundingMode;
        this.retainDecimalPlaces = other.retainDecimalPlaces;
        this.infinityDisplay = other.infinityDisplay;
        this.nanDisplay = other.nanDisplay;
        this.symbolConfig = other.symbolConfig.copy();
    }

    public DamageFormatter createFormatter() {
        return formatMode.createFormatter(this);
    }

    public FormatConfig copy() {
        return new FormatConfig(this);
    }
}
