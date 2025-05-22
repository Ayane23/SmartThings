package smartthings.dataaccess;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import smartthings.model.Device;
import smartthings.model.DeviceDTO;
import smartthings.model.UserDeviceDTO;
import java.util.List;
import java.util.ArrayList;

public class UserDeviceDA extends DataAccess{

    public Boolean isDeviceHasUser(Integer deviceId){
        connection = getConnection();
        try{
            String query = "SELECT TOP 1 * FROM account_device WHERE device_id = " + deviceId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                return true;
            }else{
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    public Boolean isUserDeviceValid(Integer userDeviceId, Integer userId){
        connection = getConnection();
        try{
            String query = "SELECT TOP 1 * FROM account_device WHERE id = " + userDeviceId + 
                            "AND account_id = " + userId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                return true;
            }else{
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    public Boolean isDeviceValueValid(Integer userDeviceId, Integer value){
        connection = getConnection();
        try{
            String query = "SELECT d.id FROM device d INNER JOIN account_device ad ON d.id = ad.device_id " + 
                            "AND ad.id = " + userDeviceId + " AND max_value>= " + value + " AND min_value<=" + value;
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                return true;
            }else{
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    public Boolean isUpdateCountrySafe(List<Integer> countries){
        connection = getConnection();
        try{
            String query = "SELECT ad.id FROM account a " +
		                    "INNER JOIN country c ON a.country = c.id " +
		                    "INNER JOIN account_device ad ON ad.account_id = a.id " +
		                    "WHERE c.id NOT IN (";
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                return true;
            }else{
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    public Boolean insertUserDevice(Integer userId, Integer deviceId){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query = "INSERT INTO account_device VALUES(" + userId + "," + deviceId + 
            ", (SELECT default_value FROM device WHERE id = " + deviceId +"))";
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean deleteUserDevice(Integer userDeviceId, Integer userId){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query = "DELETE FROM account_device WHERE id = " + userDeviceId + 
                            " AND account_id = " + userId;
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<UserDeviceDTO> getUserDevice(Integer userId){
        connection = getConnection();
        List<UserDeviceDTO> result = new ArrayList<UserDeviceDTO>();
        try{
            String query = "SELECT ad.id, brand_name, device_name, device_description, current_value, min_value, max_value " + 
                            "FROM device d INNER JOIN account_device ad ON d.id = ad.device_id AND ad.account_id = " + userId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                UserDeviceDTO device = new UserDeviceDTO(rs.getInt("id"), rs.getString("brand_name"), rs.getString("device_name"), rs.getString("device_description"), rs.getInt("current_value"), rs.getInt("min_value"), rs.getInt("max_value"));
                result.add(device);
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Boolean updateUserDevice(Integer userDeviceId, Integer value){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query = "UPDATE account_device SET current_value = " + value + 
                            " WHERE id = " + userDeviceId;
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

