package grainalcohol.lhv.client.display.func;

import grainalcohol.lhv.client.display.TextDisplay;

import java.util.function.Consumer;

@FunctionalInterface
public interface TextDisplayHandler extends Consumer<TextDisplay> {
    @Override
    void accept(TextDisplay textDisplay);
}
