package Controllers;

import Models.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class IntroController {

    @FXML
    private Button btnConnect;

    @FXML
    private void initialize() {
        System.out.println("Appplication is running");
    }

    @FXML
    public void connectAction(ActionEvent event) {
        ArrayList<Users> usersList = null;

        try (Socket socket = new Socket("localhost", 5000)) {
            File myObj = new File("C:\\Users\\silvi\\IdeaProjects\\TPD_Project\\clientJavaFX\\src\\Resources\\Token.txt");
            Scanner myReader = new Scanner(myObj);
            String token = myReader.next();
            String option = "checkToken/" + token;
            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);

            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            Object message = objectInput.readObject();

            System.out.println(message);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/FilesView.fxml"));
            AnchorPane filesView = (AnchorPane) loader.load();
            Scene fileActionScene = new Scene(filesView);
            Stage filesViewStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            filesViewStage.setTitle("Files Viewer");
            filesViewStage.setScene(fileActionScene);
            filesViewStage.show();

        } catch (IOException  | ClassNotFoundException e) {
            System.out.println("Client Error: " + e.getMessage());
        }
    }

}
