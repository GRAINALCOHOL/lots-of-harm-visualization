package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.enums.DamageSortMode;
import grainalcohol.lhv.common.source.SourceType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListTextRenderer implements WorldTextRenderer<List<StyledText>> {
    private final SourceType sourceType;
    private final ArrayDeque<TextRenderer> textRendererPool;

    public ListTextRenderer(SourceType sourceType) {
        this.sourceType = sourceType;
        this.textRendererPool = new ArrayDeque<>();
    }

    @Override
    public void setStatus(List<StyledText> styledTextList) {
        for (var styledText : styledTextList) {
            var renderer = new TextRenderer(sourceType);
            renderer.setStatus(styledText);
            textRendererPool.add(renderer);
        }
    }

    @Override
    public void render(@NotNull DrawContext drawContext, Vec3d worldPos, float yawDelta) {
        List<TextRenderer> renderList = computeRenderList(sourceType.getBasicConfig().getDamageSortMode());
        if (renderList.isEmpty()) return;

        for (var renderer : renderList) {
            renderer.render(drawContext, worldPos, yawDelta);
        }
    }

    private List<TextRenderer> computeRenderList(DamageSortMode sortMode) {
        while (!textRendererPool.isEmpty() && textRendererPool.peekFirst().isExpired()) {
            textRendererPool.pollFirst();
        }

        if (textRendererPool.isEmpty()) return List.of();

        List<TextRenderer> result = new ArrayList<>(textRendererPool);
        return switch (sortMode) {
            case LATEST -> result;
            case OLDEST -> {
                Collections.reverse(result);
                yield result;
            }
        };
    }

    @Override
    public boolean isExpired() {
        return textRendererPool.isEmpty();
    }
}
