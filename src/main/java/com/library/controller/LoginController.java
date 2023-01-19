package com.library.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;

import com.library.main.MainController;
import com.library.settings.Preferences;
import com.library.util.LibraryUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable {	
	
	@FXML
	private Label titleLabel;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	
	Preferences preferences;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		preferences = Preferences.getPreferences();		
	}
	
	@FXML
	private void handleLoginButtonAction(ActionEvent event) {
		titleLabel.setText("Library Login");
		titleLabel.setStyle("-fx-background-color: black");
		
		String username = usernameField.getText();
		String password = DigestUtils.shaHex(passwordField.getText());
		
		if (username.equals(preferences.getUsername()) && password.equals(preferences.getPassword())) {
			closeStage();
			loadMain();
		} else {
			titleLabel.setText("Bad Credentials");
			titleLabel.setStyle("-fx-background-color: #d32f2f;-fx-text-fill: white;");
		}
	}
	
	private void closeStage() {
		((Stage) usernameField.getScene().getWindow()).close();		
	}
	
	void loadMain() {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setTitle("Library");
			stage.setScene(new Scene(parent));
			LibraryUtil.setIcon(stage);
			stage.show();
		} catch (IOException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}
		
	}

	@FXML
	private void handleCancelButtonAction(ActionEvent event) {
		System.exit(0);
	}
}
