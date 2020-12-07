package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField tfClient;

    @FXML
    private DatePicker tfBirthday;

    @FXML
    private TextField tfCity;

    @FXML
    private Button btnInsert;

    @FXML
    private Button btnCancel;

    @FXML
    private TableView<Customers> tableViewCustomers;

    @FXML
    private TableColumn<Customers, String> tableClient;

    @FXML
    private TableColumn<Customers, Date> tableBirthday;

    @FXML
    private TableColumn<Customers, String> tableCity;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showCustomers();
    }

    public void showCustomers(){
        ArrayList<Customers> customersList = null;

        try (Socket socket = new Socket("localhost", 5000)) {
            String option = "showCustomers";
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);
//            objectOutput.flush();

            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            customersList  = (ArrayList<Customers>) objectInput.readObject();


//            System.out.println(newList.get(1));
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client Error: " + e.getMessage());
        }

        tableClient.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tableBirthday.setCellValueFactory(new PropertyValueFactory<>("BirthDate"));
        tableCity.setCellValueFactory(new PropertyValueFactory<>("Location"));

        ObservableList<Customers> newList = FXCollections.observableArrayList(customersList);

        tableViewCustomers.setItems(newList);
    }

    @FXML
    public void insertAction(ActionEvent event) {
        try (Socket socket = new Socket("localhost", 5000)) {
            String option = "inputCustomer/" + tfClient.getText() + '/' + tfBirthday.getValue() + '/' + tfCity.getText();
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);
//            objectOutput.flush();

//            System.out.println(newList.get(1));
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        }

//        addCustomer(tableClient.getText(), tableBirthday.getText(), tableCity.getText()); // send to server
        showCustomers(); // update table with the new Customer
    }

    @FXML
    public void cancelAction(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }



}
