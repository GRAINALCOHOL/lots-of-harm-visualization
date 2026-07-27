package grainalcohol.lhv.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.CustomDescription;
import dev.isxander.yacl3.config.v2.api.autogen.CustomName;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.LHVConfig;

import java.util.Set;

public class GlobalConfig {
    public static final ConfigClassHandler<GlobalConfig> HANDLER = ConfigClassHandler.createBuilder(GlobalConfig.class)
            .id(LHVMod.id("global_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(LHVMod.CONFIG_DIR.resolve("lhv/global.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "大数字测试模式")
    @AutoGen(category = "global")
    @TickBox
    @CustomName("lhv.config.bigNumberTestMode")
    @CustomDescription("lhv.config.bigNumberTestMode.desc")
    public boolean bigNumberTestMode = false;

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
