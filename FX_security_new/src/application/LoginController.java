package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	@FXML
	private Button cancelButton;

	@FXML
	private Button registerButton;

	@FXML
	private Label loginMessageLabel;
	@FXML
	private TextField usernameTextField;
	@FXML
	private PasswordField PasswordField;

	public void loginButtonOnAction(ActionEvent event) {

		if (usernameTextField.getText().isBlank() == false && PasswordField.getText().isBlank() == false) {
			validateLogin();
		} else {
			loginMessageLabel.setText("Please enter username and password!");
		}
	}

	public void cancelButtonOnAction(ActionEvent event) {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	public void registerButtonOnAction(ActionEvent event) {
		try {
			String userName1 = usernameTextField.getText();
			String passWord1 = PasswordField.getText();

			if (userName1.isEmpty()) {
				loginMessageLabel.setText("Please enter the username first!");

			} else if (passWord1.isEmpty()) {
				loginMessageLabel.setText("Please enter the password first!");

			} else {
				boolean isRegistered = registerUser(userName1, passWord1);
				if (isRegistered) {
					loginMessageLabel.setText("Registration Successful!");
				} else {
					loginMessageLabel.setText("Registration Failed!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Error", "Registration Error", "An error occurred during registration.");
		}
	}

	public boolean registerUser(String userName, String password) {
		String hashPassword = hashPassword(password);

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager
					.getConnection("jdbc:mysql://127.0.0.1:3306/MSwDev2023CloudAndSecurity", "root", "");
			String sql = "INSERT INTO Users (userName, userPass) VALUES (?, ?)";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, userName);
				preparedStatement.setString(2, hashPassword);

				int rowsAffected = preparedStatement.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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

					// Open the Encryption.fxml file after successful login
					openEncryptionFXML();

				} else {
					loginMessageLabel.setText("Invalid Login. Please try again!");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void openEncryptionFXML() {
		try {

			FXMLLoader loader = new FXMLLoader(
					new File("/Users/my/git/repository3/FX_security_new/src/application/Encryption.fxml").toURI()
							.toURL()); // loading "Encryption.fxml" doesn't work on my computer. (it's keep finding
										// bin/ instead of src/)

			Parent root = loader.load();
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Data Encryption");
			stage.setMinHeight(400);
			stage.setMinWidth(520);
			stage.show();

			// Close the login window
			cancelButtonOnAction(null);

		} catch (IOException e) {
			e.printStackTrace();
			showAlert("Error", "Error loading Encryption.fxml", "An error occurred while opening the Encryption view.");
		}
	}

	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	private String hashPassword(String password) {
		try {

			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);

			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(salt);

			md.update(password.getBytes());

			byte[] hashedBytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
		return null;
	}
}
