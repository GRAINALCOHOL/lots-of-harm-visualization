package grainalcohol.lhv.common.format;

import grainalcohol.lhv.common.dto.FormatConfig;

import java.math.BigDecimal;

public class AutoFormatter extends DamageFormatter {
    public static final AutoFormatter INSTANCE = new AutoFormatter();

    /**
     * 将value根据绝对值自动确定格式化方案
     * <p>abs in [0, lower): 科学计数法（极小值）</p>
     * <p>abs in [lower, upper]: 不格式化（一般值）</p>
     * <p>abs in (upper, maxSize): 单位格式化（较大值）</p>
     * <p>abs in [maxSize, +∞): 科学计数法（极大值）</p>
     *
     * @param formatConfig 格式化设置
     * @param value        需要格式化的数字
     * @return 格式化结果
     */
    @Override
    String applyFormat(FormatConfig formatConfig, BigDecimal value) {
        int dp = formatConfig.getRetainDecimalPlaces();
        BigDecimal absValue = value.abs();
        if (
                // 0.*1 <= value <= 10^n
                absValue.compareTo(this.getLowerFormatBoundary(dp)) >= 0
                &&
                absValue.compareTo(this.getUpperFormatBoundary(formatConfig.getUnitSystem())) <= 0
        ) {
            return this.rawFormat(formatConfig, value);
        }

        if (
                // value < 0.*1 || value >= maxUnit * 10000
                absValue.compareTo(formatConfig.getUnitSystem().getMaxUnit().getSize().multiply(BigDecimal.valueOf(10000))) >= 0
                ||
                absValue.compareTo(this.getLowerFormatBoundary(dp)) < 0
        ) {
            return this.scientificFormat(formatConfig, value);
        }

        // 10^n < value < maxUnit * 10000
        return this.unitFormat(formatConfig, value);
    }

    public static AutoFormatter getInstance() {
        return INSTANCE;
    }
}
