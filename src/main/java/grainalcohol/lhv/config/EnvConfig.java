package grainalcohol.lhv.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.LHVConfig;
import grainalcohol.lhv.common.enums.FormatMode;
import grainalcohol.lhv.common.enums.RenderMode;
import grainalcohol.lhv.common.enums.SourceType;
import grainalcohol.lhv.common.enums.UnitSystem;
import lombok.Getter;

import java.math.RoundingMode;

import java.util.Map;

public class EnvConfig implements LHVConfigSupplier {
    public static final ConfigClassHandler<EnvConfig> HANDLER = ConfigClassHandler.createBuilder(EnvConfig.class)
            .id(LHVMod.id("env_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(LHVMod.CONFIG_DIR.resolve("lhv/env.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @Getter
    private static LHVConfig config;

    // region general
    @SerialEntry(comment = "显示模式：MERGE（合并）、LATEST（最新）、ALL（全部）")
    @AutoGen(category = "general")
    @EnumCycler
    @CustomName("lhv.config.renderMode")
    @CustomDescription({
            "lhv.config.renderMode.desc",
            "yacl3.config.enum.RenderMode.merge.desc",
            "yacl3.config.enum.RenderMode.latest.desc",
            "yacl3.config.enum.RenderMode.all.desc"
    })
    public RenderMode renderMode = RenderMode.ALL;

    @SerialEntry(comment = "最小可见距离（米）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 0.1, max = 10.0, step = 0.1)
    @CustomName("lhv.config.minVisibleRange")
    @CustomDescription("lhv.config.minVisibleRange.desc")
    public double minVisibleRange = 0.1;

    @SerialEntry(comment = "最大可见距离（米）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 5.0, max = 100.0, step = 1.0)
    @CustomName("lhv.config.maxVisibleRange")
    @CustomDescription("lhv.config.maxVisibleRange.desc")
    public double maxVisibleRange = 30.0;

    @SerialEntry(comment = "是否追踪实体位置")
    @AutoGen(category = "general")
    @TickBox
    @CustomName("lhv.config.trackEntity")
    @CustomDescription("lhv.config.trackEntity.desc")
    public boolean trackEntity = true;

    @SerialEntry(comment = "离屏时是否保留显示")
    @AutoGen(category = "general")
    @TickBox
    @CustomName("lhv.config.retainWhenOffScreen")
    @CustomDescription("lhv.config.retainWhenOffScreen.desc")
    public boolean retainWhenOffScreen = true;

    @SerialEntry(comment = "伤害显示持续时间（tick）")
    @AutoGen(category = "general")
    @IntSlider(min = 10, max = 120, step = 5)
    @CustomName("lhv.config.displayDuration")
    @CustomDescription("lhv.config.displayDuration.desc")
    public int displayDuration = 20;

    @SerialEntry(comment = "伤害信息接收范围（米）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 5.0, max = 100.0, step = 1.0)
    @CustomName("lhv.config.maxReceiveRange")
    @CustomDescription("lhv.config.maxReceiveRange.desc")
    public double maxReceiveRange = 60.0;

    @SerialEntry(comment = "在屏幕中沿水平方向随机偏移的范围（像素）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 0.0, max = 200.0, step = 1.0)
    @CustomName("lhv.config.screenOffsetRangeX")
    @CustomDescription("lhv.config.screenOffsetRangeX.desc")
    public double screenOffsetRangeX = 60.0;

    @SerialEntry(comment = "在屏幕中沿垂直方向随机偏移的范围（像素）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 0.0, max = 200.0, step = 1.0)
    @CustomName("lhv.config.screenOffsetRangeY")
    @CustomDescription("lhv.config.screenOffsetRangeY.desc")
    public double screenOffsetRangeY = 60.0;

    @SerialEntry(comment = "在世界中沿X轴随机偏移的范围（米）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 0.0, max = 6.0, step = 0.1)
    @CustomName("lhv.config.offsetRangeX")
    @CustomDescription("lhv.config.offsetRangeX.desc")
    public double offsetRangeX = 1.0;

    @SerialEntry(comment = "在世界中沿Y轴随机偏移的范围（米）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 0.0, max = 6.0, step = 0.1)
    @CustomName("lhv.config.offsetRangeY")
    @CustomDescription("lhv.config.offsetRangeY.desc")
    public double offsetRangeY = 1.0;

    @SerialEntry(comment = "在世界中沿Z轴随机偏移的范围（米）")
    @AutoGen(category = "general")
    @DoubleSlider(min = 0.0, max = 6.0, step = 0.1)
    @CustomName("lhv.config.offsetRangeZ")
    @CustomDescription("lhv.config.offsetRangeZ.desc")
    public double offsetRangeZ = 0.0;

    // region format
    @SerialEntry(comment = "伤害格式：SCIENTIFIC（科学计数法）、UNIT（单位后缀）、AUTO（自动）、RAW（原始）")
    @AutoGen(category = "format")
    @EnumCycler
    @CustomName("lhv.config.formatMode")
    @CustomDescription({
            "lhv.config.formatMode.desc",
            "yacl3.config.enum.FormatMode.scientific.desc",
            "yacl3.config.enum.FormatMode.unit.desc",
            "yacl3.config.enum.FormatMode.auto.desc",
            "yacl3.config.enum.FormatMode.raw.desc"
    })
    public FormatMode formatMode = FormatMode.AUTO;

    @SerialEntry(comment = "保留小数位数")
    @AutoGen(category = "format")
    @IntSlider(min = 0, max = 4, step = 1)
    @CustomName("lhv.config.retainDecimalPlaces")
    @CustomDescription("lhv.config.retainDecimalPlaces.desc")
    public int retainDecimalPlaces = 2;

    @SerialEntry(comment = "伤害无穷时显示的文本")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.infinityDisplay")
    @CustomDescription("lhv.config.infinityDisplay.desc")
    public String infinityDisplay = "Infinity";

    @SerialEntry(comment = "伤害非数字时显示的文本")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.nanDisplay")
    @CustomDescription("lhv.config.nanDisplay.desc")
    public String nanDisplay = "NaN";

    @SerialEntry(comment = "单位制：SHORT_SCALE（短级差，千进制大数单位）、METRIC_PREFIX（SI 国际单位制前缀）、LONG_SCALE（长级差，百万进制大数单位）")
    @AutoGen(category = "format")
    @EnumCycler
    @CustomName("lhv.config.unitSystem")
    @CustomDescription({
            "lhv.config.unitSystem.desc",
            "yacl3.config.enum.UnitSystem.short_scale.desc",
            "yacl3.config.enum.UnitSystem.metric_prefix.desc",
            "yacl3.config.enum.UnitSystem.long_scale.desc"
    })
    public UnitSystem unitSystem = UnitSystem.SHORT_SCALE;

    @SerialEntry(comment = "是否启用数字分组（千位分隔）")
    @AutoGen(category = "format")
    @TickBox
    @CustomName("lhv.config.useGrouping")
    @CustomDescription("lhv.config.useGrouping.desc")
    public boolean useGrouping = true;

    @SerialEntry(comment = "千位分隔符")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.groupingSeparator")
    @CustomDescription("lhv.config.groupingSeparator.desc")
    public String groupingSeparator = ",";

    @SerialEntry(comment = "小数分隔符")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.decimalSeparator")
    @CustomDescription("lhv.config.decimalSeparator.desc")
    public String decimalSeparator = ".";

    @SerialEntry(comment = "指数分隔符（科学计数法的指数标记）")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.exponentSeparator")
    @CustomDescription("lhv.config.exponentSeparator.desc")
    public String exponentSeparator = "E";

    @SerialEntry(comment = "正数前缀")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.positivePrefix")
    @CustomDescription("lhv.config.positivePrefix.desc")
    public String positivePrefix = "";

    @SerialEntry(comment = "负数前缀")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.negativePrefix")
    @CustomDescription("lhv.config.negativePrefix.desc")
    public String negativePrefix = "-";

    @SerialEntry(comment = "正数后缀")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.positiveSuffix")
    @CustomDescription("lhv.config.positiveSuffix.desc")
    public String positiveSuffix = "";

    @SerialEntry(comment = "负数后缀")
    @AutoGen(category = "format")
    @StringField
    @CustomName("lhv.config.negativeSuffix")
    @CustomDescription("lhv.config.negativeSuffix.desc")
    public String negativeSuffix = "";

    @SerialEntry(comment = "舍入模式：UP（向上）、DOWN（向下）、CEILING（向正无穷）、FLOOR（向负无穷）、HALF_UP（四舍五入）、HALF_DOWN（五舍六入）、HALF_EVEN（银行家舍入）")
    @AutoGen(category = "format")
    @EnumCycler(allowedOrdinals = {7})
    @CustomName("lhv.config.roundingMode")
    @CustomDescription({
            "lhv.config.roundingMode.desc",
            "yacl3.config.enum.RoundingMode.up.desc",
            "yacl3.config.enum.RoundingMode.down.desc",
            "yacl3.config.enum.RoundingMode.ceiling.desc",
            "yacl3.config.enum.RoundingMode.floor.desc",
            "yacl3.config.enum.RoundingMode.half_up.desc",
            "yacl3.config.enum.RoundingMode.half_down.desc",
            "yacl3.config.enum.RoundingMode.half_even.desc"
    })
    public RoundingMode roundingMode = RoundingMode.DOWN;

    // region custom
    @SerialEntry(comment = "击杀实体时额外显示的内容，留空则不显示")
    @AutoGen(category = "custom")
    @StringField
    @CustomName("lhv.config.killDisplay")
    @CustomDescription("lhv.config.killDisplay.desc")
    public String killDisplay = "";

    @SerialEntry(comment = "默认伤害颜色（#RRGGBB）")
    @AutoGen(category = "custom")
    @StringField
    @CustomName("lhv.config.defaultColor")
    @CustomDescription("lhv.config.defaultColor.desc")
    public String defaultColor = "#303030";

    @SerialEntry(comment = "暴击伤害颜色（#RRGGBB）")
    @AutoGen(category = "custom")
    @StringField
    @CustomName("lhv.config.criticalColor")
    @CustomDescription("lhv.config.criticalColor.desc")
    public String criticalColor = "#FF0000";

    @SerialEntry(comment = "暴击时的格式化模板，%s为伤害数值占位符")
    @AutoGen(category = "custom")
    @StringField
    @CustomName("lhv.config.criticalFormatTemplate")
    @CustomDescription("lhv.config.criticalFormatTemplate.desc")
    public String criticalFormat = "%s!!";

    @SerialEntry(comment = "是否启用文字描边")
    @AutoGen(category = "custom")
    @TickBox
    @CustomName("lhv.config.outlineEnable")
    @CustomDescription("lhv.config.outlineEnable.desc")
    public boolean outlineEnable = true;

    @SerialEntry(comment = "描边颜色（#RRGGBB）")
    @AutoGen(category = "custom")
    @StringField
    @CustomName("lhv.config.outlineColor")
    @CustomDescription("lhv.config.outlineColor.desc")
    public String outlineColor = "#000000";

    @SerialEntry(comment = "描边宽度（像素）")
    @AutoGen(category = "custom")
    @FloatSlider(min = 0f, max = 5f, step = 0.1f)
    @CustomName("lhv.config.outlineWidth")
    @CustomDescription("lhv.config.outlineWidth.desc")
    public float outlineWidth = 0.8f;

    @SerialEntry(comment = "随深度缩放的参考距离（米），此距离下缩放比例为100%")
    @AutoGen(category = "custom")
    @FloatSlider(min = 0.1f, max = 50f, step = 0.5f)
    @CustomName("lhv.config.depthToScaleRef")
    @CustomDescription("lhv.config.depthToScaleRef.desc")
    public float depthToScaleRef = 10f;

    @SerialEntry(comment = "最小缩放倍率")
    @AutoGen(category = "custom")
    @FloatSlider(min = 0.1f, max = 1f, step = 0.1f)
    @CustomName("lhv.config.minScale")
    @CustomDescription("lhv.config.minScale.desc")
    public float minScale = 0.6f;

    @SerialEntry(comment = "最大缩放倍率")
    @AutoGen(category = "custom")
    @FloatSlider(min = 1f, max = 5f, step = 0.1f)
    @CustomName("lhv.config.maxScale")
    @CustomDescription("lhv.config.maxScale.desc")
    public float maxScale = 2.5f;

    @SerialEntry(comment = "随深度不透明的参考距离（米），此距离下不透明度为指定的最大不透明度")
    @AutoGen(category = "custom")
    @FloatSlider(min = 0.1f, max = 50f, step = 0.5f)
    @CustomName("lhv.config.depthToAlphaRef")
    @CustomDescription("lhv.config.depthToAlphaRef.desc")
    public float depthToAlphaRef = 2f;

    @SerialEntry(comment = "最小不透明度")
    @AutoGen(category = "custom")
    @FloatSlider(min = 0f, max = 1f, step = 0.05f)
    @CustomName("lhv.config.minAlpha")
    @CustomDescription("lhv.config.minAlpha.desc")
    public float minAlpha = 0.8f;

    @SerialEntry(comment = "最大不透明度，指定大于1的数是无意义的")
    @AutoGen(category = "custom")
    @FloatSlider(min = 0f, max = 1f, step = 0.05f)
    @CustomName("lhv.config.maxAlpha")
    @CustomDescription("lhv.config.maxAlpha.desc")
    public float maxAlpha = 1f;

    @SerialEntry(comment = "是否启用更具打击感的文本动画")
    @AutoGen(category = "custom")
    @TickBox
    @CustomName("lhv.config.punchyEffectEnable")
    @CustomDescription("lhv.config.punchyEffectEnable.desc")
    public boolean punchyEffectEnable = false;

    @SerialEntry(comment = "在此处为任意伤害类型定制颜色")
    public Map<String, String> damageTypeColors = LHVConfig.getDefaultColors();

    public static void setConfig(LHVConfig config) {
        EnvConfig.config = config;
    }

    public static EnvConfig getInstance() {
        return HANDLER.instance();
    }

    public static void load() {
        HANDLER.load();
        setConfig(getInstance().toConfig());
    }

    @Override
    public LHVConfig toConfig() {
        return new LHVConfig(
                SourceType.ENVIRONMENT,
                renderMode,
                minVisibleRange,
                maxVisibleRange,
                trackEntity,
                retainWhenOffScreen,
                displayDuration,
                maxReceiveRange,
                screenOffsetRangeX,
                screenOffsetRangeY,
                offsetRangeX,
                offsetRangeY,
                offsetRangeZ,
                formatMode,
                retainDecimalPlaces,
                infinityDisplay,
                nanDisplay,
                unitSystem,
                useGrouping,
                groupingSeparator.charAt(0),
                decimalSeparator.charAt(0),
                exponentSeparator,
                positivePrefix,
                negativePrefix,
                positiveSuffix,
                negativeSuffix,
                roundingMode,
                killDisplay,
                defaultColor,
                criticalColor,
                criticalFormat,
                outlineEnable,
                outlineColor,
                outlineWidth,
                depthToScaleRef,
                minScale,
                maxScale,
                depthToAlphaRef,
                minAlpha,
                maxAlpha,
                punchyEffectEnable,
                damageTypeColors
        );
    }
}
