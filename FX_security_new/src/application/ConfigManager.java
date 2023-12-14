package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ConfigManager {

	private static final String CONFIG_FILE = "config.properties";

	private static final String ENCRYPTION_ALGORITHM_KEY = "encryptionAlgorithm";

	// Encrypt sensitive information before saving

	private static String encrypt(String input, String secretKey) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedData = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(encryptedData);
	}

	public static void saveSettings(String aesSecretKey, String databaseUrl, String databaseUsername,
			String databasePassword) {
		Properties properties = new Properties();

		try {
			// Encrypt sensitive information before saving
			String encryptedKey = encrypt(aesSecretKey, "YourEncryptionKey");
			String encryptedPassword = encrypt(databasePassword, "YourEncryptionKey");

			properties.setProperty("aesSecretKey", encryptedKey);
			properties.setProperty("databaseUrl", databaseUrl);
			properties.setProperty("databaseUsername", databaseUsername);
			properties.setProperty("databasePassword", encryptedPassword);

			// Save encryption algorithm preference
			properties.setProperty(ENCRYPTION_ALGORITHM_KEY, loadAlgorithmPreference());

			try (OutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
				properties.store(outputStream, "Application Configuration");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException e) {
			e.printStackTrace();
		}
	}

	public static Properties loadSettings() {
		Properties properties = new Properties();

		try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

	// Save the encryption algorithm preference
	public static void saveAlgorithmPreference(String algorithm) {
		Properties properties = loadProperties();
		properties.setProperty(ENCRYPTION_ALGORITHM_KEY, algorithm);
		saveProperties(properties);
	}

	// Load the encryption algorithm preference
	public static String loadAlgorithmPreference() {
		Properties properties = loadProperties();
		return properties.getProperty(ENCRYPTION_ALGORITHM_KEY, "AES");
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	// Helper method to save properties
	private static void saveProperties(Properties properties) {
		try (OutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
			properties.store(outputStream, "Application Configuration");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
