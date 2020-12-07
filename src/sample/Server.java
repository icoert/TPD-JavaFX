package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import save.Customer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

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
                    case "showCustomers": {
                        ArrayList<Customers> customersList = getCustomersList();

                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(customersList);
                    }
                    break;
                    case "inputCustomer": {
                        String tableClient = clientInput.split("/")[1];
                        String tableBirthday = clientInput.split("/")[2];
                        String tableCity = clientInput.split("/")[3];
                        System.out.println(tableClient + ", " + tableBirthday + ", " + tableCity);
                        addCustomer(tableClient, tableBirthday, tableCity);
                    }
                    break;
                }
            }

        } catch(IOException | ClassNotFoundException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }

    public static Connection getConnection(){
        Connection connector;
        try{
            connector = DriverManager.getConnection("jdbc:sqlserver://;server_name=DESKTOP-VDVDFS2;databaseName=RentC;integratedSecurity=true");
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
