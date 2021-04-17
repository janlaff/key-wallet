package key_wallet.data;

public class Credential {
    public String description;
    public String username;
    public String password;
    public String category;

    public Credential(String description, String username, String password, String category) {
        this.description = description;
        this.username = username;
        this.password = password;
        this.category = category;
    }
}
