package monash.smarterclient;

public class Credential {
    private Object resid;
    private String passwdHash;
    private String userName;
    private String regDate;

    public Credential(String username, String passwordHash, String registerDate, Object resident){
        resid = resident;
        userName = username;
        regDate = registerDate;
        passwdHash = passwordHash;
    }
}
