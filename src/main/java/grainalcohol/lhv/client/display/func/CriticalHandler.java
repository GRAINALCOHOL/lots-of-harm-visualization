package grainalcohol.lhv.client.display.func;

import java.util.function.Consumer;

public interface CriticalHandler extends Consumer<Boolean> {
    @Override
    void accept(Boolean isCritical);
}
