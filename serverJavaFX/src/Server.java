import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import Models.Users;
import Models.Customers;

public class Server {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)) {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                String clientInput = (String) objectInput.readObject();
                String input = null;

                if (clientInput.indexOf("/") != -1) {
                    input = clientInput.split("/")[0];
                } else {
                    input = clientInput;
                }

                switch (input) {
                    case "checkToken": {
                        ArrayList<Users> usersList = getUsersList();


                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(usersList);
                    }
                    break;
//                    case "showCustomers": {
//                        ArrayList<Customers> customersList = getCustomersList();
//
//                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
//                        objectOutput.writeObject(customersList);
//                    }
//                    break;
//                    case "inputCustomer": {
//                        String tableClient = clientInput.split("/")[1];
//                        String tableBirthday = clientInput.split("/")[2];
//                        String tableCity = clientInput.split("/")[3];
//                        System.out.println(tableClient + ", " + tableBirthday + ", " + tableCity);
//                        addCustomer(tableClient, tableBirthday, tableCity);
//                    }
//                    break;
                }
            }

        } catch(IOException | ClassNotFoundException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }

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

    public static ArrayList<Customers> getCustomersList(){
        ArrayList<Customers> customersList = new ArrayList<>();
        Connection connector = getConnection();
        String query = "SELECT * FROM Customers";
        Statement st;
        ResultSet rs;

        try {
            st = connector.createStatement();
            rs= st.executeQuery(query);

            Customers customer;
            while(rs.next()) {
                customer = new Customers(rs.getString("Name"), rs.getDate("BirthDate"), rs.getString("Location"));

                customersList.add(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customersList;
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

    public static void addCustomer(String tableClient, String tableBirthday, String tableCity) {
        String query = "INSERT INTO Customers VALUES (\'" + tableClient + "\',\'" + tableBirthday + "\',\'" + tableCity + "\')";
        executeQuery(query);
    }

    private static void executeQuery(String query) {
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
