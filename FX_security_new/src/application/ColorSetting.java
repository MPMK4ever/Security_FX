package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.paint.Color;

public class ColorSetting {

	private Connection connection;

	public ColorSetting(Connection connection) {
		this.connection = connection;
	}

	public static String colorToString(Color color) {
		return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
	}

	public static Color stringToColor(String strColor) {
		String[] rgb = strColor.split(",");
		return new Color(Double.parseDouble(rgb[0]), Double.parseDouble(rgb[1]), Double.parseDouble(rgb[2]), 1.0);
	}

	public void saveColorToDatabase(Color color) {
		String strColor = colorToString(color);
		try (PreparedStatement statement = connection
				.prepareStatement("UPDATE settings SET setting_value = ? WHERE setting_name = 'bgcolor'")) {
			statement.setString(1, strColor);
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Color saved successfully");
			} else {
				System.out.println("No rows affected. Check if the 'bgcolor' setting exists.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Color loadColorFromDatabase() {
		try (PreparedStatement statement = connection
				.prepareStatement("SELECT setting_value FROM settings WHERE setting_name = 'bgcolor'");
				ResultSet resultSet = statement.executeQuery()) {
			if (resultSet.next()) {
				return stringToColor(resultSet.getString("setting_value"));
			} else {
				System.out.println("No 'bgcolor' setting found in the database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
