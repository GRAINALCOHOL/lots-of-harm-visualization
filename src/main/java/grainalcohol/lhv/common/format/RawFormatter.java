package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import org.jetbrains.annotations.NotNull;

public class RawFormatter extends DamageFormatter {
    public RawFormatter(@NotNull FormatConfig formatConfig) {
        super(formatConfig);
    }

    @Override
    String applyFormat(DecimalValue value) {
        return this.rawFormat(value.asBigDecimal());
    }
}
