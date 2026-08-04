package grainalcohol.lhv.client.text;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.DamageContext;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TextProvider {
    @Nullable
    StyledText provide(DamageContext context);
}
