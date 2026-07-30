package grainalcohol.lhv.common.dto;

import grainalcohol.lhv.common.enums.FormatMode;
import grainalcohol.lhv.common.enums.UnitSystem;
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
    private final boolean useGrouping;
    private final char groupingSeparator;
    private final char decimalSeparator;
    private final String exponentSeparator;
    private final String positivePrefix;
    private final String negativePrefix;
    private final String positiveSuffix;
    private final String negativeSuffix;
}
