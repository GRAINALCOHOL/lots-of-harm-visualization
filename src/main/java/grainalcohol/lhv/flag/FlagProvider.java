package grainalcohol.lhv.flag;

import java.util.Set;

@FunctionalInterface
public interface FlagProvider {
    Set<String> provide(FlagContext context);
}
