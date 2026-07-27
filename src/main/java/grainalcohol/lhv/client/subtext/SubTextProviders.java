package grainalcohol.lhv.client.subtext;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.DamageContext;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public class SubTextProviders {
    private static final EnumMap<SubTextPriority, List<SubTextProvider>> PROVIDERS = new EnumMap<>(SubTextPriority.class);

    public static void register(SubTextPriority priority, SubTextProvider provider) {
        PROVIDERS.computeIfAbsent(priority, k -> new ArrayList<>()).add(provider);
    }

    public static void register(SubTextProvider provider) {
        register(SubTextPriority.DEFAULT, provider);
    }

    public static StyledText compute(DamageContext context) {
        for (var list : PROVIDERS.values()) {
            if (list == null || list.isEmpty()) continue;
            for (var provider : list) {
                var result = provider.provide(context);
                // 短路第优先级的所有内容
                if (result.isPresent()) return result.get();
            }
        }

        return StyledText.empty();
    }

    public static void init() {
        register(context -> {
            if (context.isDied()) {
                return Optional.of(StyledText.literal(
                        context.getSourceType().getConfig().getKillDisplay(),
                        0xFFFF1000
                ));
            } else return Optional.empty();
        });
//        register(context -> {
//            if (context.isInstantKill()) {
//                return Optional.of(StyledText.literal(
//                        context.getSourceType().getConfig().getInstantKillDisplay(),
//                        0xFFFF1000
//                ));
//            } else return Optional.empty();
//        });
    }
}
