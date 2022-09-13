package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Objects;

public class SetSpawn implements ClientModInitializer {
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "setspawnmod";
    public static boolean shouldModifySpawn;
    public static final String subDir = SetSpawn.MOD_ID + "_global";
    public static File localConfigFile;
    public static File globalConfigFile;
    public static Config config;

    private static void createIfNonExistent(File file) {
        try {
            if(file.createNewFile()){
                writeDefaultProperties(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException,NumberFormatException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(localConfigFile, Config.class);
            if (config.isUseGlobalConfig()) {
                config = mapper.readValue(globalConfigFile, Config.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDefaultProperties(File file) throws IOException {
        Seed vine = new Seed("8398967436125155523", "vine", -201.5, 229.5);
        Seed taiga = new Seed("2483313382402348964", "taiga", -230.5, 247.5);
        Seed[] seedsToWrite = new Seed[] { vine, taiga };
        Config config = new Config(true, false, seedsToWrite);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing");
        File globalDir = new File(System.getProperty("user.home").replace("\\", "/"), subDir);
        globalDir.mkdirs();
        globalConfigFile = new File(globalDir, "setspawn.json");
        localConfigFile = FabricLoader.getInstance().getConfigDir().resolve("setspawn.json").toFile();
        createIfNonExistent(globalConfigFile);
        createIfNonExistent(localConfigFile);
        try {
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Seed findSeedObjectFromLong(long seedLong) {
        String seed = String.valueOf(seedLong);
        Seed[] seedObjects = config.getSeeds();
        for (Seed seedObject : seedObjects) {
            if (Objects.equals(seedObject.getSeed(), seed)) {
                return seedObject;
            }
        }
        return null;
    }

}
