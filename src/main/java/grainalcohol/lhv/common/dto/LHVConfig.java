package grainalcohol.lhv.common.dto;

import grainalcohol.lhv.client.display.renderer.DamageRenderer;
import grainalcohol.lhv.client.display.renderer.ListRenderer;
import grainalcohol.lhv.client.display.renderer.MergeRenderer;
import grainalcohol.lhv.client.display.renderer.SingleRenderer;
import grainalcohol.lhv.common.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
public final class LHVConfig {
    // region general
    private final RenderMode renderMode;
    private final DamageSortMode damageSortMode;
    private final double minVisibleRange;
    private final double maxVisibleRange;
    private final boolean trackEntity;
    private final boolean retainWhenOffScreen;
    private final int displayDuration;
    private final double maxReceiveRange;
    private final double screenOffsetRangeX;
    private final double screenOffsetRangeY;
    private final double offsetRangeX;
    private final double offsetRangeY;
    private final double offsetRangeZ;

    // region format
    private final FormatMode formatMode;
    private final int retainDecimalPlaces;
    private final String infinityDisplay;
    private final String nanDisplay;
    private final UnitSystem unitSystem;
    private final boolean useGrouping;
    private final char groupingSeparator;
    private final char decimalSeparator;
    private final String exponentSeparator;
    private final String positivePrefix;
    private final String negativePrefix;
    private final String positiveSuffix;
    private final String negativeSuffix;
    private final RoundingMode roundingMode;

    // region custom
    private final String killDisplay;

    private final String defaultColor;
    private final String criticalColor;
    private final String criticalFormat;

    private final boolean outlineEnable;
    private final String outlineColor;
    private final float outlineWidth;

    private final float depthToScaleRef;
    private final float minScale;
    private final float maxScale;

    private final float depthToAlphaRef;
    private final float minAlpha;
    private final float maxAlpha;

    private final boolean punchyEffectEnable;

    private final Map<String, String> damageTypeColors;

    @NotNull
    public DamageRenderer createRenderer(SourceType sourceType) {
        return switch (getRenderMode()) {
            case MERGE -> new MergeRenderer(sourceType);
            case ALL -> new ListRenderer(sourceType);
            case LATEST -> new SingleRenderer(sourceType);
        };
    }

    public static Set<String> getDefaultIgnoreTypes() {
        Set<String> set = new HashSet<>();
        set.add("minecraft:out_of_world");
        set.add("minecraft:in_wall");
        set.add("minecraft:cramming");
        return set;
    }

    public static Map<String, String> getDefaultColors() {
        Map<String, String> map = new HashMap<>();
        map.put("minecraft:in_fire", "#FE3622");
        map.put("minecraft:on_fire", "#FF3017");
        map.put("minecraft:lava", "#FF1B00");
        map.put("minecraft:hot_floor", "#FF4300");
        map.put("minecraft:lightning_bolt", "#EBD31C");
        map.put("minecraft:drown", "#090072");
        map.put("minecraft:starve", "#782531");
        map.put("minecraft:cactus", "#1F9300");
        map.put("minecraft:fall", "#303030");
        map.put("minecraft:fly_into_wall", "#303030");
        map.put("minecraft:out_of_world", "#D000FF");
        map.put("minecraft:dry_out", "#000CFF");
        map.put("minecraft:sweet_berry_bush", "#00FF4A");
        map.put("minecraft:freeze", "#001FFF");
        map.put("minecraft:stalagmite", "#4A4000");
        map.put("minecraft:falling_block", "#4A4000");
        map.put("minecraft:falling_anvil", "#030200");
        map.put("minecraft:falling_stalactite", "#4A4000");
        map.put("minecraft:sting", "#00FF12");
        map.put("minecraft:outside_border", "#000000");
        map.put("minecraft:mob_attack", "#FF0000");
        map.put("minecraft:mob_attack_no_aggro", "#FF0064");
        map.put("minecraft:player_attack", "#FF0000");
        map.put("minecraft:arrow", "#FF0000");
        map.put("minecraft:trident", "#000AFF");
        map.put("minecraft:mob_projectile", "#FF0000");
        map.put("minecraft:fireworks", "#00FF07");
        map.put("minecraft:fireball", "#FF6B00");
        map.put("minecraft:unattributed_fireball", "#FD6A00");
        map.put("minecraft:wither_skull", "#000000");
        map.put("minecraft:thrown", "#5C0062");
        map.put("minecraft:dragon_breath", "#5C0062");
        map.put("minecraft:sonic_boom", "#003E8D");
        map.put("minecraft:magic", "#6B008D");
        map.put("minecraft:indirect_magic", "#6D008D");
        map.put("minecraft:wither", "#000000");
        map.put("minecraft:thorns", "#004614");
        map.put("minecraft:explosion", "#FFC000");
        map.put("minecraft:player_explosion", "#FFC000");
        map.put("minecraft:generic", "#FF0058");
        map.put("minecraft:generic_kill", "#000000");
        map.put("minecraft:bad_respawn_point", "#000000");
        return map;
    }

    @Nullable
    public TextColor findColor(String damageTypeId) {
        String hex = getDamageTypeColors().get(damageTypeId);
        if (hex == null) return null;
        return TextColor.parse(hex);
    }

    public boolean isInRenderRange(Vec3d start, Vec3d end) {
        double sqDist = start.squaredDistanceTo(end);
        return sqDist >= this.getMinVisibleRange() * this.getMinVisibleRange() && sqDist <= this.getMaxVisibleRange() * this.getMaxVisibleRange();
    }

    public boolean isInReceiveRange(Vec3d start, Vec3d end) {
        double sqDist = start.squaredDistanceTo(end);
        return sqDist <= this.getMaxReceiveRange() * this.getMaxReceiveRange();
    }
}
