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
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
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
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private ColorPicker colorPicker;
	@FXML
	private Button saveColorButton;
	@FXML
	private Button loadColorButton;
	@FXML
	private Label statusMessage;

	private DatabaseConnection databaseConnection;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		databaseConnection = new DatabaseConnection();
		initializeColorSettings();

	}

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
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		String hashedPassword = hashPassword(password, salt);

		try {
			Connection connection = DriverManager
					.getConnection("jdbc:mysql://127.0.0.1:3306/MSwDev2023CloudAndSecurity", "root", "");
			String sql = "INSERT INTO Users (userName, userPass, salt) VALUES (?, ?, ?)";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, userName);
				preparedStatement.setString(2, hashedPassword);
				preparedStatement.setBytes(3, salt);

				int rowsAffected = preparedStatement.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String hashPassword(String password, byte[] salt) {
		try {
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

	public void validateLogin() {
		DatabaseConnection connectNow = new DatabaseConnection();
		Connection connectDB = connectNow.getConnection();

		String enteredUserName = usernameTextField.getText();
		String enteredPassword = PasswordField.getText();

		try {
			String sql = "SELECT userPass, salt FROM Users WHERE userName = ?";
			try (PreparedStatement preparedStatement = connectDB.prepareStatement(sql)) {
				preparedStatement.setString(1, enteredUserName);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						String storedHash = resultSet.getString("userPass");
						byte[] storedSalt = resultSet.getBytes("salt");

						if (storedSalt != null) {
							String hashedEnteredPassword = hashPassword(enteredPassword, storedSalt);
							if (hashedEnteredPassword != null && hashedEnteredPassword.equals(storedHash)) {
								loginMessageLabel.setText("Welcome!");
								openEncryptionFXML();
							} else {
								loginMessageLabel.setText("Invalid Login. Please try again!");
							}
						} else {
							loginMessageLabel.setText("Salt not found for user.");
						}
					} else {
						loginMessageLabel.setText("Username not found");
					}
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

	private void initializeColorSettings() {
		colorPicker.setOnAction(event -> {
			Color myColor = colorPicker.getValue();
			anchorPane.setBackground(new Background(new BackgroundFill(myColor, null, null)));
			statusMessage.setText("Color selected: " + myColor.toString());
		});

		saveColorButton.setOnAction(event -> {
			Color myColor = colorPicker.getValue();
			new ColorSetting(databaseConnection.getConnection()).saveColorToDatabase(myColor);
			statusMessage.setText("Color saved successfully!");
		});

		loadColorButton.setOnAction(event -> {
			Color loadedColor = new ColorSetting(databaseConnection.getConnection()).loadColorFromDatabase();
			if (loadedColor != null) {
				colorPicker.setValue(loadedColor);
				anchorPane.setBackground(new Background(new BackgroundFill(loadedColor, null, null)));
				statusMessage.setText("Color loaded successfully!");
			}
		});
	}

	@FXML
	private void handleColorPickerAction(ActionEvent event) {
		Color myColor = colorPicker.getValue();
		anchorPane.setBackground(new Background(new BackgroundFill(myColor, null, null)));
		statusMessage.setText("Color selected: " + myColor.toString());
	}

	@FXML
	private void handleSaveColorAction(ActionEvent event) {
		Color myColor = colorPicker.getValue();
		new ColorSetting(databaseConnection.getConnection()).saveColorToDatabase(myColor);
		statusMessage.setText("Color saved successfully!");
	}

	@FXML
	private void handleLoadColorAction(ActionEvent event) {
		Color loadedColor = new ColorSetting(databaseConnection.getConnection()).loadColorFromDatabase();
		if (loadedColor != null) {
			colorPicker.setValue(loadedColor);
			anchorPane.setBackground(new Background(new BackgroundFill(loadedColor, null, null)));
			statusMessage.setText("Color loaded successfully!");
		}
	}

}
