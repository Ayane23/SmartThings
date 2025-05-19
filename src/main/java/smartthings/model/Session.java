package smartthings.model;

public class Session {
    public String token;
    public Integer role, userId, country;
    public Session(String token, Integer role, Integer userId, Integer country){
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.country = country;
    }
}