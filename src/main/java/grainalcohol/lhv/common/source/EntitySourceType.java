package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.DisplayConfig;
import grainalcohol.lhv.common.dto.FormatConfig;
import grainalcohol.lhv.common.dto.GeneralConfig;
import grainalcohol.lhv.config.EntityConfig;
import net.minecraft.util.Identifier;

public class EntitySourceType implements SourceType {
    @Override
    public Identifier getId() {
        return LHVMod.id("entity");
    }

    @Override
    public GeneralConfig getGeneralConfig() {
        return EntityConfig.getInstance().getGeneralConfig();
    }

    @Override
    public FormatConfig getFormatConfig() {
        return EntityConfig.getInstance().getFormatConfig();
    }

    @Override
    public DisplayConfig getDisplayConfig() {
        return EntityConfig.getInstance().getDisplayConfig();
    }
}
