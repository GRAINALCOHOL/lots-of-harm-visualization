package grainalcohol.lhv.client.text;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.DamageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TextProviders {
    List<TextProvider> PROVIDERS = new ArrayList<>();

    static void register(TextProvider provider) {
        PROVIDERS.add(provider);
    }

    static List<StyledText> compute(DamageContext context) {
        List<StyledText> result = new ArrayList<>();
        for (var provider : PROVIDERS) {
            StyledText text = provider.provide(context);
            if (text != null && !text.isBlank()) result.add(text);
        }
        return result;
    }

    static void init() {
        register(context -> {
            Set<String> flags = context.getDamageFlags();
            if (context.getKillTime() >= 0 && context.getKillTime() <= 20) {
                return StyledText.literal(
                        context.getSourceType().getDisplayConfig().getInstantKillDisplay(),
                        0xFF1000
                );
            } else if (flags.contains(LHVMod.DIED_FLAG)) {
                return StyledText.literal(
                        context.getSourceType().getDisplayConfig().getKillDisplay(),
                        0xFF1000
                );
            } else return null;
        });
    }
}
