package grainalcohol.lhv.config;

import grainalcohol.lhv.common.dto.config.BasicConfig;
import grainalcohol.lhv.common.dto.config.DisplayConfig;
import grainalcohol.lhv.common.dto.config.FormatConfig;
import org.jetbrains.annotations.NotNull;

public interface LHVConfigSupplier {
    @NotNull
    BasicConfig getBasicConfig();
    @NotNull
    FormatConfig getFormatConfig();
    @NotNull
    DisplayConfig getDisplayConfig();
     void clearCache();
}
