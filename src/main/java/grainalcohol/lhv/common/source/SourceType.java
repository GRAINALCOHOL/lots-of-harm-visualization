package grainalcohol.lhv.common.source;

import grainalcohol.lhv.common.dto.DisplayConfig;
import grainalcohol.lhv.common.dto.FormatConfig;
import grainalcohol.lhv.common.dto.GeneralConfig;
import net.minecraft.util.Identifier;

public interface SourceType {
    Identifier getId();

    GeneralConfig getGeneralConfig();
    FormatConfig getFormatConfig();
    DisplayConfig getDisplayConfig();
}
