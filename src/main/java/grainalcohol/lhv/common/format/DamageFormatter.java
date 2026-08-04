package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import grainalcohol.lhv.common.dto.config.SymbolConfig;
import grainalcohol.lhv.common.enums.UnitSystem;
import grainalcohol.lhv.common.format.unit.Units;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public abstract class DamageFormatter {
    @NotNull
    private DecimalFormatSymbols symbols;
    @NotNull
    private DecimalFormat defaultDecimalFormat;
    @NotNull
    private DecimalFormat scientificDecimalFormat;
    @NotNull
    protected final FormatConfig formatConfig;

    public DamageFormatter(@NotNull FormatConfig formatConfig) {
        this.formatConfig = formatConfig;
        this.createSymbols(formatConfig.getSymbolConfig());
        this.createDefaultFormat();
        this.createScientificFormat();
    }

    public String format(DecimalValue value) {
        if (value.isInfinite()) return formatConfig.getInfinityDisplay();
        if (value.isNaN()) return formatConfig.getNanDisplay();
        if (value.isZero()) return "0";

        return applyFormat(value);
    }

    /**
     * 实现此方法时，value 不会是特殊值
     * @param value 十进制数值
     * @return 格式化结果
     */
    abstract String applyFormat(DecimalValue value);

    protected String rawFormat(BigDecimal value) {
        return defaultDecimalFormat.format(value);
    }

    protected String unitFormat(BigDecimal value) {
        return Units.applyFormat(formatConfig.getUnitSystem(), value, defaultDecimalFormat);
    }

    protected String scientificFormat(BigDecimal value) {
        if (shouldScientificNotation(value)) {
            // value < 0.*1 || value > 10^n
            return scientificDecimalFormat.format(value);
        } else {
            return defaultDecimalFormat.format(value);
        }
    }

    protected BigDecimal getMaxFormatBoundary(UnitSystem unitSystem) {
        return unitSystem.getMaxUnit().getSize().multiply(BigDecimal.valueOf(10000));
    }

    protected BigDecimal getPositiveFormatBoundary(UnitSystem unitSystem) {
        return unitSystem.getSmallestIntegerUnit().getSize();
    }

    protected BigDecimal getDecimalFormatBoundary(int decimalPlace) {
        return BigDecimal.ONE.movePointLeft(decimalPlace);
    }

    protected boolean shouldScientificNotation(BigDecimal value) {
        return value.abs().compareTo(getPositiveFormatBoundary(formatConfig.getUnitSystem())) > 0
                || value.abs().compareTo(getDecimalFormatBoundary(formatConfig.getRetainDecimalPlaces())) < 0;
    }

    private void createSymbols(SymbolConfig symbolConfig) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(symbolConfig.getGroupingSeparator());
        symbols.setDecimalSeparator(symbolConfig.getDecimalSeparator());
        symbols.setExponentSeparator(symbolConfig.getExponentSeparator());
        this.symbols = symbols;
    }

    private DecimalFormat createBaseFormat() {
        DecimalFormat decimalFormat = new DecimalFormat();

        var symbolConfig = formatConfig.getSymbolConfig();
        decimalFormat.setDecimalFormatSymbols(this.symbols);
        decimalFormat.setGroupingUsed(symbolConfig.isUseGrouping());
        decimalFormat.setPositivePrefix(symbolConfig.getPositivePrefix());
        decimalFormat.setNegativePrefix(symbolConfig.getNegativePrefix());
        decimalFormat.setPositiveSuffix(symbolConfig.getPositiveSuffix());
        decimalFormat.setNegativeSuffix(symbolConfig.getNegativeSuffix());
        decimalFormat.setRoundingMode(formatConfig.getRoundingMode());
        return decimalFormat;
    }

    private void createDefaultFormat() {
        var decimalFormat = createBaseFormat();
        decimalFormat.applyPattern(defaultPattern(formatConfig.getRetainDecimalPlaces()));
        this.defaultDecimalFormat = decimalFormat;
    }

    private void createScientificFormat() {
        var decimalFormat = createBaseFormat();
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
}
