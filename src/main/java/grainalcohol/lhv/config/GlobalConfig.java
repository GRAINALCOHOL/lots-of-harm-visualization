package grainalcohol.lhv.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.LHVConfig;
import grainalcohol.lhv.common.enums.EntitySortMode;
import grainalcohol.lhv.common.enums.SourceSortMode;

import java.util.Set;

public class GlobalConfig {
    public static final ConfigClassHandler<GlobalConfig> HANDLER = ConfigClassHandler.createBuilder(GlobalConfig.class)
            .id(LHVMod.id("global_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(LHVMod.CONFIG_DIR.resolve("lhv/global.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "实体排序方式：RANDOM（随机）、NEAREST（最近）、FARTHEST（最远）")
    @AutoGen(category = "global")
    @EnumCycler
    @CustomName("lhv.config.entitySortMode")
    @CustomDescription({
            "lhv.config.entitySortMode.desc",
            "yacl3.config.enum.EntitySortMode.random.desc",
            "yacl3.config.enum.EntitySortMode.nearest.desc",
            "yacl3.config.enum.EntitySortMode.farthest.desc"
    })
    public EntitySortMode entitySortMode = EntitySortMode.NEAREST;

    @SerialEntry(comment = "伤害来源排序方式：LATEST（最新）、OLDEST（最旧）")
    @AutoGen(category = "global")
    @EnumCycler
    @CustomName("lhv.config.sourceSortMode")
    @CustomDescription({
            "lhv.config.sourceSortMode.desc",
            "yacl3.config.enum.SourceSortMode.latest.desc",
            "yacl3.config.enum.SourceSortMode.oldest.desc"
    })
    public SourceSortMode sourceSortMode = SourceSortMode.LATEST;

    @SerialEntry(comment = "大数字测试模式")
    @AutoGen(category = "global")
    @TickBox
    @CustomName("lhv.config.bigNumberTestMode")
    @CustomDescription("lhv.config.bigNumberTestMode.desc")
    public boolean bigNumberTestMode = false;

    @SerialEntry(comment = "无限伤害测试模式")
    @AutoGen(category = "global")
    @TickBox
    @CustomName("lhv.config.infinityTestMode")
    @CustomDescription("lhv.config.infinityTestMode.desc")
    public boolean infinityTestMode = false;

    @SerialEntry(comment = "客户端不会处理的伤害类型（客户端不显示）")
    public Set<String> ignoreDamageTypes = LHVConfig.getDefaultIgnoreTypes();

    public static GlobalConfig getInstance() {
        return HANDLER.instance();
    }

    public static boolean shouldIgnore(String damageTypeId) {
        return getInstance().ignoreDamageTypes.contains(damageTypeId);
    }

    public static void load() {
        HANDLER.load();
    }
}
