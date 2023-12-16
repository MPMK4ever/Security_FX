package application;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitMenuButton;
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
	@FXML
	private SplitMenuButton settingsButton;
	@FXML
	private MenuItem saveButton;
	@FXML
	private MenuItem loadButton;

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

	private static final String SALT = "ThisIsSalt";

	private static Cipher cipher;

	public void initialize(URL url, ResourceBundle rb) {

		// Load settings after login
		String keySize = ConfigManager.loadEncryptionSettings();
		if (keySize != null) {
			txt_key.setText(keySize);
		}

		// Initialize menu items for the SplitMenuButton
		MenuItem saveItem = new MenuItem("Save Settings");
		saveItem.setOnAction(event -> saveSettings());

		MenuItem loadItem = new MenuItem("Load Settings");
		loadItem.setOnAction(event -> loadSettings());

		settingsButton.getItems().addAll(saveItem, loadItem);
	}

	@FXML
	public void saveSettings() {
		String keySize = txt_key.getText();
		ConfigManager.saveEncryptionSettings(keySize);
	}

	public void loadSettings() {
		String keySize = ConfigManager.loadEncryptionSettings();
		if (keySize != null) {
			txt_key.setText(keySize);
		}
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
				alert.setContentText("3DES key must be 24 characters long.");
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

			int shift = Integer.parseInt(key);
			String encryptedData = caesarCipher.cipherEncrypt(data, shift);
			txtArea_result_cipher.setText("Encrypted message: " + encryptedData);
		} catch (NumberFormatException e) {
			showAlert("Invalid shift value. Please enter a valid number.", "Error");
		}
	}

	private void showAlert(String content, String title) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText(content);
		alert.setHeaderText(null);
		alert.setTitle(title);
		alert.showAndWait();
	}

	@FXML
	private void buttonDecryptCipher(MouseEvent event) {

		String message = txt_data_cipher.getText().trim();
		String keyString = txt_key_cipher.getText().trim();

		if (message.isEmpty()) {
			showAlert("Message cannot be empty", "Decrypt");
			return;
		}

		if (keyString.isEmpty()) {
			showAlert("Shift key cannot be empty", "Decrypt");
			return;
		}

		try {

			int shift = Integer.parseInt(keyString);
			String decryptedMessage = caesarCipher.cipherDecrypt(message, shift);
			txtArea_result_cipher.setText("Decrypted message: " + decryptedMessage);
		} catch (NumberFormatException e) {
			showAlert("Invalid shift value. Please enter a valid number.", "Decrypt");
		}
	}

}
