package smartthings.dataaccess;
import java.sql.*;
public class DataAccess {
    private String server = "localhost:1433";
    private String database = "smart_things";
    private String user = "sa";
    private String password = "";
    public Connection connection;

    public Connection getConnection(){
        try {
            String Connectionurl="jdbc:sqlserver://"+server+";DatabaseName="+database+";user="+user+";password="+password+";encrypt=true;trustServerCertificate=true";
            connection = DriverManager.getConnection(Connectionurl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
