package save;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller {

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
    private TableView<Customer> tableViewCustomers;

    @FXML
    private TableColumn<Customer, String> tableClient;

    @FXML
    private TableColumn<Customer, String> tableBirthday;

    @FXML
    private TableColumn<Customer, String> tableCity;

    @FXML
    public void insertAction(ActionEvent event) {
        Control control = null;
        String value = null;
        try {
            Customer customer = new Customer()
                    .setName(tfClient.getText());
            {//Set Date of Birth
                LocalDate datePicker = tfBirthday.getValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
                value = datePicker.format(formatter);
                customer.setBirthDate(value);
                value = null;
            }

            customer.setCity(
                    tfCity.getText()
            );
            tableViewCustomers.getItems().add(customer);
        } catch(Throwable e) {
            if(control != null) {
                control.requestFocus();
                if(value != null)
                    ((TextInputControl) control).selectPositionCaret(value.length());
            }
            return;
        }
        {//Reset all text fields
            tfClient.setText(null);
            tfBirthday.setValue(null);
            tfCity.setText(null);
        }
    }

    @FXML
    public void cancelAction(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }



}
