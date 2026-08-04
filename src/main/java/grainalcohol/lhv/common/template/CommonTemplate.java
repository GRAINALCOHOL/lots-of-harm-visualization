package grainalcohol.lhv.common.template;

import grainalcohol.lhv.LHVMod;
import grainalcohol.lhv.client.display.func.TextDisplayHandler;
import grainalcohol.lhv.client.effect.effects.*;
import net.minecraft.util.Identifier;

public class CommonTemplate implements EffectTemplate {
    @Override
    public Identifier getId() {
        return LHVMod.id("common");
    }

    @Override
    public TextDisplayHandler onCreated() {
        return textDisplay -> textDisplay
                .addEffect(new FlashInEffect())
                .addEffect(new ShrinkEffect())
                .addEffect(new SimpleTypewriterEffect())
                .addEffect(new SettleEffect());
    }

    @Override
    public TextDisplayHandler onChanged() {
        return textDisplay -> {
            textDisplay.getOrPutEffect(BounceEffect.class, new BounceEffect()).ifPresent(effect -> effect.restartIfFinished(textDisplay.textLength()));
            textDisplay.getOrPutEffect(SweepEffect.class, new SweepEffect()).ifPresent(effect -> effect.restartIfFinished(textDisplay.textLength()));
        };
    }
}
