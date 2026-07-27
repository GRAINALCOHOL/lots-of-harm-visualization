package grainalcohol.lhv.common.dto;

import grainalcohol.lhv.client.wrapper.StyledText;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.TextColor;

@Getter
@AllArgsConstructor
public class DamageInfo {
    private final double damageAmount;
    private final boolean isCritical;
    private final StyledText subText;
    private final TextColor damageColor;
}
