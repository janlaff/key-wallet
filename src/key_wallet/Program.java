package key_wallet;

import key_wallet.controllers.MasterPasswordController;

public class Program {
    public static void main(String[] args) {
        System.out.println(MasterPasswordController.getInstance().getMasterPassword());
    }
}