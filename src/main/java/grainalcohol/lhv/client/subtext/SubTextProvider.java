package grainalcohol.lhv.client.subtext;

import grainalcohol.lhv.client.wrapper.StyledText;
import grainalcohol.lhv.common.dto.DamageContext;

import java.util.Optional;

@FunctionalInterface
public interface SubTextProvider {
    Optional<StyledText> provide(DamageContext context);
}
