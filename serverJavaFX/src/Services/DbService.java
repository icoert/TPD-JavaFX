package Services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbService {
    public static Connection getConnection(){
        Connection connector;
        try{
            connector = DriverManager.getConnection("jdbc:sqlserver://;server_name=DESKTOP-VDVDFS2;databaseName=JavaUsers;integratedSecurity=true");
            return connector;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    static void executeQuery(String query) {
        Connection connector = getConnection();
        Statement st;
        try{
            st = connector.createStatement();
            st.executeUpdate(query);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
