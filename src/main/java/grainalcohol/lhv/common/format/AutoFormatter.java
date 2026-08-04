package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.DecimalValue;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import org.jetbrains.annotations.NotNull;

public class AutoFormatter extends DamageFormatter {
    public AutoFormatter(@NotNull FormatConfig formatConfig) {
        super(formatConfig);
    }

    /**
     * 将value根据绝对值自动确定格式化方案
     * <p>abs in [0, lower): 科学计数法（极小值）</p>
     * <p>abs in [lower, upper]: 不格式化（一般值）</p>
     * <p>abs in (upper, maxSize): 单位格式化（较大值）</p>
     * <p>abs in [maxSize, +∞): 科学计数法（极大值）</p>
     *
     * @param value@return 格式化结果
     */
    @Override
    String applyFormat(DecimalValue value) {
        int dp = formatConfig.getRetainDecimalPlaces();
        var absValue = value.abs();
        if (
                // 0.*1 <= value <= 10^n
                !absValue.isSmallerThan(this.getDecimalFormatBoundary(dp))
                &&
                !absValue.isBiggerThan(this.getPositiveFormatBoundary(formatConfig.getUnitSystem()))
        ) {
            return this.rawFormat(value.asBigDecimal());
        }

        if (
                // value < 0.*1 || value >= maxUnit * 10000
                !absValue.isSmallerThan(this.getMaxFormatBoundary(formatConfig.getUnitSystem()))
                ||
                absValue.isSmallerThan(this.getDecimalFormatBoundary(dp))
        ) {
            return this.scientificFormat(value.asBigDecimal());
        }

        // 10^n < value < maxUnit * 10000
        return this.unitFormat(value.asBigDecimal());
    }
}
