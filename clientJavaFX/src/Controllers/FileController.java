package Controllers;

import Models.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class FileController {
    @FXML
    private TextArea tfFileContext;

    @FXML
    private Button btnDownload;

    @FXML
    private Button btnBack;

    private Users user;

    @FXML
    public void downloadAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Destination");

        File selectedDirectory = chooser.showDialog(null);

        System.out.println(selectedDirectory);

        String downloadContent = tfFileContext.getText();

        try {
            String fullFilePath = selectedDirectory + "javaFXFileSaved.txt";
            File myObj = new File(fullFilePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                try {
                    FileWriter myWriter = new FileWriter(fullFilePath);
                    myWriter.write(downloadContent);
                    myWriter.close();

                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    @FXML
    public void backAction(ActionEvent event) throws IOException {
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

    void initData(Users userConnected, String fileName) {
        tfFileContext.setEditable(false);

        user = userConnected;
        Boolean downloadAccess = user.getDownload();

        try (Socket socket = new Socket("localhost", 5000)) {
            String option = "viewFile/" + fileName;

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);

            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            Object result = objectInput.readObject();

            tfFileContext.setText((String) result);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (!downloadAccess){
            btnDownload.setVisible(false);
        }
    }

}
