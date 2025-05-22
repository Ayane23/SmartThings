package smartthings.model;

import smartthings.model.DeviceConfiguration;
import java.util.List;

public class Device {
    public String brandName, name, description;
    public Integer vendorId;
    public DeviceConfiguration deviceConfiguration;
    public List<Integer> countries;
}