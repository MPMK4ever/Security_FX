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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class EncryptionController {

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

	private static final String SALT = "ThisIsSalt";
	private static Cipher cipher;

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
}
