package Controllers;

import Models.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class UsersController {

    @FXML
    private TextField tfToken;

    @FXML
    private TextField tfUsername;

    @FXML
    private CheckBox tfAdmin;

    @FXML
    private CheckBox tfUpload;

    @FXML
    private CheckBox tfDownload;

    @FXML
    private CheckBox tfVisualization;

    @FXML
    private Button btnInsert;

    @FXML
    private Button btnBack;

    @FXML
    private TableView<Users> tableViewUsers;

    @FXML
    private TableColumn<Users, String> tableToken;

    @FXML
    private TableColumn<Users, String> tableUsername;

    @FXML
    private TableColumn<Users, Boolean> tableAdmin;

    @FXML
    private TableColumn<Users, Boolean> tableUpload;

    @FXML
    private TableColumn<Users, Boolean> tableDownload;

    @FXML
    private TableColumn<Users, Boolean> tableVisualization;

    private Users user;

    @FXML
    void backAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/FilesView.fxml"));
        AnchorPane filesView = loader.load();

        Scene fileActionScene = new Scene(filesView);
        Stage filesViewStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FilesController controller = loader.getController();
        controller.initData(user);

        filesViewStage.setTitle("Files Viewer");
        filesViewStage.setScene(fileActionScene);
        filesViewStage.show();
    }

    @FXML
    void insertAction(ActionEvent event) {
        try (Socket socket = new Socket("localhost", 5000)) {
            String option = "inputUser/" +
                            tfToken.getText() + '/' +
                            tfUsername.getText() + '/' +
                            tfAdmin.isSelected() + '/' +
                            tfUpload.isSelected() + '/' +
                            tfDownload.isSelected() + '/' +
                            tfVisualization.isSelected();

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);
        } catch (IOException e) {
            e.printStackTrace();
        }

        showUsers(); // update table with the new Customer
    }

    void showUsers(){
        try (Socket socket = new Socket("localhost", 5000)) {
            String option = "showUsers/";

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);

            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            ArrayList<Users> usersList = (ArrayList<Users>) objectInput.readObject();

            tableToken.setCellValueFactory(new PropertyValueFactory<>("Token"));
            tableUsername.setCellValueFactory(new PropertyValueFactory<>("Username"));
            tableAdmin.setCellValueFactory(new PropertyValueFactory<>("Admin"));
            tableUpload.setCellValueFactory(new PropertyValueFactory<>("Upload"));
            tableDownload.setCellValueFactory(new PropertyValueFactory<>("Download"));
            tableVisualization.setCellValueFactory(new PropertyValueFactory<>("Visualization"));

            ObservableList<Users> newList = FXCollections.observableArrayList(usersList);

            tableViewUsers.setItems(newList);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }

    protected void initData(Users userConnected) {
        user = userConnected;
    }

}

