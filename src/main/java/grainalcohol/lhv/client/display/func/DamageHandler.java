package grainalcohol.lhv.client.display.func;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface DamageHandler extends BiConsumer<Double, Boolean> {
    @Override
    void accept(Double damageAmount, Boolean isCritical);
}
