package grainalcohol.lhv.common.format.unit;

import grainalcohol.lhv.common.enums.UnitSystem;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public interface Units {
    DecimalUnit UNIT = new DecimalUnit(ofPow10(0), "");

    DecimalUnit[] getUnits();

    default DecimalUnit getMaxUnit() {
        return getUnits()[0];
    }

    default DecimalUnit getMinUnit() {
        return getUnits()[getUnits().length - 1];
    }

    DecimalUnit getSmallestIntegerUnit();

    static BigDecimal ofPow10(int pow) {
        if (pow < 0) {
            return BigDecimal.ONE.movePointLeft(-pow);
        }
        return BigDecimal.TEN.pow(pow);
    }

    static String applyFormat(UnitSystem unitSystem, BigDecimal value, DecimalFormat decimalFormat) {
        for (DecimalUnit unit : unitSystem.getUnits()) {
            if (value.compareTo(unit.getSize()) >= 0) {
                return unit.format(value, decimalFormat);
            }
        }
        return UNIT.format(value, decimalFormat);
    }
}
