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

    public static Config loadConfig() throws ConfigException {
        File configFile = new File(CONFIG_FILENAME);

        if (configFile.exists()) {
            try {
                Ini ini = new Ini(configFile);

                File databaseFile = new File(ini.get(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP));
                String uiTheme = ini.get(CONFIG_SETTINGS_HEADER, CONFIG_THEME_PROP);

                return new Config(databaseFile, uiTheme);
            } catch (IOException e) {
                throw new ConfigException("Config file found, but it is invalid");
            }
        } else {
            return createConfig();
        }
    }

    public static Config createConfig() throws ConfigException {
        // Get password file
        JFileChooser chooser = new JFileChooser("./");
        int fileChooserResult = chooser.showOpenDialog(null);

        // Exit if no file was selected
        if (fileChooserResult != 0) {
            throw new ConfigException("Abort due to missing cancel password dialog");
        }

        File dataFile = chooser.getSelectedFile();

        // Get theme
        String[] options = new String[] { "Light", "Dark" };

        int themeSelection = JOptionPane.showOptionDialog(
                null,
                "Select your default ui theme",
                "Ui Theme Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        Ini ini = new Ini();
        ini.put(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP, dataFile.getAbsolutePath());
        ini.put(CONFIG_SETTINGS_HEADER, CONFIG_THEME_PROP, options[themeSelection]);
        try {
            ini.store(new File(CONFIG_FILENAME));
        } catch (IOException e) {
            throw new ConfigException("Failed to store config file");
        }

        return new Config(dataFile, options[themeSelection]);
    }
}
