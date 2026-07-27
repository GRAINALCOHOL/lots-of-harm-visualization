package grainalcohol.lhv;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class LHVMod implements ModInitializer {
	public static final String MOD_ID = "lhv";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();

	@Override
	public void onInitialize() {
		// 非纯客户端模组，服务端需要处理伤害并发送数据包
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
