package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.DisplayConfig;
import grainalcohol.lhv.common.dto.FormatConfig;
import grainalcohol.lhv.common.dto.GeneralConfig;
import grainalcohol.lhv.config.PlayerConfig;
import net.minecraft.util.Identifier;

public class PlayerSourceType implements SourceType {
    @Override
    public Identifier getId() {
        return LHVMod.id("player");
    }

    @Override
    public GeneralConfig getGeneralConfig() {
        return PlayerConfig.getInstance().getGeneralConfig();
    }

    @Override
    public FormatConfig getFormatConfig() {
        return PlayerConfig.getInstance().getFormatConfig();
    }

    @Override
    public DisplayConfig getDisplayConfig() {
        return PlayerConfig.getInstance().getDisplayConfig();
    }
}
