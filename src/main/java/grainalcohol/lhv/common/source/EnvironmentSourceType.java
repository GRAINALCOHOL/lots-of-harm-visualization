package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.DisplayConfig;
import grainalcohol.lhv.common.dto.FormatConfig;
import grainalcohol.lhv.common.dto.GeneralConfig;
import grainalcohol.lhv.config.EnvConfig;
import net.minecraft.util.Identifier;

public class EnvironmentSourceType implements SourceType {
    @Override
    public Identifier getId() {
        return LHVMod.id("environment");
    }

    @Override
    public GeneralConfig getGeneralConfig() {
        return EnvConfig.getInstance().getGeneralConfig();
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
