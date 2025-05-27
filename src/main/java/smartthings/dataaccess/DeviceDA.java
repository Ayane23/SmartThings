package smartthings.dataaccess;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import smartthings.model.Device;
import smartthings.model.DeviceDTO;
import smartthings.model.UserDeviceDTO;
import smartthings.model.AdminDeviceDTO;
import java.util.List;
import java.util.ArrayList;

public class DeviceDA extends DataAccess{

    public boolean insertDevice(Device device){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query = "INSERT INTO device " +
                "(vendor_id ,brand_name ,device_name ,device_description ,min_value ,max_value ,default_value) VALUES " +
                "(" + device.vendorId + " ,'" + device.brandName + "' ,'" + device.name + "' ,'" + 
                device.description + "' ," + device.deviceConfiguration.minValue + " ," +
                device.deviceConfiguration.maxValue + " ," + device.deviceConfiguration.defaultValue + ");"+
                "SELECT SCOPE_IDENTITY() AS device_id;";
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                var deviceId = rs.getInt("device_id");
                query = "INSERT INTO device_country (device_id, country_id) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                for(var country : device.countries){
                    ps.setInt(1, deviceId);
                    ps.setInt(2, country);
                    ps.addBatch();
                }
                ps.executeBatch();
                return true;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<DeviceDTO> getDevicesByVendorId(Integer vendorId){
        connection = getConnection();
        List<DeviceDTO> result = new ArrayList<DeviceDTO>();
        try{
            String query = "SELECT * FROM device WHERE vendor_id = " + vendorId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                DeviceDTO device = new DeviceDTO(rs.getInt("id"), rs.getString("brand_name"), rs.getString("device_name"), rs.getString("device_description"));
                result.add(device);
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Integer getDeviceVendorIdByDeviceId(Integer deviceId){
        connection = getConnection();
        try{
            String query = "SELECT TOP 1 * FROM device WHERE id = " + deviceId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                return rs.getInt("vendor_id");
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteDevice(Integer deviceId){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query =  "DELETE FROM device_country WHERE device_id = " + deviceId + ";" +
                            "DELETE FROM device WHERE id = " + deviceId;
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<DeviceDTO> getDevicesByCountry(Integer countryId){
        connection = getConnection();
        List<DeviceDTO> result = new ArrayList<DeviceDTO>();
        try{
            String query = "SELECT * FROM device d INNER JOIN device_country dc ON d.id=dc.device_id AND dc.country_id=" + countryId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                DeviceDTO device = new DeviceDTO(rs.getInt("id"), rs.getString("brand_name"), rs.getString("device_name"), rs.getString("device_description"));
                result.add(device);
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isDeviceCountryValid(Integer deviceId, Integer countryId){
        connection = getConnection();
        try{
            String query = "SELECT TOP 1 * FROM device_country WHERE device_id = " + deviceId + 
                            "AND country_id = "+countryId;
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

    public Integer isDeviceUpdateValid(Integer deviceId, String countries, Integer minValue, Integer maxValue){
        connection = getConnection();
        Integer result = 0;
        try{
            String query = "SELECT ad.id FROM account a " +
                            "INNER JOIN country c ON a.country = c.id " +
                            "INNER JOIN account_device ad ON ad.account_id = a.id " +
                            "WHERE c.id NOT IN (" + countries + ") AND ad.device_id = " + deviceId;
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                result += 1;
            }
            query = "SELECT id FROM account_device " +
                    "WHERE device_id = " + deviceId + 
                    " AND (current_value<" + minValue + " OR current_value>" + minValue + ")";
            rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                result += 2;
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return 4;
    }

    public boolean updateDevice(Integer deviceId, Device device, String countries){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query = "UPDATE device SET brand_name = '" + device.brandName + "', " +
                            "device_name = '" + device.name + "', " +
                            "device_description = '" + device.description + "', " +
                            "min_value = " + device.deviceConfiguration.minValue + ", " +
                            "max_value = " + device.deviceConfiguration.maxValue + ", " +
                            "default_value = " + device.deviceConfiguration.defaultValue +
                            "WHERE id = " + deviceId + "; ";
            query += "DELETE device_country WHERE country_id " +
                    "NOT IN (" + countries + ") AND device_id = " + deviceId + "; ";
            query += "INSERT INTO device_country (device_id, country_id) " +
                    "SELECT " + deviceId + " AS device_id, VALUE FROM STRING_SPLIT('" + countries + 
                    "',',') AS country_id " + "WHERE VALUE NOT IN " + 
                    "(SELECT country_id FROM device_country WHERE device_id = " + deviceId + ")";
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public List<AdminDeviceDTO> getDevicesAdmin(){
        connection = getConnection();
        List<AdminDeviceDTO> result = new ArrayList<AdminDeviceDTO>();
        try{
            String query = "SELECT d.id, d.vendor_id, d.brand_name, d.device_name, d.device_description, COUNT(ad.id) AS user_count FROM device d LEFT JOIN account_device ad ON d.id=ad.device_id GROUP BY d.id, d.vendor_id, d.brand_name, d.device_name, d.device_description";
            ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                AdminDeviceDTO device = new AdminDeviceDTO(rs.getInt("id"), rs.getInt("vendor_id"), rs.getString("brand_name"), rs.getString("device_name"), rs.getString("device_description"), rs.getInt("user_count"));
                result.add(device);
            }
            return result;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}

