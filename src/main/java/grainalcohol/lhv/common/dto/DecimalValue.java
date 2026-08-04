package grainalcohol.lhv.common.dto;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>基于 IEEE 754 标准的可以表示特殊值的十进制数值类，支持正无穷、负无穷和 NaN。</p>
 * <p>有限数值使用 BigDecimal 存储，使用枚举表示特殊值。</p>
 * <p>注意：由于使用 BigDecimal 存储有限数值，在解析 {@code -0.0} 和 {@code +0.0} 后会丢失符号信息。</p>
 * @author GRAINALCOHOL
 * @since 2026-08-02
 */
public class DecimalValue {
    public enum Kind {
        FINITE,
        POSITIVE_INFINITY,
        NEGATIVE_INFINITY,
        NAN
    }

    @NotNull
    private Kind kind;
    @NotNull
    private BigDecimal finiteValue;

    private DecimalValue(@NotNull Kind kind, @NotNull BigDecimal finiteValue) {
        this.kind = kind;
        this.finiteValue = finiteValue;
    }

    public DecimalValue() {
        this(Kind.FINITE, BigDecimal.ZERO);
    }

    public DecimalValue(@NotNull Kind kind) {
        this(kind, BigDecimal.ZERO);
    }

    public DecimalValue(BigDecimal finiteValue) {
        this(Kind.FINITE, finiteValue);
    }

    public DecimalValue(String stringValue) {
        this(Kind.FINITE, new BigDecimal(stringValue));
    }

    public static DecimalValue finiteValue(double finiteValue) {
        if (Double.isInfinite(finiteValue)) {
            throw new IllegalArgumentException("Value cannot be infinite");
        } else if (Double.isNaN(finiteValue)) {
            throw new IllegalArgumentException("Value cannot be NaN");
        } else {
            return new DecimalValue(BigDecimal.valueOf(finiteValue));
        }
    }

    public static DecimalValue finiteValue(String stringValue) {
        return new DecimalValue(new BigDecimal(stringValue));
    }

    public static DecimalValue valueOf(double value) {
        if (Double.isInfinite(value)) {
            return new DecimalValue(value > 0 ? Kind.POSITIVE_INFINITY : Kind.NEGATIVE_INFINITY);
        } else if (Double.isNaN(value)) {
            return new DecimalValue(Kind.NAN);
        } else {
            return new DecimalValue(BigDecimal.valueOf(value));
        }
    }

    public void set(Kind kind, BigDecimal finiteValue) {
        this.kind = kind;
        this.finiteValue = finiteValue;
    }

    public void set(double value) {
        if (Double.isInfinite(value)) {
            this.kind = value > 0 ? Kind.POSITIVE_INFINITY : Kind.NEGATIVE_INFINITY;
            this.finiteValue = BigDecimal.ZERO;
        } else if (Double.isNaN(value)) {
            this.kind = Kind.NAN;
            this.finiteValue = BigDecimal.ZERO;
        } else {
            this.kind = Kind.FINITE;
            this.finiteValue = BigDecimal.valueOf(value);
        }
    }

    public void add(DecimalValue decimalValue) {
        if (decimalValue.isZero() || this.isZero()) return;

        if (decimalValue.isNaN() || this.isNaN()) {
            this.kind = Kind.NAN;
            return;
        }

        if (decimalValue.isInfinite()) {
            switch (this.kind) {
                case FINITE -> this.kind = decimalValue.kind;
                // inf + -inf = NaN
                case POSITIVE_INFINITY -> { if (decimalValue.kind == Kind.NEGATIVE_INFINITY) this.kind = Kind.NAN; }
                case NEGATIVE_INFINITY -> { if (decimalValue.kind == Kind.POSITIVE_INFINITY) this.kind = Kind.NAN; }
            }
            return;
        }

        if (this.isFinite()) {
            this.finiteValue = this.finiteValue.add(decimalValue.finiteValue);
        }
    }

    public void add(double value) {
        if (value == 0.0 || this.kind == Kind.NAN) return;

        if (Double.isNaN(value)) {
            this.kind = Kind.NAN;
            return;
        }

        if (Double.isInfinite(value)) {
            switch (this.kind) {
                case FINITE -> this.kind = value > 0 ? Kind.POSITIVE_INFINITY : Kind.NEGATIVE_INFINITY;
                // inf + -inf = NaN
                case POSITIVE_INFINITY -> { if (value < 0) this.kind = Kind.NAN; }
                case NEGATIVE_INFINITY -> { if (value > 0) this.kind = Kind.NAN; }
            }
            return;
        }

        if (this.isFinite()) {
            this.finiteValue = this.finiteValue.add(BigDecimal.valueOf(value));
        }
    }

    public boolean isFinite() {
        return this.kind == Kind.FINITE;
    }

    public boolean isInfinite() {
        return this.isPositiveInfinity() || this.isNegativeInfinity();
    }

    public boolean isPositiveInfinity() {
        return this.kind == Kind.POSITIVE_INFINITY;
    }

    public boolean isNegativeInfinity() {
        return this.kind == Kind.NEGATIVE_INFINITY;
    }

    public boolean isNaN() {
        return this.kind == Kind.NAN;
    }

    public boolean isZero() {
        return this.isFinite() && this.finiteValue.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.kind == Kind.POSITIVE_INFINITY || (this.isFinite() && this.finiteValue.compareTo(BigDecimal.ZERO) > 0);
    }

    public boolean isNegative() {
        return this.kind == Kind.NEGATIVE_INFINITY || (this.isFinite() && this.finiteValue.compareTo(BigDecimal.ZERO) < 0);
    }

    public boolean isBiggerThan(double value) {
        return this.isBiggerThan(DecimalValue.valueOf(value));
    }

    public boolean isBiggerThan(DecimalValue value) {
        if (this.isNaN() || value.isNaN()) return false;

        if (this.kind == value.kind) {
            if (this.isFinite()) {
                return this.finiteValue.compareTo(value.finiteValue) > 0;
            } else return false;
        } else {
            if (this.kind == Kind.NEGATIVE_INFINITY) return false;
            if (this.kind == Kind.POSITIVE_INFINITY) return true;
            if (value.kind == Kind.NEGATIVE_INFINITY) return true;
            if (value.kind == Kind.POSITIVE_INFINITY) return false;
            return this.finiteValue.compareTo(value.finiteValue) > 0;
        }
    }

    public boolean isBiggerThan(BigDecimal value) {
        if (this.isNaN()) return false;
        if (this.kind == Kind.POSITIVE_INFINITY) return true;
        if (this.kind == Kind.NEGATIVE_INFINITY) return false;
        return this.finiteValue.compareTo(value) > 0;
    }

    public boolean isBiggerThan(BigInteger value) {
        return this.isBiggerThan(new BigDecimal(value));
    }

    public boolean isSmallerThan(DecimalValue value) {
        if (this.isNaN() || value.isNaN()) return false;

        if (this.kind == value.kind) {
            if (this.isFinite()) {
                return this.finiteValue.compareTo(value.finiteValue) < 0;
            } else return false;
        } else {
            if (this.kind == Kind.NEGATIVE_INFINITY) return true;
            if (this.kind == Kind.POSITIVE_INFINITY) return false;
            if (value.kind == Kind.NEGATIVE_INFINITY) return false;
            if (value.kind == Kind.POSITIVE_INFINITY) return true;
            return this.finiteValue.compareTo(value.finiteValue) < 0;
        }
    }

    public boolean isSmallerThan(BigDecimal value) {
        if (this.isNaN()) return false;
        if (this.kind == Kind.POSITIVE_INFINITY) return false;
        if (this.kind == Kind.NEGATIVE_INFINITY) return true;
        return this.finiteValue.compareTo(value) < 0;
    }

    public boolean isSmallerThan(double value) {
        return this.isSmallerThan(DecimalValue.valueOf(value));
    }

    public DecimalValue abs() {
        if (this.isNaN()) return this;
        if (this.kind == Kind.NEGATIVE_INFINITY) return new DecimalValue(Kind.POSITIVE_INFINITY);
        if (this.isFinite()) {
            return new DecimalValue(this.finiteValue.abs());
        }
        return this;
    }

    public double asDouble() {
        return switch (this.kind) {
            case FINITE -> this.finiteValue.doubleValue();
            case POSITIVE_INFINITY -> Double.POSITIVE_INFINITY;
            case NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY;
            case NAN -> Double.NaN;
        };
    }

    public BigDecimal asBigDecimal() {
        if (this.isFinite()) {
            return this.finiteValue;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public int compareFiniteValue(DecimalValue other) {
        if (!this.isFinite() || !other.isFinite()) {
            throw new IllegalArgumentException("Both DecimalValue objects must be finite for comparison.");
        }

        return this.finiteValue.compareTo(other.finiteValue);
    }

    /**
     * <p>基于 IEEE 754 标准的比较方法，比较两个 DecimalValue 对象的数值是否相等，任意一方为 NaN 时返回 false。</p>
     * <p>注意：该方法仅比较数值大小，不考虑对象的引用是否相同。</p>
     * @param other 另一个 DecimalValue 对象
     * @return 数值是否相等
     */
    public boolean compare(DecimalValue other) {
        if (this.isNaN() || other.isNaN()) return false;

        if (this.kind == other.kind) {
            if (this.isFinite()) {
                return this.finiteValue.compareTo(other.finiteValue) == 0;
            } else return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.isFinite()) {
            return this.finiteValue.toString();
        } else if (this.kind == Kind.POSITIVE_INFINITY) {
            return "+Infinity";
        } else if (this.kind == Kind.NEGATIVE_INFINITY) {
            return "-Infinity";
        } else {
            return "NaN";
        }
    }

    /**
     * <p>比较有限值时使用 {@code compareTo()} 以避免因 BigDecimal 精度不同而导致的 equals 结果不一致问题。</p>
     * <p>仅比较对象内容是否一样，需要符合 IEEE 754 标准的数值比较请使用{@code compare()}</p>
     * @param obj 其他对象
     * @return 是否相同
     * @see compare(DecimalValue)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DecimalValue other)) return false;
        if (this.kind != other.kind) return false;
        if (this.isFinite()) {
            return this.finiteValue.compareTo(other.finiteValue) == 0;
        } else return true;
    }

    /**
     * 用 {@code stripTrailingZeros()} 方法去除尾随零，以避免因 BigDecimal 精度不同而导致的hashCode不一致问题。
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        if (!this.isFinite()) return this.kind.hashCode();
        return this.kind.hashCode() ^ this.finiteValue.stripTrailingZeros().hashCode();
    }
}
