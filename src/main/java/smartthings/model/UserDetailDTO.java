package smartthings.model;

import smartthings.model.DeviceConfiguration;

public class UserDetailDTO{
    public String email, name, country, dob, address;
    public Integer userId;
    public UserDetailDTO(Integer userId, String email, String name, String country, String dob, String address) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.country = country;
        this.dob = dob;
        this.address = address;
    }
}