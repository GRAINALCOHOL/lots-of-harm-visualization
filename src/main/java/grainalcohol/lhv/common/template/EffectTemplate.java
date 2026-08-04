package grainalcohol.lhv.common.template;

import grainalcohol.lhv.client.display.func.TextDisplayHandler;
import net.minecraft.util.Identifier;

public interface EffectTemplate {
    Identifier getId();
    TextDisplayHandler onCreated();
    TextDisplayHandler onChanged();
}
