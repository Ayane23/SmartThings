package smartthings.model;

import smartthings.model.DeviceConfiguration;

public class AdminDeviceDTO {
    public String brandName, name, description;
    public Integer deviceId, vendorId, userCount;
    public AdminDeviceDTO(Integer deviceId, Integer vendorId, String brandName, String name, String description, Integer userCount) {
        this.deviceId = deviceId;
        this.vendorId = vendorId;
        this.brandName = brandName;
        this.name = name;
        this.description = description;
        this.userCount = userCount;
    }
}