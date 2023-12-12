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
			Class.forName("com.mysql.cj.jdbc.Driver"); // this is the connection string for MySQL
			connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
//			Statement statement = connection.createStatement();
//
////			String userName = "minyoo";
////			String userPass = "1234";
//
//			String sql = "SELECT * FROM Users WHERE userName = \"" + userName + "\" and userPass = \"" + userPass
//					+ "\"";
//			System.out.println(sql);
//
//			ResultSet resultSet = statement.executeQuery(sql);
//
//			if (resultSet.next()) {
//				//
//				System.out.println("Successful login ");
//			} else {
//				System.out.println("Unsuccessful login ");
//			}
//
//			// Close external resources
//			resultSet.close();
//			statement.close();
//			connection.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;

	}

}
