package grainalcohol.lhv.common.enums;

import grainalcohol.lhv.common.dto.config.FormatConfig;
import grainalcohol.lhv.common.format.*;

import java.util.function.Function;
import java.util.function.Supplier;

public enum FormatMode {
    SCIENTIFIC(ScientificFormatter::new),
    UNIT(UnitFormatter::new),
    AUTO(AutoFormatter::new),
    RAW(RawFormatter::new);

    private final Function<FormatConfig, DamageFormatter> formatterFactory;

    FormatMode(Function<FormatConfig, DamageFormatter> formatterFactory) {
        this.formatterFactory = formatterFactory;
    }

    public DamageFormatter createFormatter(FormatConfig formatConfig) {
        return formatterFactory.apply(formatConfig);
    }
}
