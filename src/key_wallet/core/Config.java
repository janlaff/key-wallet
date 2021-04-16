package key_wallet.core;

import org.ini4j.Ini;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Config {
    public static File getDatabaseFile() throws ConfigException {
        String CONFIG_FILENAME = "config.ini";
        String CONFIG_SETTINGS_HEADER = "settings";
        String CONFIG_DATABASE_PROP = "database";

        File configFile = new File(CONFIG_FILENAME);
        if (configFile.exists()) {
            try {
                Ini ini = new Ini(configFile);
                return new File(ini.get(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP));
            } catch (IOException e) {
                throw new ConfigException("Config file found, but it is invalid");
            }
        } else {
            // Get password file
            JFileChooser chooser = new JFileChooser("./");
            int fileChooserResult = chooser.showOpenDialog(null);

            // Exit if no file was selected
            if (fileChooserResult != 0) {
                throw new ConfigException("Abort due to missing cancel password dialog");
            }

            File dataFile = chooser.getSelectedFile();

            Ini ini = new Ini();
            ini.put(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP, dataFile.getAbsolutePath());
            try {
                ini.store(configFile);
            } catch (IOException e) {
                throw new ConfigException("Failed to store config file");
            }

            return dataFile;
        }
    }
}
