package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.enums.SourceType;

import java.math.BigDecimal;

public class RawFormatter extends DamageFormatter {
    public static final RawFormatter INSTANCE = new RawFormatter();

    @Override
    String applyFormat(SourceType sourceType, BigDecimal value) {
        return this.rawFormat(sourceType, value);
    }

    public static RawFormatter getInstance() {
        return INSTANCE;
    }
}
