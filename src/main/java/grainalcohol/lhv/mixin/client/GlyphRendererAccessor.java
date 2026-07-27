package grainalcohol.lhv.mixin.client;

import net.minecraft.client.font.GlyphRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlyphRenderer.class)
public interface GlyphRendererAccessor {
    @Accessor("minX") float lhv$getMinX();
    @Accessor("maxX") float lhv$getMaxX();
    @Accessor("minY") float lhv$getMinY();
    @Accessor("maxY") float lhv$getMaxY();
}
