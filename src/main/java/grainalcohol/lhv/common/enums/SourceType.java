package grainalcohol.lhv.common.enums;

import grainalcohol.lhv.config.EntityConfig;
import grainalcohol.lhv.config.EnvConfig;
import grainalcohol.lhv.config.PlayerConfig;
import grainalcohol.lhv.common.dto.LHVConfig;

import java.util.function.Supplier;

public enum SourceType {
    PLAYER(PlayerConfig::getConfig),
    ENTITY(EntityConfig::getConfig),
    ENVIRONMENT(EnvConfig::getConfig)
    ;

    private final Supplier<LHVConfig> configSupplier;

    SourceType(Supplier<LHVConfig> configSupplier) {
        this.configSupplier = configSupplier;
    }

    public LHVConfig getConfig() {
        return configSupplier.get();
    }
}
