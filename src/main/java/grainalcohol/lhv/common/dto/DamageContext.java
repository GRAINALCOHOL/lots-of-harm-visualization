package grainalcohol.lhv.common.dto;

import grainalcohol.lhv.common.enums.SourceType;
import grainalcohol.lhv.common.network.DamageS2CPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DamageContext implements FabricPacket {
    private final SourceType sourceType;
    private final double damageAmount;
    private final boolean isCritical;

    private final UUID victimUuid;
    private String damageTypeId;

    // flags
    private final boolean isDied;

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(sourceType);
        buf.writeDouble(damageAmount);
        buf.writeBoolean(isCritical);
        buf.writeUuid(victimUuid);
        buf.writeString(damageTypeId);
        buf.writeBoolean(isDied);
    }

    @Override
    public PacketType<?> getType() {
        return DamageS2CPacket.PACKET_TYPE;
    }

    public static DamageContext read(PacketByteBuf buf) {
        SourceType sourceType = buf.readEnumConstant(SourceType.class);
        double damageAmount = buf.readDouble();
        boolean isCritical = buf.readBoolean();
        UUID victimUuid = buf.readUuid();
        String damageTypeId = buf.readString();
        boolean isDied = buf.readBoolean();
        return new DamageContext(
                sourceType,
                damageAmount,
                isCritical,
                victimUuid,
                damageTypeId,
                isDied
        );
    }
}
