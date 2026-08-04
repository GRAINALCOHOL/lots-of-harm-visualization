package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.config.BasicConfig;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import grainalcohol.lhv.config.PlayerConfig;
import net.minecraft.util.Identifier;

public class PlayerSourceType implements SourceType {
    @Override
    public Identifier getId() {
        return LHVMod.id("player");
    }

    @Override
    public BasicConfig getBasicConfig() {
        return PlayerConfig.getInstance().getBasicConfig();
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
