package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.common.dto.config.BasicConfig;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import grainalcohol.lhv.config.EntityConfig;
import net.minecraft.util.Identifier;

public class EntitySourceType implements SourceType {
    @Override
    public Identifier getId() {
        return LHVMod.id("entity");
    }

    @Override
    public BasicConfig getBasicConfig() {
        return EntityConfig.getInstance().getBasicConfig();
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
