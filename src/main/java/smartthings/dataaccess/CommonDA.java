package smartthings.dataaccess;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import smartthings.model.User;
import smartthings.model.UserDTO;
import smartthings.model.UserDetailDTO;
import smartthings.model.Country;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonDA extends DataAccess{

    public List<Country> getCountries(){
        connection = getConnection();
        List<Country> result = new ArrayList<Country>();
        try{
            String query = "SELECT * FROM country";
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                result.add(new Country(rs.getInt("id"), rs.getString("code"), rs.getString("name")));
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}