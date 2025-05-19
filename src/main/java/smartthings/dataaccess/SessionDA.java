package smartthings.dataaccess;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import smartthings.model.Session;

public class SessionDA extends DataAccess{
    public boolean InsertSession(String token, int userId, int role, int country){
        connection = getConnection();
        try{
            Statement stmt = connection.createStatement();
            String query = "INSERT INTO session VALUES('" + token + 
                "', " + userId + ", " + role + ", "+country+", DATEADD(HOUR, 1, GETDATE()))";
            stmt.executeUpdate(query);
            return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Session getSessionByToken(String token){
        connection = getConnection();
        try{
            String query = "SELECT TOP 1 * FROM session WHERE token = '" + token + "'";
            ResultSet rs = connection.createStatement().executeQuery(query);
            if(rs.next()){
                Session session = new Session(rs.getString("token"), rs.getInt("role"), rs.getInt("account_id"), rs.getInt("country"));
                return session;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
