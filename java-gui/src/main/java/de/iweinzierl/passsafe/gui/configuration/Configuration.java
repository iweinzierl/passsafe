package de.iweinzierl.passsafe.gui.configuration;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class Configuration {

    public static final String DEFAULT_CONFIGURATION_FOLDER = System.getenv("HOME") + "/" + ".passsafe";

    public static final String DEFAULT_CONFIGURATION_FILE = DEFAULT_CONFIGURATION_FOLDER + "/" + "config.json";

    public static final String DEFAULT_DATABASE_FILE = DEFAULT_CONFIGURATION_FOLDER + "/" + "passsafe.sqlite";

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private String configurationFile;
    private String baseFolder;
    private String database;


    public static Configuration parse(final String configuration) {
        try {
            LOGGER.info("Parse configuration '{}'", configuration);

            InputStreamReader reader = new InputStreamReader(new FileInputStream(configuration));

            Configuration config = new Gson().fromJson(reader, Configuration.class);

            if (config == null) {
                config = newDetaultConfiguration();
            } else {
                config.setConfigurationFile(configuration);
            }

            return config;
        } catch (FileNotFoundException e) {
            LOGGER.warn("No configuration found at '{}'", configuration);
            return newDetaultConfiguration();
        }
    }


    private static Configuration newDetaultConfiguration() {
        LOGGER.debug("Initialize non existing configuration");

        Configuration configuration = new Configuration();
        configuration.setBaseFolder(getOrCreateDefaultConfigurationFolder());
        configuration.setConfigurationFile(DEFAULT_CONFIGURATION_FILE);
        configuration.setDatabase(DEFAULT_DATABASE_FILE);
        configuration.save();

        return configuration;
    }


    private static String getOrCreateDefaultConfigurationFolder() {
        File file = new File(DEFAULT_CONFIGURATION_FOLDER);
        if (!file.exists()) {
            LOGGER.debug("Initialize configuration folder at '{}'", DEFAULT_CONFIGURATION_FOLDER);
            file.mkdir();
        }

        return file.getAbsolutePath();
    }


    public void save() {
        try (FileOutputStream out = new FileOutputStream(getConfigurationFile())) {
            String json = new Gson().toJson(this);
            out.write(json.getBytes());

        } catch (IOException e) {
            LOGGER.error("Unable to save configuration to '{}'", getConfigurationFile());
        }
    }


    public String getConfigurationFile() {
        return configurationFile;
    }


    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }


    public String getBaseFolder() {
        return baseFolder;
    }


    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }


    public String getDatabase() {
        return database;
    }


    public void setDatabase(String database) {
        this.database = database;
    }
}
