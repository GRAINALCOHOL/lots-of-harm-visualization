package grainalcohol.lhv.common.template;

import grainalcohol.lhv.LHVMod;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public interface EffectTemplates {
    Map<Identifier, EffectTemplate> TEMPLATES = new HashMap<>();

    EffectTemplate SPRING = register(new SpringTemplate());
    EffectTemplate COMMON = register(new CommonTemplate());

    @NotNull
    static EffectTemplate register(@NotNull EffectTemplate template) {
        if (TEMPLATES.containsKey(template.getId())) {
            LHVMod.LOGGER.warn("EffectTemplate with id {} is already registered.", template.getId());
        } else {
            TEMPLATES.put(template.getId(), template);
        }
        return template;
    }

    @Nullable
    static EffectTemplate getTemplate(Identifier id) {
        return TEMPLATES.getOrDefault(id, null);
    }
}
