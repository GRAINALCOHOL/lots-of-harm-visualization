package grainalcohol.lhv.common.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class NetworkUtil {
    public static void writeVec3d(PacketByteBuf buf, Vec3d vec3d) {
        buf.writeDouble(vec3d.x);
        buf.writeDouble(vec3d.y);
        buf.writeDouble(vec3d.z);
    }

    public static Vec3d readVec3d(PacketByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vec3d(x, y, z);
    }
}
