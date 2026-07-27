package grainalcohol.lhv.mixin.client;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextRenderer.class)
public interface FontAccess {
    @Invoker("getFontStorage")
    FontStorage callGetFontStorage(Identifier id);
}
