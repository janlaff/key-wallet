package key_wallet.core;

import org.ini4j.Ini;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Config {
    private static String CONFIG_FILENAME = "config.ini";
    private static String CONFIG_SETTINGS_HEADER = "settings";
    private static String CONFIG_DATABASE_PROP = "database";

    public static File getDatabaseFile() throws IOException {
        File configFile = new File(CONFIG_FILENAME);

        if (configFile.exists()) {
            Ini ini = new Ini(configFile);
            return new File(ini.get(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP));
        } else {
            // Get password file
            JFileChooser chooser = new JFileChooser("./");
            int fileChooserResult = chooser.showOpenDialog(null);

            // Exit if no file was selected
            if (fileChooserResult != 0) {
                // TODO: throw config exception
            }

            File dataFile = chooser.getSelectedFile();

            Ini ini = new Ini();
            ini.put(CONFIG_SETTINGS_HEADER, CONFIG_DATABASE_PROP, dataFile.getAbsolutePath());
            ini.store(configFile);

            return dataFile;
        }
    }
}
