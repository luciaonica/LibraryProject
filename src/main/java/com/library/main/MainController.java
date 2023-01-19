package com.library.main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.effects.JFXDepthManager;
import com.library.alert.AlertMaker;
import com.library.database.DatabaseHandler;
import com.library.util.LibraryUtil;

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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable {
	
	@FXML
	private StackPane rootPane;
	@FXML
	private HBox book_info_HBox;
	@FXML
	private HBox member_info_HBox;	
	@FXML
	private TextField bookIdInputField;
	@FXML
	private Text bookTitle;
	@FXML
	private Text bookAuthor;
	@FXML
	private Text bookStatus;
	@FXML
	private TextField memberIdInputField;
	@FXML
	private Text memberName;
	@FXML
	private Text memberMobile;
	@FXML
	private TextField bookIdForRenewSubmissionField;
	@FXML
	private ListView<String> issueDataList;
	
	boolean isReadyForSubmission = false;
	
	DatabaseHandler databaseHandler;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		databaseHandler = DatabaseHandler.getInstance();
		JFXDepthManager.setDepth(book_info_HBox, 1);
		JFXDepthManager.setDepth(member_info_HBox, 1);
	}
	
	@FXML
	private void loadAddMember(ActionEvent event) {
		loadWindow("/fxml/add_member.fxml", "Add New Member");
	}

	@FXML
	private void loadAddBook(ActionEvent event) {
		loadWindow("/fxml/add_book.fxml", "Add New Book");
	}
	
	@FXML
	private void loadMemberTable(ActionEvent event) {
		loadWindow("/fxml/list_member.fxml", "View Members");
	}

	@FXML
	private void loadBookTable(ActionEvent event) {
		loadWindow("/fxml/list_book.fxml", "View Books");
	}
	
	@FXML
	private void loadIssuedBookTable(ActionEvent event) {
		loadWindow("/fxml/list_issue_book.fxml", "View Issued Books");
	}
	
	@FXML
	private void loadSettings(ActionEvent event) {
		loadWindow("/fxml/settings.fxml", "Settings");
	}
	
	void loadWindow(String loc, String title) {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource(loc));
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setTitle(title);
			stage.setScene(new Scene(parent));
			LibraryUtil.setIcon(stage);
			stage.show();
		} catch (IOException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}		
	}
	
	@FXML
	private void loadBookInfoForIssue(ActionEvent event) {
		clearBookFields();
		
		String id = bookIdInputField.getText();
		String qu = "select * from books where id = '" + id + "'";
		ResultSet rs = databaseHandler.execQuery(qu);
		boolean flag = false;
		try {
			while(rs.next()) {
				String bTitle = rs.getString("title");
				String bAuthor = rs.getString("author");
				Boolean bStatus = rs.getBoolean("isAvail");
				
				bookTitle.setText(bTitle);
				bookAuthor.setText(bAuthor);
				bookStatus.setText((bStatus)? "Available" : "Not Available");
				
				flag = true;
			}
			if (!flag) {
				bookTitle.setText("No Such Book Available");
			}
		} catch (SQLException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	void clearBookFields() {
		bookTitle.setText("");
		bookAuthor.setText("");
		bookStatus.setText("");
	}
	
	void clearMemberFields() {
		memberName.setText("");
		memberMobile.setText("");
	}
	
	@FXML
	private void loadMemberInfo(ActionEvent event) {
		clearMemberFields();
		
		String id = memberIdInputField.getText();
		String qu = "select * from members where id = '" + id + "'";
		ResultSet rs = databaseHandler.execQuery(qu);
		Boolean flag = false;
		try {
			while(rs.next()) {
				String mName = rs.getString("name");
				String mMobile = rs.getString("mobile");
				
				memberName.setText(mName);
				memberMobile.setText(mMobile);
				
				flag = true;
			}
			if (!flag) {
				memberName.setText("No Such Member Available");
			}
		} catch (SQLException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	@FXML
	private void loadIssueOperation(ActionEvent event) {
		String memberId = memberIdInputField.getText();
		String bookId = bookIdInputField.getText();
		
		if (isBookAvailable(bookId)) {
		
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	        alert.setTitle("Confirm Issue Operation");
	        alert.setHeaderText(null);
	        alert.setContentText("Are you sure you want to issue the book " + bookTitle.getText() + " to " + memberName.getText() + "?");
	        
	        Optional<ButtonType> response = alert.showAndWait();
	        if (response.get() == ButtonType.OK) {
	        	String qu1 = "INSERT INTO issues(memberID, bookID) VALUES ( "
	        			+ "'" + memberId + "',"
	        			+ "'" + bookId + "')";
	        	String qu2 = "UPDATE books SET isAvail = false WHERE id = '" + bookId + "'";
	        	
	        	if (databaseHandler.execAction(qu1) && databaseHandler.execAction(qu2)) {
	        		AlertMaker.showSimpleAlert("Success", "Book Issue Complete");
	        	} else {
	        		AlertMaker.showErrorMessage("Failed", "Issue Operation Failed");
	        	}
	        } else {
	        	AlertMaker.showSimpleAlert("Cancelled", "Issue Operation has been Cancelled");
	        }
		} else {
			AlertMaker.showSimpleAlert("Not Available", "Book is not available");
		}
	}
	
	private boolean isBookAvailable(String bookID) {	
		String qu = "SELECT isAvail FROM books WHERE id = '" + bookID + "'";
		ResultSet rs = databaseHandler.execQuery(qu);
		Boolean isAvail = false;
		try {
			while(rs.next()) {
				isAvail = rs.getBoolean("isAvail");
			}
		} catch (SQLException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}		
		return isAvail;		
	}
	
	@FXML
	private void loadBookInfoForRenew(ActionEvent event) {
		ObservableList<String> issueData = FXCollections.observableArrayList();		
		isReadyForSubmission = false;
		
		String id = bookIdForRenewSubmissionField.getText();
		String qu = "SELECT * FROM issues WHERE bookID = '" + id + "'";
		ResultSet rs = databaseHandler.execQuery(qu);
		try {
			while(rs.next()) {
				String mBookId = id;
				String mMemberId = rs.getString("memberID");
				Timestamp mIssueTime = rs.getTimestamp("issueTime");
				int mRenewCount = rs.getInt("renewCount");
				
				issueData.add("Issue Date and Time: " + mIssueTime.toGMTString());
				issueData.add("Renew Count: " + mRenewCount);
				
				issueData.add("Book Information:-");				
				qu = "SELECT * FROM books WHERE id = '" + mBookId + "'";
				ResultSet rs1 = databaseHandler.execQuery(qu);
				
				while(rs1.next()) {
					issueData.add("Book Title: " + rs1.getString("title"));
					issueData.add("Book ID: " + rs1.getString("id"));
					issueData.add("Book Author: " + rs1.getString("author"));
					issueData.add("Book Publisher: " + rs1.getString("publisher"));
				}
				qu = "SELECT * FROM members WHERE id = '" + mMemberId + "'";
				rs1 = databaseHandler.execQuery(qu);
				
				issueData.add("Member Information:-");			
				
				while(rs1.next()) {
					issueData.add("Name: " + rs1.getString("name"));
					issueData.add("Mobile: " + rs1.getString("mobile"));
					issueData.add("Email: " + rs1.getString("email"));
				}
				
				isReadyForSubmission = true;
			}
			
		} catch (SQLException e) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
		}
		
		issueDataList.getItems().setAll(issueData);
	}
	
	@FXML
	private void loadSubmissionOperation(ActionEvent event) {
		if (!isReadyForSubmission) {
			AlertMaker.showErrorMessage("Failed", "Please select a book to submit.");
			return;
		}
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submission Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to issue the book?");
        
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {		
			String id = bookIdForRenewSubmissionField.getText();
			String qu1 = "DELETE FROM issues WHERE bookID = '" + id + "'";
			String qu2 = "UPDATE books SET isAvail = true WHERE id = '" + id + "'";
			
			if (databaseHandler.execAction(qu1) && databaseHandler.execAction(qu2)) {
				AlertMaker.showSimpleAlert("Success", "Book has been submitted");
			} else {
				AlertMaker.showErrorMessage("Failed", "Issue Operation Failed");
			}
        } else {
        	AlertMaker.showSimpleAlert("Cancelled", "Submission Operation has been Cancelled");
        }
	}
	
	@FXML
	private void loadRenewOperation(ActionEvent event) {
		if (!isReadyForSubmission) {
			AlertMaker.showErrorMessage("Failed", "Please select a book to renew.");
			return;
		}
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to renew the book?");
        
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {		
			String id = bookIdForRenewSubmissionField.getText();
			String qu = "UPDATE issues SET  issueTime = CURRENT_TIMESTAMP, renewCount = renewCount + 1 WHERE bookID = '" + id + "'";
			
			if (databaseHandler.execAction(qu)) {
				AlertMaker.showSimpleAlert("Success", "Book has been renewed");
			} else {
				AlertMaker.showErrorMessage("Failed", "Renew Operation Failed");
			}
        } else {
        	AlertMaker.showSimpleAlert("Cancelled", "Renew Operation has been Cancelled");
        }
	}
	
	@FXML
	private void handleMenuClose(ActionEvent event) {
		((Stage) rootPane.getScene().getWindow()).close();		
	}
	
	@FXML
	private void handleMenuAddBook(ActionEvent event) {
		loadWindow("/fxml/add_book.fxml", "Add New Book");		
	}
	
	@FXML
	private void handleMenuAddMember(ActionEvent event) {
		loadWindow("/fxml/add_member.fxml", "Add New Member");
	}
	
	@FXML
	private void handleMenuViewBooks(ActionEvent event) {
		loadWindow("/fxml/list_book.fxml", "View Books");
	}
	
	@FXML
	private void handleMenuViewMembers(ActionEvent event) {
		loadWindow("/fxml/list_member.fxml", "View Members");
	}
	
	@FXML
	private void handleMenuViewIssuedBooks(ActionEvent event) {
		loadWindow("/fxml/list_issue_book.fxml", "View Issued Books");
	}
	
	@FXML
	private void handleMenuSettings(ActionEvent event) {
		loadWindow("/fxml/settings.fxml", "Settings");
	}
}
