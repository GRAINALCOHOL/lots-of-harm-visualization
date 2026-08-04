package grainalcohol.lhv.common.source;

import grainalcohol.lhv.LHVMod;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface SourceTypes {
    Map<Identifier, SourceType> SOURCE_TYPES = new HashMap<>();

    SourceType PLAYER = register(new PlayerSourceType());
    SourceType ENTITY = register(new EntitySourceType());
    SourceType ENVIRONMENT = register(new EnvironmentSourceType());

    @NotNull
    static SourceType register(@NotNull SourceType sourceType) {
        if (SOURCE_TYPES.containsKey(sourceType.getId())) {
            LHVMod.LOGGER.warn("SourceType with id {} is already registered.", sourceType.getId());
        } else {
            SOURCE_TYPES.put(sourceType.getId(), sourceType);
        }
        return sourceType;
    }

    @Nullable
    static SourceType getSourceType(Identifier id) {
        return SOURCE_TYPES.getOrDefault(id, null);
    }
}
