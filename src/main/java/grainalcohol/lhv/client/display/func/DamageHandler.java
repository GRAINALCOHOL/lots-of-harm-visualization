package grainalcohol.lhv.client.display.func;

import java.math.BigDecimal;
import java.util.function.Consumer;

@FunctionalInterface
public interface DamageHandler extends Consumer<BigDecimal> {
    @Override
    void accept(BigDecimal damageAmount);
}
