package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;

public class SetSpawn implements ClientModInitializer {

    public static final String MOD_ID = "setspawnmod";

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("worldpreview")) {
            LogManager.getLogger("SetSpawn").error("WorldPreview is not compatible with SetSpawn.");
            System.exit(1);
        }
    }
}
