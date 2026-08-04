package grainalcohol.lhv.common.dto.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SymbolConfig {
    private final boolean useGrouping;
    private final char groupingSeparator;
    private final char decimalSeparator;
    private final String exponentSeparator;
    private final String positivePrefix;
    private final String negativePrefix;
    private final String positiveSuffix;
    private final String negativeSuffix;

    public SymbolConfig(SymbolConfig other) {
        this.useGrouping = other.useGrouping;
        this.groupingSeparator = other.groupingSeparator;
        this.decimalSeparator = other.decimalSeparator;
        this.exponentSeparator = other.exponentSeparator;
        this.positivePrefix = other.positivePrefix;
        this.negativePrefix = other.negativePrefix;
        this.positiveSuffix = other.positiveSuffix;
        this.negativeSuffix = other.negativeSuffix;
    }

    public SymbolConfig copy() {
        return new SymbolConfig(this);
    }
}
