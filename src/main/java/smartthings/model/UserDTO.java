package smartthings.model;

import smartthings.model.DeviceConfiguration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
public class UserDTO{
    public String email, country;
    public Integer registeredDeviceCount, userId;
    public UserDTO(Integer userId, String email, String country, Integer registeredDeviceCount) {
        this.userId = userId;
        this.email = email;
        this.country = country;
        this.registeredDeviceCount = registeredDeviceCount;
    }
}