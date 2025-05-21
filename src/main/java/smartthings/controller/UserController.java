package smartthings.controller;

import ratpack.core.handling.*;
import ratpack.core.jackson.*;
import smartthings.util.Encryption;
import smartthings.dataaccess.UserDA;
import smartthings.dataaccess.SessionDA;
import smartthings.model.*;
import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserController {
    
    private UserDA userDA;
    private SessionDA sessionDA;
    
    public UserController(){
        userDA = new UserDA();
        sessionDA = new SessionDA();
    }

    public void createUser(Context ctx) {
        ctx.parse(fromJson(User.class)).then(user ->{
            var check = userDA.getUserByEmail(user.email);
            if(check==null){
                user.password = Encryption.hash(user.password);
                var isSucceed = userDA.insertUser(user);
                if(isSucceed){
                    ctx.getResponse().status(201);
                    ctx.render(json(new GeneralResponse("User created successfully")));
                }else{
                    ctx.getResponse().status(500);
                    ctx.render(json(new GeneralResponse("Failed create user")));
                }
            }else{
                ctx.getResponse().status(400);
                ctx.render(json(new GeneralResponse("Failed email already exist")));
            }
        });
    }
    
    public void login(Context ctx) {
        ctx.parse(fromJson(User.class)).then(user ->{
            var check = userDA.getUserByEmail(user.email);
            if(check!=null && Encryption.verify(user.password, check.password)){
                var token = Encryption.generateNewToken();
                var isSucceed = sessionDA.InsertSession(token, check.id, check.role, check.country);
                if(isSucceed){
                    ctx.render(json(new LoginSucceedResponse(token)));
                }else{
                    ctx.render(json(new GeneralResponse("Failed crate token")));
                }
            }else{
                ctx.render(json(new GeneralResponse("email or password is incorrect")));
            }
        });
    }
    
    public void getUsers(Context ctx) {
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=1){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var result = userDA.getAllUser();
        ctx.render(json(new GetUsersResponse(result)));
    }
    
    public void getUser(Context ctx) {
        if(!ctx.getRequest().getHeaders().contains("token")){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var token = ctx.getRequest().getHeaders().get("token");
        var session = sessionDA.getSessionByToken(token);
        if(session==null || session.role!=1){
            ctx.getResponse().status(401);
            ctx.render("");
            return;
        }
        var sUserDeviceId = ctx.getRequest().getQueryParams().get("userId");
        if(sUserDeviceId == null){
            ctx.getResponse().status(400);
            ctx.render(json(new GeneralResponse("Missing userId")));
            return;
        }
        var userDeviceId = Integer.parseInt(sUserDeviceId);
        var result = userDA.getUserDetail(userDeviceId);
        ctx.render(json(result));
    }
}

class LoginSucceedResponse {
    public String session_token;
    public LoginSucceedResponse(String session_token) {
        this.session_token = session_token;
    }
}

class GetUsersResponse{
    @JsonProperty("users")
    List<UserDTO> users;
    public GetUsersResponse(List<UserDTO> users){
        this.users = users;
    }
}