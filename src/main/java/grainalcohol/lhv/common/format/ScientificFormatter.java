package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.FormatConfig;

import java.math.BigDecimal;

public class ScientificFormatter extends DamageFormatter {
    public static final ScientificFormatter INSTANCE = new ScientificFormatter();

    @Override
    String applyFormat(FormatConfig formatConfig, BigDecimal value) {
        return this.scientificFormat(formatConfig, value);
    }

    public static ScientificFormatter getInstance() {
        return INSTANCE;
    }
}
