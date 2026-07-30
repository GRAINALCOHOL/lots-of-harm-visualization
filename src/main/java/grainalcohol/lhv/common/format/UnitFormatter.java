package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.FormatConfig;

import java.math.BigDecimal;

public class UnitFormatter extends DamageFormatter {
    public static final UnitFormatter INSTANCE = new UnitFormatter();

    @Override
    String applyFormat(FormatConfig formatConfig, BigDecimal value) {
        return this.unitFormat(formatConfig, value);
    }

    public static UnitFormatter getInstance() {
        return INSTANCE;
    }
}
