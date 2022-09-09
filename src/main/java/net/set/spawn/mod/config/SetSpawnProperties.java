package net.set.spawn.mod.config;

import net.fabricmc.loader.api.FabricLoader;
import net.set.spawn.mod.SetSpawn;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class SetSpawnProperties {
    public static final String subDir = SetSpawn.MOD_ID + "1_16_5";
    public static File configFile;
    public static File globalConfigFile;
    public static Boolean enabled;
    public static Boolean use_global_config;
    public static String seed;
    public static String coordinates;

    public static void init() {
        Path globalDir = new File (System.getProperty("user.home").replace("\\", "/"), subDir).toPath();
        globalDir.toFile().mkdirs();
        globalConfigFile = new File(globalDir.toFile(), "globalsetspawn.properties");
        configFile = FabricLoader.getInstance().getConfigDir().resolve("setspawn.properties").toFile();
        createIfNonExistent(globalConfigFile);
        createIfNonExistent(configFile);

        try {
            loadProperties();
            saveProperties(configFile);
            saveProperties(globalConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createIfNonExistent(File filename) {
        if (!filename.exists()) {
            try {
                filename.createNewFile();
                saveDefaultProperties(filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        enabled = Boolean.valueOf(properties.getProperty("enabled", "true"));
        use_global_config = Boolean.valueOf(properties.getProperty("use_global_config", "false"));
        seed = properties.getProperty("seed", "8398967436125155523");
        coordinates = properties.getProperty("coordinates", "-201.5 72.0 229.5");
        if (use_global_config) {
            properties.load(new FileInputStream(globalConfigFile));
            enabled = Boolean.valueOf(properties.getProperty("enabled", "true"));
            seed = properties.getProperty("seed", "8398967436125155523");
            coordinates = properties.getProperty("coordinates", "-201.5 72.0 229.5");
        }
    }
    private static void saveProperties(File filename) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        Properties properties = new Properties();
        properties.put("enabled", Boolean.toString(enabled));
        if (filename == configFile) {
            properties.put("use_global_config", Boolean.toString(use_global_config));
        }
        properties.put("seed", seed);
        properties.put("coordinates", coordinates);
        properties.store(bufferedWriter, "");
        bufferedWriter.close();
    }

    private static void saveDefaultProperties(File filename) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        Properties properties = new Properties();
        properties.put("enabled", "true");
        if (filename == configFile) {
            properties.put("use_global_config", "false");
        }
        properties.put("seed", "8398967436125155523");
        properties.put("coordinates", "-201.5 72.0 229.5");
        properties.store(bufferedWriter, "");
        bufferedWriter.close();
    }
}
