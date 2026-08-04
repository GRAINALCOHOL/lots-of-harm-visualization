package grainalcohol.lhv.common.dto;

import grainalcohol.lhv.common.network.DamageS2CPacket;
import grainalcohol.lhv.common.source.SourceType;
import grainalcohol.lhv.common.source.SourceTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DamageContext implements FabricPacket {
    @NotNull
    private final SourceType sourceType;
    private final double damageAmount;
    private final boolean isCritical;

    @Nullable
    private final UUID attackerUuid;
    @NotNull
    private final UUID victimUuid;
    @NotNull
    private String damageTypeId;
    private final long killTime;
    @NotNull
    private final Set<String> damageFlags;

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(sourceType.getId());
        buf.writeDouble(damageAmount);
        buf.writeBoolean(isCritical);
        buf.writeString(attackerUuid != null ? attackerUuid.toString() : "");
        buf.writeUuid(victimUuid);
        buf.writeString(damageTypeId);
        buf.writeLong(killTime);
        buf.writeCollection(damageFlags, PacketByteBuf::writeString);
    }

    @Override
    public PacketType<?> getType() {
        return DamageS2CPacket.PACKET_TYPE;
    }

    public static DamageContext read(PacketByteBuf buf) {
        SourceType sourceType = SourceTypes.getSourceType(buf.readIdentifier());
        if (sourceType == null) {
            sourceType = SourceTypes.ENVIRONMENT;
        }
        double damageAmount = buf.readDouble();
        boolean isCritical = buf.readBoolean();
        UUID attackerUuid = null;
        String attackerUuidStr = buf.readString();
        if (!attackerUuidStr.isEmpty()) {
            attackerUuid = UUID.fromString(attackerUuidStr);
        }
        UUID victimUuid = buf.readUuid();
        String damageTypeId = buf.readString();
        long killTime = buf.readLong();
        Set<String> damageFlags = buf.readCollection(HashSet::new, PacketByteBuf::readString);
        return new DamageContext(
                sourceType,
                damageAmount,
                isCritical,
                attackerUuid,
                victimUuid,
                damageTypeId,
                killTime,
                damageFlags
        );
    }
}
