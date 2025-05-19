package smartthings.model;

import smartthings.model.DeviceConfiguration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
public class DeviceDTO {
    public String brandName, name, description;
    public Integer deviceId;
    public DeviceDTO(Integer deviceId, String brandName, String name, String description) {
        this.deviceId = deviceId;
        this.brandName = brandName;
        this.name = name;
        this.description = description;
    }
}