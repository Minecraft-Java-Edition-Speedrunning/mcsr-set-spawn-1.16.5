package net.set.spawn.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SetSpawn implements ClientModInitializer {
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "setspawnmod";
    public static boolean shouldModifySpawn;
    public static final String subDir = SetSpawn.MOD_ID + "1_16_5";
    public static File configFile;
    public static File globalConfigFile;
    public static boolean use_global_config;
    public static String seed;
    public static double coordinateX;
    public static double coordinateZ;

    private static void createIfNonExistent(File file) {
        try {
            if(file.createNewFile()){
                saveDefaultProperties(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException,NumberFormatException {
        Properties properties = getProperties(configFile);

        use_global_config = Boolean.parseBoolean(properties.getProperty("use_global_config", "false"));
        if (use_global_config) {
            properties= getProperties(globalConfigFile);
        }
        seed = properties.getProperty("seed", "8398967436125155523");

        try {
            coordinateX= Double.parseDouble( properties.getProperty("coordinateX", "-201.5"));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("X coordinate was given in an invalid format.");
        }
        try {
            coordinateZ= Double.parseDouble( properties.getProperty("coordinateZ", "229.5"));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Z coordinate was given in an invalid format.");
        }


    }
    static Properties getProperties(File file) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(file)){
            Properties properties= new Properties();
            properties.load(inputStream);
            return properties;
        }
    }

    private static void saveProperties(File file) throws IOException {
        try(FileOutputStream f = new FileOutputStream(file)){
            Properties properties = new Properties();
            if (file == configFile) {
                properties.put("use_global_config", ""+use_global_config);
            }
            properties.put("seed", seed);
            properties.put("coordinateX", ""+coordinateX);
            properties.put("coordinateZ", ""+coordinateZ);
            properties.store(f, "");
        }
    }

    private static void saveDefaultProperties(File file) throws IOException {
        try(FileOutputStream f = new FileOutputStream(file)){
            Properties properties = new Properties();
            if (file == configFile) {
                properties.put("use_global_config", "false");
            }
            properties.put("seed", "8398967436125155523");
            properties.put("coordinateX", "-201.5");
            properties.put("coordinateZ", "229.5");
            properties.store(f, "");
        }
    }
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing");
        File globalDir = new File (System.getProperty("user.home").replace("\\", "/"), subDir);
        globalDir.mkdirs();
        globalConfigFile = new File(globalDir, "globalsetspawn.properties");
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
}
