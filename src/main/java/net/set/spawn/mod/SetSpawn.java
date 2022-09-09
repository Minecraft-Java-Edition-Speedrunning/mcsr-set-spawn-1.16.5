package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;

public class SetSpawn implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("worldpreview")) {
            LogManager.getLogger("SetSpawn").error("World Preview is not compatible with Set Spawn. If you wish to use Set Spawn, remove World Preview from your instance's mod folder.");
            System.exit(1);
        }
    }
}
