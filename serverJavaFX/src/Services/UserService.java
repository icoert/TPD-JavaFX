package Services;

import Models.Users;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static Services.DbService.*;

public class UserService {
    public static Users checkToken (String token){
        ArrayList<Users> usersList = getUsersList();

        for (Users user : usersList){
            if(user.getToken().equals(token)){
                return user;
            }
        }
        return null;
    }

    public static ArrayList<Users> getUsersList(){
        ArrayList<Users> usersList = new ArrayList<>();
        Connection connector = getConnection();
        String query = "SELECT * FROM Users";
        Statement st;
        ResultSet rs;

        try {
            st = connector.createStatement();
            rs= st.executeQuery(query);

            Users user;
            while(rs.next()) {
                user = new Users(rs.getString("Token"), rs.getString("Username"), rs.getBoolean("Admin"), rs.getBoolean("Upload"), rs.getBoolean("Download"),rs.getBoolean("Visualization"));

                usersList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public static void addUser(String tableToken, String tableUsername, String tableAdmin, String tableUpload, String tableDownload, String tableVisualization) {
        String query = "INSERT INTO Users VALUES (\'" + tableToken + "\',\'" + tableUsername + "\',\'" +
                tableAdmin + "\',\'" + tableUpload + "\',\'" +
                tableDownload + "\',\'" + tableVisualization + "\')";
        System.out.println(query);
        executeQuery(query);
    }
}
