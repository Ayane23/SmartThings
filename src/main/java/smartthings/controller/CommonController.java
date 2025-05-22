package smartthings.controller;

import ratpack.core.handling.*;
import ratpack.core.jackson.*;
import smartthings.util.Encryption;
import smartthings.dataaccess.CommonDA;
import smartthings.model.*;
import static ratpack.core.jackson.Jackson.fromJson;
import static ratpack.core.jackson.Jackson.json;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonController {

    private CommonDA commonDA;

    public CommonController(){
        commonDA = new CommonDA();
    }
    
    public void getCountries(Context ctx) {
        var result = commonDA.getCountries();
        ctx.render(json(new GetCountriesResponse(result)));
    }
    
}

class GetCountriesResponse{
    @JsonProperty("countries")
    List<Country> countries;
    public GetCountriesResponse(List<Country> countries){
        this.countries = countries;
    }
}