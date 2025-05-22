package smartthings.controller;

import ratpack.core.handling.*;
import ratpack.core.jackson.*;
import smartthings.util.Encryption;
import smartthings.dataaccess.SessionDA;
import smartthings.dataaccess.DeviceDA;
import smartthings.dataaccess.UserDeviceDA;
import smartthings.model.*;
import java.util.List;
import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDeviceController {

    private DeviceDA deviceDA;
    private UserDeviceDA userDeviceDA;
    private SessionDA sessionDA;

    public UserDeviceController(){
        deviceDA = new DeviceDA();
        userDeviceDA = new UserDeviceDA();
        sessionDA = new SessionDA();
    }

    public void registerDevice(Context ctx){
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=3){
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
        if(!deviceDA.isDeviceCountryValid(deviceId, session.country)){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Device unvailable for your region")));
            return;
        }
        var isSucceed = userDeviceDA.insertUserDevice(session.userId, deviceId);
        if(isSucceed){
            ctx.getResponse().status(201);
            ctx.render(json(new GeneralResponse("Device registered successfully")));
        }else{
            ctx.getResponse().status(500);
            ctx.render(json(new GeneralResponse("Failed to register new device")));
        }
    }

    public void unregisterDevice(Context ctx){
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=3){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var sUserDeviceId = ctx.getRequest().getQueryParams().get("userDeviceId");
        if(sUserDeviceId == null){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Missing device id")));
            return;
        }
        var userDeviceId = Integer.parseInt(sUserDeviceId);
        if(!userDeviceDA.isUserDeviceValid(userDeviceId, session.userId)){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Device not found")));
            return;
        }
        var isSucceed = userDeviceDA.deleteUserDevice(userDeviceId, session.userId);
        if(isSucceed){
            ctx.getResponse().status(201);
            ctx.render(json(new GeneralResponse("Device deleted successfully")));
        }else{
            ctx.getResponse().status(500);
            ctx.render(json(new GeneralResponse("Failed to delete device")));
        }
    }

    public void getAvailableDevices(Context ctx){
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=3){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var result = deviceDA.getDevicesByCountry(session.country);
        ctx.render(json(new GetAvailableDevicesResponse(result)));
    }

    public void getRegisteredDevices(Context ctx){
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=3){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var result = userDeviceDA.getUserDevice(session.userId);
        ctx.render(json(new GetRegisteredDevicesResponse(result)));
    }

    public void updateDeviceValue(Context ctx){
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=3){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var sUserDeviceId = ctx.getRequest().getQueryParams().get("userDeviceId");
        var sDeviceNewValue = ctx.getRequest().getQueryParams().get("value");
        if(sUserDeviceId == null || sDeviceNewValue == null){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Missing device value or id")));
            return;
        }
        var userDeviceId = Integer.parseInt(sUserDeviceId);
        var deviceNewValue = Integer.parseInt(sDeviceNewValue);
        if(!userDeviceDA.isUserDeviceValid(userDeviceId, session.userId)){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Device not found")));
            return;
        }
        if(!userDeviceDA.isDeviceValueValid(userDeviceId, deviceNewValue)){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Value out of range")));
            return;
        }
        var isSucceed = userDeviceDA.updateUserDevice(userDeviceId, deviceNewValue);
        if(isSucceed){
            ctx.getResponse().status(201);
            ctx.render(json(new GeneralResponse("Device value updated successfully")));
        }else{
            ctx.getResponse().status(500);
            ctx.render(json(new GeneralResponse("Failed to update device value")));
        }
    }
}

class GetAvailableDevicesResponse{
    @JsonProperty("devices")
    List<DeviceDTO> devices;
    public GetAvailableDevicesResponse(List<DeviceDTO> devices){
        this.devices = devices;
    }
}

class GetRegisteredDevicesResponse{
    @JsonProperty("devices")
    List<UserDeviceDTO> devices;
    public GetRegisteredDevicesResponse(List<UserDeviceDTO> devices){
        this.devices = devices;
    }
}