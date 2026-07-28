package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.enums.SourceType;
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

    public String format(SourceType sourceType, BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0";

        return applyFormat(sourceType, value);
    }

    abstract String applyFormat(SourceType sourceType, BigDecimal value);

    private DecimalFormat getDefaultFormat(SourceType sourceType) {
        if (this.defaultDecimalFormat == null) {
            createDefaultFormat(sourceType);
        }
        return this.defaultDecimalFormat;
    }

    private DecimalFormat getScientificFormat(SourceType sourceType) {
        if (this.scientificDecimalFormat == null) {
            createScientificFormat(sourceType);
        }
        return this.scientificDecimalFormat;
    }

    protected String rawFormat(SourceType sourceType, BigDecimal value) {
        return getDefaultFormat(sourceType).format(value);
    }

    protected String unitFormat(SourceType sourceType, BigDecimal value) {
        return Units.applyFormat(sourceType, value, getDefaultFormat(sourceType));
    }

    protected String scientificFormat(SourceType sourceType, BigDecimal value) {
        if (shouldScientificNotation(sourceType, value)) {
            // value < 0.*1 || value > 10^n
            return getScientificFormat(sourceType).format(value);
        } else {
            return getDefaultFormat(sourceType).format(value);
        }
    }

    protected BigDecimal getUpperFormatBoundary(SourceType sourceType) {
        return sourceType.getConfig().getUnitSystem().getSmallestIntegerUnit().getSize();
    }

    protected BigDecimal getLowerFormatBoundary(int decimalPlace) {
        return BigDecimal.ONE.movePointLeft(decimalPlace);
    }

    protected boolean shouldScientificNotation(SourceType sourceType, BigDecimal value) {
        return value.abs().compareTo(getUpperFormatBoundary(sourceType)) > 0 || value.abs().compareTo(getLowerFormatBoundary(sourceType.getConfig().getRetainDecimalPlaces())) < 0;
    }

    private DecimalFormat createBaseFormat(SourceType sourceType) {
        DecimalFormat decimalFormat = new DecimalFormat();

        var config = sourceType.getConfig();
        if (this.symbols == null) {
            createSymbols(sourceType);
        }
        decimalFormat.setDecimalFormatSymbols(this.symbols);
        decimalFormat.setGroupingUsed(config.isUseGrouping());
        decimalFormat.setPositivePrefix(config.getPositivePrefix());
        decimalFormat.setNegativePrefix(config.getNegativePrefix());
        decimalFormat.setPositiveSuffix(config.getPositiveSuffix());
        decimalFormat.setNegativeSuffix(config.getNegativeSuffix());
        decimalFormat.setRoundingMode(config.getRoundingMode());
        return decimalFormat;
    }

    private void createDefaultFormat(SourceType sourceType) {
        var decimalFormat = createBaseFormat(sourceType);
        decimalFormat.applyPattern(defaultPattern(sourceType.getConfig().getRetainDecimalPlaces()));
        this.defaultDecimalFormat = decimalFormat;
    }

    private void createScientificFormat(SourceType sourceType) {
        var decimalFormat = createBaseFormat(sourceType);
        decimalFormat.applyPattern(scientificPattern(sourceType.getConfig().getRetainDecimalPlaces()));
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

    private void createSymbols(SourceType sourceType) {
        var config = sourceType.getConfig();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(config.getGroupingSeparator());
        symbols.setDecimalSeparator(config.getDecimalSeparator());
        symbols.setExponentSeparator(config.getExponentSeparator());
        this.symbols = symbols;
    }
}
