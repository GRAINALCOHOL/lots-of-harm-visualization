package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import org.jetbrains.annotations.NotNull;

public class ScientificFormatter extends DamageFormatter {
    public ScientificFormatter(@NotNull FormatConfig formatConfig) {
        super(formatConfig);
    }

    @Override
    String applyFormat(DecimalValue value) {
        return this.scientificFormat(value.asBigDecimal());
    }
}
