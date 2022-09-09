package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;

public class SetSpawn implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("worldpreview")) {
            LogManager.getLogger("SetSpawn").error("WorldPreview is not compatible with SetSpawn. If you wish to use SetSpawn, remove WorldPreview from your instance's mod folder.");
            System.exit(1);
        }
    }
}
