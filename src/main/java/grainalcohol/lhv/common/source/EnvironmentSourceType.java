package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.config.BasicConfig;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import grainalcohol.lhv.config.EnvConfig;
import net.minecraft.util.Identifier;

public class EnvironmentSourceType implements SourceType {
    @Override
    public Identifier getId() {
        return LHVMod.id("environment");
    }

    @Override
    public BasicConfig getBasicConfig() {
        return EnvConfig.getInstance().getBasicConfig();
    }

    @Override
    public FormatConfig getFormatConfig() {
        return EnvConfig.getInstance().getFormatConfig();
    }

    @Override
    public DisplayConfig getDisplayConfig() {
        return EnvConfig.getInstance().getDisplayConfig();
    }
}
