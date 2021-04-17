package key_wallet.data;

public class Credential {
    public String name;
    public String login;
    public String email;
    public String password;
    public String website;
    public String category;

    public enum PasswordRating {
        WEAK,
        NORMAL,
        STRONG,
    }

    public Credential(String name, String login, String email, String password, String website, String category) {
        this.name = name;
        this.login = login;
        this.email = email;
        this.password = password;
        this.website = website;
        this.category = category;
    }

    public PasswordRating ratePassword() {
        // TODO: implement password rating algorithm
        return PasswordRating.WEAK;
    }
}
