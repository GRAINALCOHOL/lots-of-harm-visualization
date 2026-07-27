package grainalcohol.lhv.common.format.unit;

import java.util.Arrays;

public class LongScaleUnits implements Units {
    public static final LongScaleUnits INSTANCE = new LongScaleUnits();

    public static final DecimalUnit MILLION = new DecimalUnit(Units.ofPow10(6), "M");
    public static final DecimalUnit BILLION = new DecimalUnit(Units.ofPow10(12), "B");
    public static final DecimalUnit TRILLION = new DecimalUnit(Units.ofPow10(18), "T");
    public static final DecimalUnit QUADRILLION = new DecimalUnit(Units.ofPow10(24), "Qa");
    public static final DecimalUnit QUINTILLION = new DecimalUnit(Units.ofPow10(30), "Qi");

    private static final DecimalUnit[] UNITS = {
            QUINTILLION, QUADRILLION, TRILLION, BILLION, MILLION
    };

    private LongScaleUnits() {}

    @Override
    public DecimalUnit[] getUnits() {
        return Arrays.copyOf(UNITS, UNITS.length);
    }

    @Override
    public DecimalUnit getSmallestIntegerUnit() {
        return MILLION;
    }

    public static LongScaleUnits getInstance() {
        return INSTANCE;
    }
}
