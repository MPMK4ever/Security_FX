package application;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

	public Connection connection;

	public Connection getConnection() {

		String USERNAME = "root";
		String PASSWORD = "";
		String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/MSwDev2023CloudAndSecurity";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;

	}

}
