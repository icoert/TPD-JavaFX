package Controllers;

import Models.Files;
import Models.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FilesController implements Initializable {
    @FXML
    private TableView<Files> tableViewFiles;

    @FXML
    private TableColumn<Files, String> tableFile;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnUpload;

    private Users user;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        tableViewFiles.setRowFactory(tv -> {
            TableRow<Files> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try {
                        Files rowData = row.getItem();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/FileView.fxml"));
                        AnchorPane fileView= loader.load();

                        FileController controller = loader.getController();
                        controller.initData(user, rowData.getFileName());


                        Scene fileActionScene = new Scene(fileView);
                        Stage fileViewStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                        fileViewStage.setTitle("File Viewer");
                        fileViewStage.setScene(fileActionScene);
                        fileViewStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });

        showFiles();
    }

    private void showFiles(){
        try (Socket socket = new Socket("localhost", 5000)) {
            String option = "getFiles/";

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(option);

            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> result = (ArrayList<String>) objectInput.readObject();
            ArrayList<Files> newArray = new ArrayList<>();
            for(int i = 0; i < result.size(); i++){
                newArray.add(new Files(result.get(i))) ;
            }

            tableFile.setCellValueFactory(new PropertyValueFactory<>("FileName"));
            ObservableList<Files> newList = FXCollections.observableArrayList(newArray);
            tableViewFiles.setItems(newList);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void uploadAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);


        if (selectedFile != null) {
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] data = new byte[(int) selectedFile.length()];
            fis.read(data);
            fis.close();


            String content = new String(data, "UTF-8");
            try (Socket socket = new Socket("localhost", 5000)) {
                String option = "uploadFile/" + selectedFile.getName() + "/" + content;

                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                objectOutput.writeObject(option);

                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                Object result = objectInput.readObject();

                if((Boolean) result == true){
                    showFiles();
                }
            } catch (IOException  | ClassNotFoundException e) {
                System.out.println("Client Error: " + e.getMessage());
            }
        } else {
            System.out.println("File is not valid for upload!");
        }
    }

    @FXML
    public void showUsersAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/UsersView.fxml"));
        SplitPane usersView = loader.load();

        UsersController controller = loader.getController();
        controller.showUsers();
        controller.initData(user);

        Scene usersActionScene = new Scene(usersView);
        Stage usersViewStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        usersViewStage.setTitle("Users Viewer");
        usersViewStage.setScene(usersActionScene);
        usersViewStage.show();
    }

    protected void initData(Users userConnected) {
        user = userConnected;

        Boolean adminAccess = user.getAdmin();
        Boolean uploadAccess = user.getUpload();

        if (!adminAccess){
            btnUsers.setVisible(false);
        }

        if (!uploadAccess){
            btnUpload.setVisible(false);
        }
    }
}
