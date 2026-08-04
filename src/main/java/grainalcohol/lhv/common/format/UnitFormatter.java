package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import org.jetbrains.annotations.NotNull;

public class UnitFormatter extends DamageFormatter {
    public UnitFormatter(@NotNull FormatConfig formatConfig) {
        super(formatConfig);
    }

    @Override
    String applyFormat(DecimalValue value) {
        return this.unitFormat(value.asBigDecimal());
    }
}
