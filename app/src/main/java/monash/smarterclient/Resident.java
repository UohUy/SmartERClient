package monash.smarterclient;

public class Resident {
    private int resid;
    private String address;
    private String dob;
    private String email;
    private String firstName;
    private long mobile;
    private String surname;
    private int resNumber;
    private String engProvdName;
    private String postcode;

    public Resident(int resid, String address, String dob, String email, String firstName,
                    long mobile, String surname, int resNumber, String engProvdName, String postcode){
        this.resid = resid;
        this.address = address;
        this.dob = dob;
        this.email = email;
        this.firstName = firstName;
        this.mobile = mobile;
        this.surname = surname;
        this.resNumber = resNumber;
        this.engProvdName = engProvdName;
        this.postcode = postcode;
    }
}
