package grainalcohol.lhv.common.source;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface LHVSourceTypes {
    Map<Identifier, SourceType> SOURCE_TYPES = new HashMap<>();

    SourceType PLAYER = register(new PlayerSourceType());
    SourceType ENTITY = register(new EntitySourceType());
    SourceType ENVIRONMENT = register(new EnvironmentSourceType());

    static SourceType register(SourceType sourceType) {
        SOURCE_TYPES.put(sourceType.getId(), sourceType);
        return sourceType;
    }

    @Nullable
    static SourceType getSourceType(Identifier id) {
        return SOURCE_TYPES.getOrDefault(id, null);
    }
}
