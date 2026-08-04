package grainalcohol.lhv.client.effect;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

public interface Effect {
    /**
     *
     * @param drawContext
     * @param ctx
     */
    void apply(@NotNull DrawContext drawContext, @NotNull DisplayContext ctx);

    /**
     *
     * @param ctx
     * @param setting
     */
    void apply(@NotNull DisplayContext ctx, @NotNull CharSetting setting);
    void start();
    void pause();
    void restart();
    void reset();
    void restartIfFinished(int textLength);
    int getHeadMs(int textLength);
    int getTailMs(int textLength);
    boolean isFinished(int textLength);
}
