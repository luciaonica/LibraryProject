package com.library.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.library.alert.AlertMaker;
import com.library.controller.ListBookController.Book;
import com.library.database.DatabaseHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddBookController implements Initializable {
	
	@FXML
	private TextField titleField;
	@FXML
	private TextField idField;
	@FXML
	private TextField authorField;
	@FXML
	private TextField publisherField;
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
	private void addBook(ActionEvent event) {
		String bookId = idField.getText();
		String bookAuthor = authorField.getText();
		String bookPublisher = publisherField.getText();
		String bookTitle = titleField.getText();
		
		if (bookId.isEmpty() || bookAuthor.isEmpty() || bookPublisher.isEmpty() || bookTitle.isEmpty()) {
			AlertMaker.showErrorMessage("", "Please complete all the fields");
		}
		
		if (isInEditMode) {
			handleEditOperation();
			return;
		}
		
		String qu = "insert into books values ("
				+ "'" + bookId + "',"
				+ "'" + bookTitle + "',"
				+ "'" + bookAuthor + "',"
				+ "'" + bookPublisher + "',"
				+ true + ")";
		
		if (databaseHandler.execAction(qu)) {
			AlertMaker.showSimpleAlert("", "Success");
		} else {
			AlertMaker.showErrorMessage("", "Failed");
		}
	}

	@FXML
	private void cancel(ActionEvent event) {
		Stage stage = (Stage) rootPane.getScene().getWindow();
		stage.close();
	}
	
	public void inflateUI(Book book) {
		titleField.setText(book.getTitle());
		idField.setText(book.getId());
		idField.setEditable(false);
		authorField.setText(book.getAuthor());
		publisherField.setText(book.getPublisher());
		isInEditMode = true;
	}
	
	private void handleEditOperation() {
		Book book = new Book(titleField.getText(), idField.getText(), authorField.getText(), publisherField.getText(), true);
		if (databaseHandler.updateBook(book)) {
			AlertMaker.showSimpleAlert("Success", "Book Updated");
		} else {
			AlertMaker.showErrorMessage("Failed", "Could not update the book");
		}
	}
}
