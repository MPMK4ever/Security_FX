package application;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class EncryptionController {

	// Encryption Data

	@FXML
	private TextField txt_data;
	@FXML
	private TextField txt_key;
	@FXML
	private TextArea txtArea_result;
	@FXML
	private Button btn_encrypt;
	@FXML
	private Button btn_decrypt;
	@FXML
	private RadioButton radio_aes;
	@FXML
	private RadioButton radio_3des;

	// Caesar Cipher

	@FXML
	private TextField txt_data_cipher;

	@FXML
	private TextField txt_key_cipher;

	@FXML
	private TextArea txtArea_result_cipher;

	@FXML
	private Button btn_encrypt_cipher;

	@FXML
	private Button btn_decrypt_cipher;

	private CaesarCipher caesarCipher = new CaesarCipher();

	private static final ConfigManager configManager = new ConfigManager();

	private static final String SALT = "ThisIsSalt";

	private static Cipher cipher;

	private static SecretKey secretKey;

	private static final DatabaseConnection databaseConnection = new DatabaseConnection();

	public void initialize(URL url, ResourceBundle rb) {

	}

	public static String AESencrypt(String data, String aesSECRET_KEY) {

		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			PBEKeySpec keyspec = new PBEKeySpec(aesSECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey sk = factory.generateSecret(keyspec);
			SecretKeySpec secretKeyspec = new SecretKeySpec(sk.getEncoded(), "AES");

			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeyspec, ivspec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));

		} catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException
				| InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException
				| NoSuchPaddingException e) {
			System.out.println(e);
		}
		return null;

	}

	public static String AESdecrypt(String data, String aesSECRET_KEY) {
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			PBEKeySpec keyspec = new PBEKeySpec(aesSECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			SecretKey sk = factory.generateSecret(keyspec);
			SecretKeySpec secretKeyspec = new SecretKeySpec(sk.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKeyspec, ivspec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
		} catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException
				| InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException
				| NoSuchPaddingException e) {
			System.out.println(e);
		}
		return null;
	}

	public String TDESencrypt() throws Exception {
		byte[] encryptKey = txt_key.getText().getBytes();
		DESedeKeySpec spec = new DESedeKeySpec(encryptKey);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey theKey = keyFactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		IvParameterSpec IvParameters = new IvParameterSpec(new byte[] { 12, 34, 56, 78, 90, 87, 65, 43 });
		cipher.init(Cipher.ENCRYPT_MODE, theKey, IvParameters);
		byte[] encrypted = cipher.doFinal(txt_data.getText().getBytes());
		String txt = Base64.getEncoder().encodeToString(encrypted);
		txtArea_result.setText(txt);
		return null;
	}

	public String TDESdecrypt() throws Exception {
		byte[] encryptKey = txt_key.getText().getBytes();
		DESedeKeySpec spec = new DESedeKeySpec(encryptKey);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey theKey = keyFactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		IvParameterSpec ivParameters = new IvParameterSpec(new byte[] { 12, 34, 56, 78, 90, 87, 65, 43 });
		cipher.init(Cipher.DECRYPT_MODE, theKey, ivParameters);
		byte[] original = cipher.doFinal(Base64.getDecoder().decode(txt_data.getText().getBytes()));
		String dec = new String(original);
		txtArea_result.setText(dec);
		return null;
	}

	@FXML
	private void buttonEncrypt(MouseEvent event) throws Exception {

		if (txt_key.getText().trim().isEmpty() && txt_data.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Key and Data cannot be empty");
			alert.setHeaderText(null);
			alert.setTitle("Encrypt");
			alert.showAndWait();

		} else if (txt_key.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Key cannot be empty");
			alert.setHeaderText(null);
			alert.setTitle("Encrypt");
			alert.showAndWait();

		} else if (txt_data.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Data cannot be empty");
			alert.setHeaderText(null);
			alert.setTitle("Encrypt");
			alert.showAndWait();

		} else if (radio_aes.isSelected()) {
			txtArea_result.setText(AESencrypt(txt_data.getText(), txt_key.getText()));

		} else if (radio_3des.isSelected()) {
			if (txt_key.getText().length() <= 23) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Please add your key");
				alert.setHeaderText("Wrong key size");
				alert.setTitle("Encrypt");
				alert.showAndWait();

			} else {
				TDESencrypt();
			}
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Please select an Encryption type");
			alert.setHeaderText(null);
			alert.setTitle("Encrypt");
			alert.showAndWait();
		}

	}

	@FXML
	private void buttonDecrypt(MouseEvent event) throws Exception {
		if (txt_key.getText().trim().isEmpty() && txt_data.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Key and Data cannot be empty");
			alert.setHeaderText(null);
			alert.setTitle("Decrypt");
			alert.showAndWait();
		} else if (txt_key.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Key cannot be empty");
			alert.setHeaderText(null);
			alert.setTitle("Decrypt");
			alert.showAndWait();
		} else if (txt_data.getText().trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Data cannot be empty");
			alert.setHeaderText(null);
			alert.setTitle("Decrypt");
			alert.showAndWait();
		} else if (radio_aes.isSelected()) {
			txtArea_result.setText(AESdecrypt(txt_data.getText(), txt_key.getText()));
		} else if (radio_3des.isSelected()) {
			TDESdecrypt();
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("Please select an Encryption type");
			alert.setHeaderText(null);
			alert.setTitle("Decrypt");
			alert.showAndWait();
		}
	}

	@FXML
	private void radio_aes(MouseEvent event) {
		radio_3des.setSelected(false);
	}

	@FXML
	private void radio_tdes(MouseEvent event) {
		radio_aes.setSelected(false);
	}

//	// save KeyFile using encrpted key
//
//	public void saveKeyFile(String fileName) throws Exception {
//		try {
//			// Generate a secure random key for encrypting the secret key
//			SecretKey secureRandomKey = generateSecureRandomKey();
//
//			// Create a cipher for encrypting the secret key
//			cipher = Cipher.getInstance("AES");
//			cipher.init(Cipher.ENCRYPT_MODE, secureRandomKey);
//
//			// Encrypt the secret key
//			byte[] encryptedKeyBytes = cipher.doFinal(this.secretKey.getEncoded());
//
//			// Save the encrypted key to the file
//			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
//				oos.writeObject(encryptedKeyBytes);
//			}
//		} catch (Exception e) {
//			showAlert(Alert.AlertType.ERROR, "Error", "Error saving key to the file.");
//			e.printStackTrace();
//		}
//	}

//	private SecretKey generateSecureRandomKey() throws NoSuchAlgorithmException {
//		// Use a secure random number generator to generate the key
//		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//		SecureRandom secureRandom = new SecureRandom();
//		keyGenerator.init(secureRandom);
//		return keyGenerator.generateKey();
//	}

	// Save encrypted data to the database
	@FXML
	private void saveToDatabase(MouseEvent event) throws Exception {
		try (Connection connection = databaseConnection.getConnection()) {
			if (connection != null) {
				String encryptedData = txtArea_result.getText();
				String insertQuery = "INSERT INTO EncryptedData (data) VALUES (?)";
				try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
					preparedStatement.setString(1, encryptedData);
					preparedStatement.executeUpdate();
					showAlert(Alert.AlertType.INFORMATION, "Success", "Data saved to the database.");
				}
			}
		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "Error", "Error saving data to the database.");
			e.printStackTrace();
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setContentText(content);
		alert.setHeaderText(null);
		alert.setTitle(title);
		alert.showAndWait();
	}

	// Linking methods to FXML elements
	@FXML
	private void handleEncryptCaesar() {
		String message = txt_data_cipher.getText();
		int shift = Integer.parseInt(txt_key_cipher.getText());

		String encryptedMessage = caesarCipher.cipherEncrypt(message, shift);
		txtArea_result_cipher.setText("Encrypted message: " + encryptedMessage);
	}

	@FXML
	private void handleDecryptCaesar() {
		String message = txt_data_cipher.getText();
		int shift = Integer.parseInt(txt_key_cipher.getText());

		String decryptedMessage = caesarCipher.cipherDecrypt(message, shift);
		txtArea_result_cipher.setText("Decrypted message: " + decryptedMessage);
	}

	@FXML
	private void buttonEncryptCaesar(MouseEvent event) throws Exception {
		// Check if the key or data is empty and display an alert if needed
		// This is standard validation for input fields
		String key = txt_key_cipher.getText().trim();
		String data = txt_data_cipher.getText().trim();

		if (key.isEmpty() && data.isEmpty()) {
			showAlert("Key and Data cannot be empty", "Encrypt");
			return;
		} else if (key.isEmpty()) {
			showAlert("Key cannot be empty", "Encrypt");
			return;
		} else if (data.isEmpty()) {
			showAlert("Data cannot be empty", "Encrypt");
			return;
		}

		try {
			// Convert the shift key to an integer
			int shift = Integer.parseInt(key);

			// Encrypt the data using the Caesar cipher
			String encryptedData = caesarCipher.cipherEncrypt(data, shift);

			// Display the encrypted data in the TextArea
			txtArea_result_cipher.setText("Encrypted message: " + encryptedData);
		} catch (NumberFormatException e) {
			showAlert("Invalid shift value. Please enter a valid number.", "Error");
		}
	}

	// Utility method to show alert dialogs
	private void showAlert(String content, String title) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText(content);
		alert.setHeaderText(null);
		alert.setTitle(title);
		alert.showAndWait();
	}

	@FXML
	private void buttonDecryptCipher(MouseEvent event) {
		// Retrieve the message and shift key from the text fields
		String message = txt_data_cipher.getText().trim();
		String keyString = txt_key_cipher.getText().trim();

		// Validate inputs: message and shift key should not be empty
		if (message.isEmpty()) {
			showAlert("Message cannot be empty", "Decrypt");
			return;
		}

		if (keyString.isEmpty()) {
			showAlert("Shift key cannot be empty", "Decrypt");
			return;
		}

		try {
			// Convert the shift key string to an integer
			int shift = Integer.parseInt(keyString);

			// Decrypt the message using the Caesar cipher
			String decryptedMessage = caesarCipher.cipherDecrypt(message, shift);

			// Display the decrypted message in the TextArea
			txtArea_result_cipher.setText("Decrypted message: " + decryptedMessage);
		} catch (NumberFormatException e) {
			// Handle cases where the shift key is not a valid integer
			showAlert("Invalid shift value. Please enter a valid number.", "Decrypt");
		}
	}

}
