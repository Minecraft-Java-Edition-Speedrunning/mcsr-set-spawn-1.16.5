package net.set.spawn.mod.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class SetSpawnProperties {
    private static final Logger LOGGER = LogManager.getLogger();
    public static File configFile;
    public static Boolean enabled;
    public static String seed;
    public static String coordinates;

    public static void init() {
        configFile = FabricLoader.getInstance().getConfigDir().resolve("setspawn.properties").toFile();
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                saveDefaultProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            loadProperties();
            saveProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        enabled = Boolean.valueOf(properties.getProperty("enabled", "true"));
        seed = properties.getProperty("seed", "8398967436125155523");
        coordinates = properties.getProperty("coordinates", "-201.5 72.0 229.5");
    }
    private static void saveProperties() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
        Properties properties = new Properties();
        properties.put("enabled", Boolean.toString(enabled));
        properties.put("seed", seed);
        properties.put("coordinates", coordinates);
        properties.store(bufferedWriter, "");
        bufferedWriter.close();
    }

    private static void saveDefaultProperties() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
        Properties properties = new Properties();
        properties.put("enabled", "true");
        properties.put("seed", "8398967436125155523");
        properties.put("coordinates", "-201.5 72.0 229.5");
        properties.store(bufferedWriter, "");
        bufferedWriter.close();
    }
}
