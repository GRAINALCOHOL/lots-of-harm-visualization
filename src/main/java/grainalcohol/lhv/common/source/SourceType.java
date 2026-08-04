package grainalcohol.lhv.common.source;

import grainalcohol.lhv.common.dto.config.BasicConfig;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import net.minecraft.util.Identifier;

public interface SourceType {
    Identifier getId();

    BasicConfig getBasicConfig();
    FormatConfig getFormatConfig();
    DisplayConfig getDisplayConfig();
}
