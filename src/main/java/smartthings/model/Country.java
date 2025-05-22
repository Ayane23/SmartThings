package smartthings.model;

public class Country {
    public String code, name;
    public Integer id;
    
    public Country(Integer id, String code, String name){
        this.id = id;
        this.code = code;
        this.name = name;
    }
}