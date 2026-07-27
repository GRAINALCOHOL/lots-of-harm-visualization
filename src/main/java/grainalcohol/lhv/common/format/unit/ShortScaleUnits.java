package grainalcohol.lhv.common.format.unit;

import java.util.Arrays;

public class ShortScaleUnits implements Units {
    public static final ShortScaleUnits INSTANCE = new ShortScaleUnits();

    public static final DecimalUnit KILO = new DecimalUnit(Units.ofPow10(3), "K");
    public static final DecimalUnit MILLION = new DecimalUnit(Units.ofPow10(6), "M");
    public static final DecimalUnit BILLION = new DecimalUnit(Units.ofPow10(9), "B");
    public static final DecimalUnit TRILLION = new DecimalUnit(Units.ofPow10(12), "T");
    public static final DecimalUnit QUADRILLION = new DecimalUnit(Units.ofPow10(15), "Qa");
    public static final DecimalUnit QUINTILLION = new DecimalUnit(Units.ofPow10(18), "Qi");
    public static final DecimalUnit SEXTILLION = new DecimalUnit(Units.ofPow10(21), "Sx");
    public static final DecimalUnit SEPTILLION = new DecimalUnit(Units.ofPow10(24), "Sp");
    public static final DecimalUnit OCTILLION = new DecimalUnit(Units.ofPow10(27), "Oc");
    public static final DecimalUnit NONILLION = new DecimalUnit(Units.ofPow10(30), "No");
    public static final DecimalUnit DECILLION = new DecimalUnit(Units.ofPow10(33), "Dc");

    private static final DecimalUnit[] UNITS = {
            DECILLION, NONILLION, OCTILLION, SEPTILLION, SEXTILLION,
            QUINTILLION, QUADRILLION, TRILLION, BILLION, MILLION, KILO
    };

    private ShortScaleUnits() {}

    @Override
    public DecimalUnit[] getUnits() {
        return Arrays.copyOf(UNITS, UNITS.length);
    }

    @Override
    public DecimalUnit getSmallestIntegerUnit() {
        return KILO;
    }

    public static ShortScaleUnits getInstance() {
        return INSTANCE;
    }
}
