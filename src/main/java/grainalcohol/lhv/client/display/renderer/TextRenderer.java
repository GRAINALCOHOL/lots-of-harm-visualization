package grainalcohol.lhv.client.display.renderer;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.source.SourceType;

public class TextRenderer extends BaseWorldTextRenderer<StyledText> {
    public TextRenderer(SourceType sourceType) {
        super(sourceType);
    }

    @Override
    public void setStatus(StyledText styledText) {
        this.screenTextRenderer.setText(styledText);
        this.setInitialized();
    }
}
