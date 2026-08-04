package grainalcohol.lhv.flag;

import grainalcohol.lhv.LHVMod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface FlagProviders {
    List<FlagProvider> PROVIDERS = new ArrayList<>();

    static void register(FlagProvider provider) {
        PROVIDERS.add(provider);
    }

    static Set<String> compute(FlagContext context) {
        Set<String> flags = new HashSet<>();
        for (var provider : PROVIDERS) {
            flags.addAll(provider.provide(context));
        }
        return flags;
    }

    static void init() {
        register(context -> {
            if (context.getVictim().isDead()) return Set.of(LHVMod.DIED_FLAG);
            else return Set.of();
        });
    }
}
