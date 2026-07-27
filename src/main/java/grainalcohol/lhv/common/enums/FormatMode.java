package grainalcohol.lhv.common.enums;

import grainalcohol.lhv.common.format.*;

import java.util.function.Supplier;

public enum FormatMode {
    SCIENTIFIC(ScientificFormatter::new),
    UNIT(UnitFormatter::new),
    AUTO(AutoFormatter::new),
    RAW(RawFormatter::new);

    private final Supplier<DamageFormatter> formatterSupplier;

    FormatMode(Supplier<DamageFormatter> formatterSupplier) {
        this.formatterSupplier = formatterSupplier;
    }

    public DamageFormatter createFormatter() {
        return formatterSupplier.get();
    }
}
