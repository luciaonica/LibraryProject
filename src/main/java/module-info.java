module com.library {
	requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
	requires java.desktop;
	requires javafx.base;
	requires javafx.graphics;
	requires com.jfoenix;
	requires com.google.gson;
	requires org.apache.commons.codec;

    opens com.library.controller to javafx.fxml;
    opens com.library.main to javafx.fxml;
    opens com.library.settings to javafx.fxml, com.google.gson;
    
    exports com.library.controller;
    exports com.library.main;
    exports com.library.settings;
}
