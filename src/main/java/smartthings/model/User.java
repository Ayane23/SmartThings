package smartthings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    public String name, email, password, dob, address;
    public Integer country, id, role;

    public User(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("dob") String dob,
        @JsonProperty("address") String address,
        @JsonProperty("country") Integer country) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.address = address;
        this.country = country;
    }
    
    public User(Integer id, String password, Integer role, Integer country) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.country = country;
    }
}
    
