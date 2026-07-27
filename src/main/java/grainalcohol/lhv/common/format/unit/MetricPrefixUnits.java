package grainalcohol.lhv.common.format.unit;

import java.util.Arrays;

public class MetricPrefixUnits implements Units {
    public static final MetricPrefixUnits INSTANCE = new MetricPrefixUnits();

    public static final DecimalUnit YOTTA = new DecimalUnit(Units.ofPow10(24), "Y");
    public static final DecimalUnit ZETTA = new DecimalUnit(Units.ofPow10(21), "Z");
    public static final DecimalUnit EXA   = new DecimalUnit(Units.ofPow10(18), "E");
    public static final DecimalUnit PETA  = new DecimalUnit(Units.ofPow10(15), "P");
    public static final DecimalUnit TERA  = new DecimalUnit(Units.ofPow10(12), "T");
    public static final DecimalUnit GIGA  = new DecimalUnit(Units.ofPow10(9), "G");
    public static final DecimalUnit MEGA  = new DecimalUnit(Units.ofPow10(6), "M");
    public static final DecimalUnit KILO  = new DecimalUnit(Units.ofPow10(3), "k");

    public static final DecimalUnit MILLI = new DecimalUnit(Units.ofPow10(-3), "m");
    public static final DecimalUnit MICRO = new DecimalUnit(Units.ofPow10(-6), "µ");
    public static final DecimalUnit NANO  = new DecimalUnit(Units.ofPow10(-9), "n");
    public static final DecimalUnit PICO  = new DecimalUnit(Units.ofPow10(-12), "p");
    public static final DecimalUnit FEMTO = new DecimalUnit(Units.ofPow10(-15), "f");
    public static final DecimalUnit ATTO  = new DecimalUnit(Units.ofPow10(-18), "a");
    public static final DecimalUnit ZEPTO = new DecimalUnit(Units.ofPow10(-21), "z");
    public static final DecimalUnit YOCTO = new DecimalUnit(Units.ofPow10(-24), "y");

    private static final DecimalUnit[] UNITS = {
            YOTTA, ZETTA, EXA, PETA, TERA, GIGA, MEGA, KILO,
            MILLI, MICRO, NANO, PICO, FEMTO, ATTO, ZEPTO, YOCTO
    };

    private MetricPrefixUnits() {}

    @Override
    public DecimalUnit[] getUnits() {
        return Arrays.copyOf(UNITS, UNITS.length);
    }

    @Override
    public DecimalUnit getSmallestIntegerUnit() {
        return KILO;
    }

    public static MetricPrefixUnits getInstance() {
        return INSTANCE;
    }
}
