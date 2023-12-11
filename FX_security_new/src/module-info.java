module FX_security_new {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;

	opens application to javafx.graphics, javafx.fxml;
}
