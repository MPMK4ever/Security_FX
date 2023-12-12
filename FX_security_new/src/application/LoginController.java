package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML
	private Button cancelButton;

	@FXML
	private Label loginMessageLabel;
	@FXML
	private TextField usernameTextField;
	@FXML
	private PasswordField PasswordField;

	public void loginButtonOnAction(ActionEvent event) {

		if (usernameTextField.getText().isBlank() == false && PasswordField.getText().isBlank() == false) {
			// loginMessageLabel.setText("You tried to Login!");

			validateLogin();
		} else {
			loginMessageLabel.setText("Please enter username and password!");
		}
	}

	public void cancelButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	public void validateLogin() {

		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();

		String verifyLogin = "SELECT COUNT(1) FROM Users WHERE userName = '" + usernameTextField.getText()
				+ "' AND userPass = '" + PasswordField.getText() + "'";

		try {

			Statement statement = connectDB.createStatement();
			ResultSet queryResult = statement.executeQuery(verifyLogin);

			while (queryResult.next()) {
				if (queryResult.getInt(1) == 1) {
					loginMessageLabel.setText("Welcome!");

				} else {
					loginMessageLabel.setText("Invalid Login. Please try again!");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}
