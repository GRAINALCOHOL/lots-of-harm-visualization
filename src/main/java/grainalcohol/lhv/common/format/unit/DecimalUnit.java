package grainalcohol.lhv.common.format.unit;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
@AllArgsConstructor
public class DecimalUnit {
    private final BigDecimal size;
    private final String suffix;

    public String format(
            BigDecimal value,
            DecimalFormat decimalFormat
    ) {
        BigDecimal dividedValue = value.divide(getSize(), 20, decimalFormat.getRoundingMode());
        return decimalFormat.format(dividedValue) + getSuffix();
    }
}
