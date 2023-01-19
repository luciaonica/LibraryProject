package com.library.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.library.alert.AlertMaker;
import com.library.controller.ListMemberController.Member;
import com.library.database.DatabaseHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddMemberController implements Initializable {
	
	@FXML
	private TextField nameField;
	@FXML
	private TextField idField;
	@FXML
	private TextField mobileField;
	@FXML
	private TextField emailField;
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;
	@FXML
	private AnchorPane rootPane;

	DatabaseHandler databaseHandler;
	
	private boolean isInEditMode = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		databaseHandler = DatabaseHandler.getInstance();

	}
	
	@FXML
	private void addMember(ActionEvent event) {
		String memberId = idField.getText();
		String memberName = nameField.getText();
		String memberMobile = mobileField.getText();
		String memberEmail = emailField.getText();	
		
		if (memberId.isEmpty() || memberName.isEmpty() || memberMobile.isEmpty() || memberEmail.isEmpty()) {
			AlertMaker.showErrorMessage("", "Please complete all the fields");
		}
		
		if (isInEditMode) {
			handleUpdateMember();
			return;
		}
		
		String qu = "insert into members values ("
				+ "'" + memberId + "',"
				+ "'" + memberName + "',"
				+ "'" + memberMobile + "',"
				+ "'" + memberEmail + "'"
				+ ")";
		
		if (databaseHandler.execAction(qu)) {
			AlertMaker.showSimpleAlert("", "Saved");
		} else {
			AlertMaker.showErrorMessage("", "Failed");
		}
	}

	@FXML
	private void cancel(ActionEvent event) {
		Stage stage = (Stage) rootPane.getScene().getWindow();
		stage.close();
	}

	public void inflateUI(Member member) {
		nameField.setText(member.getName());
		idField.setText(member.getId());
		idField.setEditable(false);
		mobileField.setText(member.getMobile());
		emailField.setText(member.getEmail());
		isInEditMode = true;		
	}
	
	private void handleUpdateMember() {
		Member member = new Member(nameField.getText(), idField.getText(), mobileField.getText(), emailField.getText());
		if (databaseHandler.updateMember(member)) {
			AlertMaker.showSimpleAlert("Success", "Member Updated");
		} else {
			AlertMaker.showErrorMessage("Failed", "Could not update the member");
		}
	}
}
