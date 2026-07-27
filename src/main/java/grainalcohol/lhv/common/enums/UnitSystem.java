package grainalcohol.lhv.common.enums;

import grainalcohol.lhv.common.format.unit.DecimalUnit;
import grainalcohol.lhv.common.format.unit.LongScaleUnits;
import grainalcohol.lhv.common.format.unit.MetricPrefixUnits;
import grainalcohol.lhv.common.format.unit.ShortScaleUnits;
import grainalcohol.lhv.common.format.unit.Units;

import java.util.function.Supplier;

public enum UnitSystem {
    METRIC_PREFIX(MetricPrefixUnits::getInstance),
    SHORT_SCALE(ShortScaleUnits::getInstance),
    LONG_SCALE(LongScaleUnits::getInstance)
    ;

    private final Supplier<Units> unitsSupplier;

    UnitSystem(Supplier<Units> unitsSupplier) {
        this.unitsSupplier = unitsSupplier;
    }

    public DecimalUnit getUnit(int index) {
        return getUnits()[index];
    }

    public DecimalUnit getSmallestIntegerUnit() {
        return getUnitInstance().getSmallestIntegerUnit();
    }

    public DecimalUnit getReversedUnit(int reverseIndex) {
        return getUnits()[getUnits().length - 1 - reverseIndex];
    }

    public DecimalUnit[] getUnits() {
        return getUnitInstance().getUnits();
    }

    public DecimalUnit getMaxUnit() {
        return getUnitInstance().getMaxUnit();
    }

    public DecimalUnit getMinUnit() {
        return getUnitInstance().getMinUnit();
    }

    private Units getUnitInstance() {
        return unitsSupplier.get();
    }
}
