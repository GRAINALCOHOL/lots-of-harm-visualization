package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.FormatConfig;

import java.math.BigDecimal;

public class RawFormatter extends DamageFormatter {
    public static final RawFormatter INSTANCE = new RawFormatter();

    @Override
    String applyFormat(FormatConfig formatConfig, BigDecimal value) {
        return this.rawFormat(formatConfig, value);
    }

    public static RawFormatter getInstance() {
        return INSTANCE;
    }
}
