package com.library.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.library.alert.AlertMaker;
import com.library.database.DatabaseHandler;
import com.library.util.LibraryUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ListMemberController implements Initializable {
	
	@FXML
	private TableView<Member> tableView;
	@FXML
	private TableColumn<Member, String> idCol;
	@FXML
	private TableColumn<Member, String> nameCol;
	@FXML
	private TableColumn<Member, String> mobileCol;
	@FXML
	private TableColumn<Member, String> emailCol;
	
	@FXML
	private AnchorPane rootPane;

	ObservableList<Member> list = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCol();
		loadData();
	}
	
	private void loadData() {
		list.clear();
		
		DatabaseHandler handler = DatabaseHandler.getInstance();
		String query = " select * from members";
		ResultSet rs = handler.execQuery(query);
		try {
			while(rs.next()) {
				String name = rs.getString("name");
				String id = rs.getString("id");
				String mobile = rs.getString("mobile");
				String email = rs.getString("email");
				
				list.add(new Member(name, id, mobile, email));
			}
		} catch (SQLException e) {
			Logger.getLogger(ListBookController.class.getName()).log(Level.SEVERE, null, e);
		}
		
		tableView.getItems().setAll(list);
	}

	private void initCol() {
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
	}
	
	@FXML
	private void handleMemberEditOption(ActionEvent event) {
		Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
		if (selectedForEdit == null) {
			AlertMaker.showErrorMessage("No Member Selected", "Please select a member to edit");
			return;
		} 		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_member.fxml"));
			Parent parent = loader.load();
			
			AddMemberController controller = (AddMemberController) loader.getController();
			controller.inflateUI(selectedForEdit);
			
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setTitle("Edit Member");
			stage.setScene(new Scene(parent));
			LibraryUtil.setIcon(stage);
			stage.show();
			
			stage.setOnCloseRequest((e) -> {
				handleRefreshOption(new ActionEvent());
			});
		} catch (IOException e) {
			Logger.getLogger(ListMemberController.class.getName()).log(Level.SEVERE, null, e);
		}	
	}
	
	@FXML
	private void handleMemberDeleteOption(ActionEvent event) {
		Member selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
		if (selectedForDeletion == null) {
			AlertMaker.showErrorMessage("No Member Selected", "Please select a Member to delete");
			return;
		} 
		if (DatabaseHandler.getInstance().isMemberHasAnyBooks(selectedForDeletion)) {
			AlertMaker.showErrorMessage("Cannot be deleted", "This member has some books.");
			return;
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete " + selectedForDeletion.getName() +  "?");
        
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
        	Boolean result = DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
        	if (result) {
        		AlertMaker.showSimpleAlert("Member deleted", selectedForDeletion.getName() + " has been deleted successfully.");
        		list.remove(selectedForDeletion);
        	} else {
        		AlertMaker.showErrorMessage("Failed", selectedForDeletion.getName() + " could not be deleted");
        	}        	
        } else {
        	AlertMaker.showSimpleAlert("Cancelled", "Delete Operation has been Cancelled");
        }
	}
	
	@FXML
	private void handleRefreshOption(ActionEvent event) {
		loadData();
	}


	public static class Member {
		private final SimpleStringProperty name;
		private final SimpleStringProperty id;
		private final SimpleStringProperty mobile;
		private final SimpleStringProperty email;
		
		public Member(String name, String id, String mobile, String email) {
			this.name = new SimpleStringProperty(name);
			this.id = new SimpleStringProperty(id);
			this.mobile = new SimpleStringProperty(mobile);
			this.email = new SimpleStringProperty(email);
		}

		public String getName() {
			return name.get();
		}

		public String getId() {
			return id.get();
		}

		public String getMobile() {
			return mobile.get();
		}

		public String getEmail() {
			return email.get();
		}
	
	}

}
