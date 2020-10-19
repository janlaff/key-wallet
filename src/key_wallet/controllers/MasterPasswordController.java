package key_wallet.controllers;

public class MasterPasswordController {
    private static MasterPasswordController instance;
    private MasterPasswordController() {}

    public static MasterPasswordController getInstance() {
        if (instance == null) {
            instance = new MasterPasswordController();
        }
        return instance;
    }

    public String getMasterPassword() {
        // TODO: validate access and prompt for password if necessary
        return "MasterPassword";
    }
}
