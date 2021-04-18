package key_wallet.core;

import org.ini4j.Ini;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Config {
    private static String CONFIG_FILENAME = "config.ini";
    private static String CONFIG_SETTINGS_HEADER = "settings";
    private static String CONFIG_DATABASE_PROP = "database";
    private static String CONFIG_THEME_PROP = "theme";

    private File databaseFile;
    private String uiTheme;

    private Config(File databaseFile, String uiTheme) {
        this.databaseFile = databaseFile;
        this.uiTheme = uiTheme;
    }

    public File getDatabaseFile() {
        return databaseFile;
    }

    public String getUiTheme() {
        return uiTheme;
    }

    public static boolean available() {
        return new File(CONFIG_FILENAME).exists();
    }

    public static Config load() throws ConfigException {
        File configFile = new File(CONFIG_FILENAME);

        try {
            Ini ini = new Ini(configFile);

            File databaseFile = new File(ini.get(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP));

            String uiTheme = ini.get(CONFIG_SETTINGS_HEADER, CONFIG_THEME_PROP);

            if (!uiTheme.equals("Light") && !uiTheme.equals("Dark")) {
                throw new ConfigException("Ui Theme (" + uiTheme + ") is invalid");
            }

            return new Config(databaseFile, uiTheme);
        } catch (IOException e) {
            throw new ConfigException("Config file found, but it is invalid");
        }
    }

    public static Config create(File databaseFile, String uiTheme) throws ConfigException {
        Ini ini = new Ini();
        ini.put(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP, databaseFile.getAbsolutePath());
        ini.put(CONFIG_SETTINGS_HEADER, CONFIG_THEME_PROP, uiTheme);

        File configFile = new File(CONFIG_FILENAME);
        try {
            ini.store(configFile);
        } catch (IOException e) {
            throw new ConfigException("Failed to store config file: " + configFile.getAbsolutePath());
        }

        return new Config(databaseFile, uiTheme);
    }
}
