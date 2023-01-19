package com.library.settings;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController implements Initializable {
	
	@FXML
	private TextField nDaysWithoutFine;
	@FXML
	private TextField finePerDay;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initDefaultValues();

	}

	@FXML
	private void handleSaveButtonAction(ActionEvent event) {
		Preferences preferences = Preferences.getPreferences();
		preferences.setnDaysWithoutFine(Integer.parseInt(nDaysWithoutFine.getText()));
		preferences.setFinePerDay(Float.parseFloat(finePerDay.getText()));
		preferences.setUsername(username.getText());
		preferences.setPassword(password.getText());
		
		Preferences.writePreferencesToFile(preferences);
	}

	@FXML
	private void handleCancelButtonAction(ActionEvent event) {
		Stage stage = (Stage) username.getScene().getWindow();
		stage.close();
	}
	
	private void initDefaultValues() {
		Preferences preferences = Preferences.getPreferences();
		nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
		finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
		username.setText(String.valueOf(preferences.getUsername()));
		password.setText(String.valueOf(preferences.getPassword()));
	}
}
