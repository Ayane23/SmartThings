package smartthings.model;

import smartthings.model.DeviceConfiguration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
public class UserDeviceDTO{
    public String brandName, name, description;
    public Integer userDeviceId, currentValue, minValue, maxValue;
    public UserDeviceDTO(Integer userDeviceId, String brandName, String name, String description, Integer currentValue, Integer minValue, Integer maxValue) {
        this.userDeviceId = userDeviceId;
        this.brandName = brandName;
        this.name = name;
        this.description = description;
        this.currentValue = currentValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}