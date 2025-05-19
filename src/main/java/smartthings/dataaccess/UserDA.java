package smartthings.dataaccess;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import smartthings.model.User;
import smartthings.model.UserDTO;
import smartthings.model.UserDetailDTO;
import java.util.List;
import java.util.ArrayList;

public class UserDA extends DataAccess{
    public boolean insertUser(User user){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query ="INSERT INTO account " +
                "(email ,password ,name ,dob ,address ,country ,role) VALUES " +
                "('" + user.email + "' ,'" + user.password + "' ,'" + user.name + 
                "' ,'" + user.dob + "' ,'" + user.address + "' ," + user.country + " ,3)";
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByEmail(String email){
        connection = getConnection();
        try{
            String query = "SELECT TOP 1 * FROM account WHERE email = '" + email + "'";
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                User user = new User();
                user.id = rs.getInt("id");
                user.password = rs.getString("password");
                user.role = rs.getInt("role");
                user.country = rs.getInt("country");
                return user;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<UserDTO> getAllUser(){
        connection = getConnection();
        List<UserDTO> result = new ArrayList<UserDTO>();
        try{
            String query = "SELECT a.id, a.email, c.name, COUNT(ad.id) AS device_count FROM account a INNER JOIN country c ON a.country = c.id AND a.role=3 LEFT JOIN account_device ad ON ad.account_id = a.id GROUP BY a.id, a.email, c.name";
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                UserDTO device = new UserDTO(rs.getInt("id"), rs.getString("email"), rs.getString("name"), rs.getInt("device_count"));
                result.add(device);
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public UserDetailDTO getUserDetail(Integer userId){
        connection = getConnection();
        try{
            String query = "SELECT a.id, email, a.name, a.dob, a.address, c.name AS country FROM account a INNER JOIN country c ON a.country = c.id AND a.id = " + userId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                return new UserDetailDTO(rs.getInt("id"), rs.getString("email"), rs.getString("name"), rs.getString("country"), rs.getString("dob"), rs.getString("address"));
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}