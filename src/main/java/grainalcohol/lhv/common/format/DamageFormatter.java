package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.FormatConfig;
import grainalcohol.lhv.common.enums.UnitSystem;
import grainalcohol.lhv.common.format.unit.Units;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public abstract class DamageFormatter {
    @Nullable
    private DecimalFormatSymbols symbols;
    @Nullable
    private DecimalFormat defaultDecimalFormat;
    @Nullable
    private DecimalFormat scientificDecimalFormat;

    public String format(FormatConfig formatConfig , BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0";

        return applyFormat(formatConfig, value);
    }

    abstract String applyFormat(FormatConfig formatConfig, BigDecimal value);

    private DecimalFormat getDefaultFormat(FormatConfig formatConfig) {
        if (this.defaultDecimalFormat == null) {
            createDefaultFormat(formatConfig);
        }
        return this.defaultDecimalFormat;
    }

    private DecimalFormat getScientificFormat(FormatConfig formatConfig) {
        if (this.scientificDecimalFormat == null) {
            createScientificFormat(formatConfig);
        }
        return this.scientificDecimalFormat;
    }

    protected String rawFormat(FormatConfig formatConfig, BigDecimal value) {
        return getDefaultFormat(formatConfig).format(value);
    }

    protected String unitFormat(FormatConfig formatConfig, BigDecimal value) {
        return Units.applyFormat(formatConfig.getUnitSystem(), value, getDefaultFormat(formatConfig));
    }

    protected String scientificFormat(FormatConfig formatConfig, BigDecimal value) {
        if (shouldScientificNotation(formatConfig, value)) {
            // value < 0.*1 || value > 10^n
            return getScientificFormat(formatConfig).format(value);
        } else {
            return getDefaultFormat(formatConfig).format(value);
        }
    }

    protected BigDecimal getUpperFormatBoundary(UnitSystem unitSystem) {
        return unitSystem.getSmallestIntegerUnit().getSize();
    }

    protected BigDecimal getLowerFormatBoundary(int decimalPlace) {
        return BigDecimal.ONE.movePointLeft(decimalPlace);
    }

    protected boolean shouldScientificNotation(FormatConfig formatConfig, BigDecimal value) {
        return value.abs().compareTo(getUpperFormatBoundary(formatConfig.getUnitSystem())) > 0
                || value.abs().compareTo(getLowerFormatBoundary(formatConfig.getRetainDecimalPlaces())) < 0;
    }

    private DecimalFormat createBaseFormat(FormatConfig formatConfig) {
        DecimalFormat decimalFormat = new DecimalFormat();

        if (this.symbols == null) {
            createSymbols(formatConfig);
        }
        decimalFormat.setDecimalFormatSymbols(this.symbols);
        decimalFormat.setGroupingUsed(formatConfig.isUseGrouping());
        decimalFormat.setPositivePrefix(formatConfig.getPositivePrefix());
        decimalFormat.setNegativePrefix(formatConfig.getNegativePrefix());
        decimalFormat.setPositiveSuffix(formatConfig.getPositiveSuffix());
        decimalFormat.setNegativeSuffix(formatConfig.getNegativeSuffix());
        decimalFormat.setRoundingMode(formatConfig.getRoundingMode());
        return decimalFormat;
    }

    private void createDefaultFormat(FormatConfig formatConfig) {
        var decimalFormat = createBaseFormat(formatConfig);
        decimalFormat.applyPattern(defaultPattern(formatConfig.getRetainDecimalPlaces()));
        this.defaultDecimalFormat = decimalFormat;
    }

    private void createScientificFormat(FormatConfig formatConfig) {
        var decimalFormat = createBaseFormat(formatConfig);
        decimalFormat.applyPattern(scientificPattern(formatConfig.getRetainDecimalPlaces()));
        this.scientificDecimalFormat = decimalFormat;
    }

    protected String defaultPattern(int decimalPlace) {
        // looks like this: #,##0.####
        return decimalPlace <= 0 ? "0" : "#,##0." + "#".repeat(decimalPlace);
    }

    protected String scientificPattern(int decimalPlace) {
        // looks like this: 0.####E0
        return decimalPlace <= 0 ? "0" : "0." + "#".repeat(decimalPlace) + "E0";
    }

    private void createSymbols(FormatConfig formatConfig) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(formatConfig.getGroupingSeparator());
        symbols.setDecimalSeparator(formatConfig.getDecimalSeparator());
        symbols.setExponentSeparator(formatConfig.getExponentSeparator());
        this.symbols = symbols;
    }
}
