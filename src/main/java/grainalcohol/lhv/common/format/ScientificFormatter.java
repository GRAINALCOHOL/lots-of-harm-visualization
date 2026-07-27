package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.enums.SourceType;

import java.math.BigDecimal;

public class ScientificFormatter extends DamageFormatter {
    public static final ScientificFormatter INSTANCE = new ScientificFormatter();

    @Override
    String applyFormat(SourceType sourceType, BigDecimal value) {
        return this.scientificFormat(sourceType, value);
    }

    public static ScientificFormatter getInstance() {
        return INSTANCE;
    }
}
