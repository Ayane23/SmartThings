package smartthings.controller;

import ratpack.core.handling.*;
import ratpack.core.jackson.*;
import smartthings.util.Encryption;
import smartthings.dataaccess.SessionDA;
import smartthings.dataaccess.DeviceDA;
import smartthings.model.*;
import java.util.List;
import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceController {
    
    private DeviceDA deviceDA;
    private SessionDA sessionDA;

    public DeviceController(){
        deviceDA = new DeviceDA();
        sessionDA = new SessionDA();
    }

    public void createDevice(Context ctx) {
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=2){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        ctx.parse(fromJson(Device.class)).then(device ->{
            if(device.countries.size()<1 || !validateCountries(device.countries)){
                ctx.getResponse().status(400);
                ctx.render(json(new GeneralResponse("Invalid country")));
                return;
            }
            device.vendorId = session.userId;
            var isSucceed = deviceDA.insertDevice(device);
            if(isSucceed){
                ctx.getResponse().status(201);
                ctx.render(json(new GeneralResponse("Device created successfully")));
            }else{
                ctx.getResponse().status(500);
                ctx.render(json(new GeneralResponse("Failed to create new device")));
            }
        });
    }

    public boolean validateCountries(List<Integer> countries){
        for(var country : countries)
            if(country>252 || country <0)
                return false;
        return true;
    }

    public void getAllDevices(Context ctx) {
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=2){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var result = deviceDA.getDevicesByVendorId(session.userId);
        ctx.render(json(new GetAllDevicesResponse(result)));
    }

    public void deleteDevice(Context ctx) {
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=2){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var sDeviceId = ctx.getRequest().getQueryParams().get("deviceId");
        if(sDeviceId == null){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Missing device id")));
            return;
        }
        var deviceId = Integer.parseInt(sDeviceId);
        var vendorId = deviceDA.getDeviceVendorIdByDeviceId(deviceId);
        if(vendorId == null || vendorId != session.userId){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Device not found")));
            return;
        }
        if(deviceDA.isDeviceHasUser(deviceId)){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Device still got active user in the system")));
            return;
        }
        var isSucceed = deviceDA.deleteDevice(deviceId);
        if(isSucceed){
            ctx.getResponse().status(201);
            ctx.render(json(new GeneralResponse("Device deleted successfully")));
        }else{
            ctx.getResponse().status(500);
            ctx.render(json(new GeneralResponse("Failed to delete device")));
        }
    }
}

class GetAllDevicesResponse{
    @JsonProperty("devices")
    List<DeviceDTO> devices;
    public GetAllDevicesResponse(List<DeviceDTO> devices){
        this.devices = devices;
    }
}