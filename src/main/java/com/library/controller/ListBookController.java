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
import com.library.main.MainController;
import com.library.util.LibraryUtil;

import javafx.beans.property.SimpleBooleanProperty;
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

public class ListBookController implements Initializable {
	
	@FXML
	private TableView<Book> tableView;
	@FXML
	private TableColumn<Book, String> idCol;
	@FXML
	private TableColumn<Book, String> titleCol;
	@FXML
	private TableColumn<Book, String> authorCol;
	@FXML
	private TableColumn<Book, String> publisherCol;
	@FXML
	private TableColumn<Book, Boolean> availablityCol;
	@FXML
	private AnchorPane rootPane;

	ObservableList<Book> list = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCol();
		loadData();
	}
	
	private void loadData() {
		list.clear();
		
		DatabaseHandler handler = DatabaseHandler.getInstance();
		String query = " select * from books";
		ResultSet rs = handler.execQuery(query);
		try {
			while(rs.next()) {
				String title = rs.getString("title");
				String id = rs.getString("id");
				String author = rs.getString("author");
				String publisher = rs.getString("publisher");
				Boolean avail = rs.getBoolean("isAvail");
				
				list.add(new Book(title, id, author, publisher, avail));
			}
		} catch (SQLException e) {
			Logger.getLogger(ListBookController.class.getName()).log(Level.SEVERE, null, e);
		}
		
		tableView.setItems(list);
	}

	private void initCol() {
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
		publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
		availablityCol.setCellValueFactory(new PropertyValueFactory<>("availablity"));		
	}
	
	@FXML
	private void handleBookEditOption(ActionEvent event) {
		Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
		if (selectedForEdit == null) {
			AlertMaker.showErrorMessage("No Book Selected", "Please select a book to edit");
			return;
		} 		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_book.fxml"));
			Parent parent = loader.load();
			
			AddBookController controller = (AddBookController) loader.getController();
			controller.inflateUI(selectedForEdit);
			
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setTitle("Edit Book");
			stage.setScene(new Scene(parent));
			LibraryUtil.setIcon(stage);
			stage.show();
			
			stage.setOnCloseRequest((e) -> {
				handleRefreshOption(new ActionEvent());
			});
		} catch (IOException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}	
		

	}
	
	@FXML
	private void handleBookDeleteOption(ActionEvent event) {
		Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
		if (selectedForDeletion == null) {
			AlertMaker.showErrorMessage("No Book Selected", "Please select a book to delete");
			return;
		} 
		if (DatabaseHandler.getInstance().isBookAlreadyIssued(selectedForDeletion)) {
			AlertMaker.showErrorMessage("Cannot be deleted", "This book is already issued and cannot be deleted.");
			return;
		}
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Book");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the book " + selectedForDeletion.getTitle() +  "?");
        
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
        	Boolean result = DatabaseHandler.getInstance().deleteBook(selectedForDeletion);
        	if (result) {
        		AlertMaker.showSimpleAlert("Book deleted", selectedForDeletion.getTitle() + " has been deleted successfully.");
        		list.remove(selectedForDeletion);
        	} else {
        		AlertMaker.showErrorMessage("Failed", selectedForDeletion.getTitle() + " could not be deleted");
        	}
        	
        } else {
        	AlertMaker.showSimpleAlert("Cancelled", "Delete Operation has been Cancelled");
        }
	}
	
	@FXML
	private void handleRefreshOption(ActionEvent event) {
		loadData();
	}

	public static class Book {
		private final SimpleStringProperty title;
		private final SimpleStringProperty id;
		private final SimpleStringProperty author;
		private final SimpleStringProperty publisher;
		private final SimpleBooleanProperty availablity;
		
		public Book(String title, String id, String author, String publisher, Boolean availablity) {
			this.title = new SimpleStringProperty(title);
			this.id = new SimpleStringProperty(id);
			this.author = new SimpleStringProperty(author);
			this.publisher = new SimpleStringProperty(publisher);
			this.availablity = new SimpleBooleanProperty(availablity);
		}

		public String getTitle() {
			return title.get();
		}

		public String getId() {
			return id.get();
		}

		public String getAuthor() {
			return author.get();
		}

		public String getPublisher() {
			return publisher.get();
		}

		public Boolean getAvailablity() {
			return availablity.get();
		}	
	}

}
