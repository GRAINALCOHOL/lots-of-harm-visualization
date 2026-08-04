package grainalcohol.lhv.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class DamageInfo {
    private final double damageAmount;
    private final boolean isCritical;
    @Nullable
    private final TextColor damageColor;
}
