package com.library.controller;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.library.database.DatabaseHandler;
import com.library.settings.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ListIssueBookController implements Initializable {
	
	@FXML
	private TableView<IssueBook> tableView;
	@FXML
	private TableColumn<IssueBook, String> idCol;
	@FXML
	private TableColumn<IssueBook, String> titleCol;
	@FXML
	private TableColumn<IssueBook, String> authorCol;
	@FXML
	private TableColumn<IssueBook, String> memberCol;
	@FXML
	private TableColumn<IssueBook, Date> issueTimeCol;
	@FXML
	private TableColumn<IssueBook, Integer> renewCountCol;
	@FXML
	private TableColumn<IssueBook, Date> dueDateCol;

	ObservableList<IssueBook> list = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initCol();
		loadData();
	}
	
	private void loadData() {
		DatabaseHandler handler = DatabaseHandler.getInstance();
		String query = "SELECT i.bookID, i.issueTime, i.renewCount, m.name, b.title, b.author \n"
				+ "FROM issues i inner join members m inner join books b\n"
				+ "where i.memberID=m.id and i.bookID=b.id;";
		ResultSet rs = handler.execQuery(query);
		try {
			while(rs.next()) {
				String title = rs.getString("title");
				String id = rs.getString("bookID");
				String author = rs.getString("author");
				String member = rs.getString("name");
				Date issueTime = rs.getDate("issueTime");
				int renewCount = rs.getInt("renewCount");
				
				Calendar cal = Calendar.getInstance();
		        cal.setTime(issueTime);
		        
		        Preferences preferences = Preferences.getPreferences();
		        
		        cal.add(Calendar.DATE, preferences.getnDaysWithoutFine());
		        Date dueDate = new Date(cal.getTime().getTime());
						
				list.add(new IssueBook(title, id, author, member, issueTime, renewCount, dueDate));
			}
		} catch (SQLException e) {
			Logger.getLogger(ListIssueBookController.class.getName()).log(Level.SEVERE, null, e);
		}
		
		tableView.getItems().setAll(list);
	}

	private void initCol() {
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
		memberCol.setCellValueFactory(new PropertyValueFactory<>("member"));
		issueTimeCol.setCellValueFactory(new PropertyValueFactory<>("issueTime"));		
		renewCountCol.setCellValueFactory(new PropertyValueFactory<>("renewCount"));	
		dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));	
	}

	public static class IssueBook {
		private final String title;
		private final String id;
		private final String author;
		private final String member;
		private final Date issueTime;
		private final Integer renewCount;
		private final Date dueDate;
		
		public IssueBook(String title, String id, String author, String member, Date issueTime, int renewCount, Date dueDate) {
			this.title = title;
			this.id = id;
			this.author = author;
			this.member = member;
			this.issueTime = issueTime;
			this.renewCount = renewCount;
			this.dueDate = dueDate;
		}

		public String getTitle() {
			return title;
		}

		public String getId() {
			return id;
		}

		public String getAuthor() {
			return author;
		}

		public String getMember() {
			return member;
		}

		public Date getIssueTime() {
			return issueTime;
		}
		
		public int getRenewCount() {
			return renewCount;
		}
		
		public Date getDueDate() {
			return dueDate;
		}
	}

}
