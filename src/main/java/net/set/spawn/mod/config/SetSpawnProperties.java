package net.set.spawn.mod.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class SetSpawnProperties {
    public static final String dirName = "setspawn1_16_5";
    public static File configFile;
    public static File globalConfigFile;
    public static Boolean enabled;
    public static String seed;
    public static String coordinates;

    public static void init() {
        Path globalDir = new File (System.getProperty("user.home").replace("\\", "/"), dirName).toPath();
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
        properties.load(new FileInputStream(globalConfigFile));
        enabled = Boolean.valueOf(properties.getProperty("enabled", "true"));
        seed = properties.getProperty("seed", "8398967436125155523");
        coordinates = properties.getProperty("coordinates", "-201.5 72.0 229.5");
    }
    private static void saveProperties(File filename) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        Properties properties = new Properties();
        properties.put("enabled", Boolean.toString(enabled));
        properties.put("seed", seed);
        properties.put("coordinates", coordinates);
        properties.store(bufferedWriter, "");
        bufferedWriter.close();
    }

    private static void saveDefaultProperties(File filename) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        Properties properties = new Properties();
        properties.put("enabled", "true");
        properties.put("seed", "8398967436125155523");
        properties.put("coordinates", "-201.5 72.0 229.5");
        properties.store(bufferedWriter, "");
        bufferedWriter.close();
    }
}
