package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

	private static final String CONFIG_FILE = "encryption_settings.txt";

	// Method to save encryption settings to a text file
	public static void saveEncryptionSettings(String keySize) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
			writer.write(keySize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to load encryption settings from a text file
	public static String loadEncryptionSettings() {
		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
