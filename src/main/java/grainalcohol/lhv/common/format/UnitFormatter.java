package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.enums.SourceType;

import java.math.BigDecimal;

public class UnitFormatter extends DamageFormatter {
    public static final UnitFormatter INSTANCE = new UnitFormatter();

    @Override
    String applyFormat(SourceType sourceType, BigDecimal value) {
        return this.unitFormat(sourceType, value);
    }

    public static UnitFormatter getInstance() {
        return INSTANCE;
    }
}
