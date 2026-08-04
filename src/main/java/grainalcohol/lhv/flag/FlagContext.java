package grainalcohol.lhv.flag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Getter
@AllArgsConstructor
public class FlagContext {
    private final DamageSource damageSource;
    private final LivingEntity victim;
    private final float damageAmount;
    private final boolean isCritical;
}
