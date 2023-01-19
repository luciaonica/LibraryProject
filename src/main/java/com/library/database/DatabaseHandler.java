package com.library.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.library.controller.ListBookController.Book;
import com.library.controller.ListMemberController.Member;


public final class DatabaseHandler {
	private static DatabaseHandler handler = null;

	private static Connection conn = null;
	private static Statement stmt = null;

	private DatabaseHandler() {
		createConnection();	
		setupBookTable();
		setupMemberTable();
		setupIssueTable();
	}
	
	public static DatabaseHandler getInstance() {
		if (handler == null) {
			handler = new DatabaseHandler();
		}
		return handler;
	}

	private void createConnection() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_library", "root", "password");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Could not load database", "Database Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}		
	}
	
	void setupBookTable() {
		String table_name = "books";
		try {
			stmt = conn.createStatement();
			
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			
			if (tables.next()) {
				System.out.println("Table " + table_name + " already exists. Ready to go!");
			} else {
				stmt.execute("CREATE TABLE " + table_name + "("
						+ " id varchar(100) primary key, \n"
						+ " title varchar(200),\n"
						+ " author varchar(200),\n"
						+ " publisher varchar(100),\n"
						+ " isAvail boolean default true"
						+ " )");
			}
		} catch (SQLException e) {
			System.err.print(e.getMessage() + " ... setupDatabase");
		}
	}
	
	void setupMemberTable() {
		String table_name = "members";
		try {
			stmt = conn.createStatement();
			
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			
			if (tables.next()) {
				System.out.println("Table " + table_name + " already exists. Ready to go!");
			} else {
				stmt.execute("CREATE TABLE " + table_name + "("
						+ " id varchar(100) primary key, \n"
						+ " name varchar(200),\n"
						+ " mobile varchar(20),\n"
						+ " email varchar(100)"
						+ " )");
			}
		} catch (SQLException e) {
			System.err.print(e.getMessage() + " ... setupDatabase");
		}
	}
	
	void setupIssueTable() {
		String table_name = "issues";
		try {
			stmt = conn.createStatement();
			
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, table_name, null);
			
			if (tables.next()) {
				System.out.println("Table " + table_name + " already exists. Ready to go!");
			} else {
				stmt.execute("CREATE TABLE " + table_name + "("
						+ " bookID varchar(100) primary key, \n"
						+ " memberID varchar(200),\n"
						+ " issueTime timestamp default CURRENT_TIMESTAMP,\n"
						+ " renewCount integer default 0,\n"
						+ " FOREIGN KEY (bookID) REFERENCES books(id),\n"
						+ " FOREIGN KEY (memberID) REFERENCES members(id)" 
						+ " )");
			}
		} catch (SQLException e) {
			System.err.print(e.getMessage() + " ... setupDatabase");
		}
	}
	
	public ResultSet execQuery(String query) {
		ResultSet result;
		try {
			stmt = conn.createStatement();
			result = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Exception at execQuery:dataHandler " + e.getLocalizedMessage());
			return null;
		} 
		return result;
	}
	
	public boolean execAction(String qu) {
		try {
			stmt = conn.createStatement();
			stmt.execute(qu);
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error:" + e.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
			System.out.println("Exception at execQuery:dataHandler " + e.getLocalizedMessage());
			return false;
		} 
	}
	
	public boolean deleteBook(Book book) {
		try {
			String deleteStmt = "DELETE FROM books WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(deleteStmt);
			stmt.setString(1, book.getId());
			int res = stmt.executeUpdate();
			if (res == 1) {
				return true;
			}
		} catch (SQLException e) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
		}		
		
		return false;		
	}
	
	public boolean isBookAlreadyIssued(Book book) {
		try {
			String checkStmt = "SELECT COUNT(*) FROM issues WHERE bookID = ?";
			PreparedStatement stmt = conn.prepareStatement(checkStmt);
			stmt.setString(1, book.getId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				return (count > 0);
			}
		} catch (SQLException e) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
		}
		return false;			
	}
	
	public boolean isMemberHasAnyBooks(Member member) {
		try {
			String checkStmt = "SELECT COUNT(*) FROM issues WHERE memberID = ?";
			PreparedStatement stmt = conn.prepareStatement(checkStmt);
			stmt.setString(1, member.getId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				return (count > 0);
			}
		} catch (SQLException e) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
		}
		return false;			
	}
	
	public boolean updateBook(Book book) {
		try {
			String update = "UPDATE books SET title=?, author=?, publisher=? WHERE id=?";
			PreparedStatement stmt = conn.prepareStatement(update);
			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getAuthor());
			stmt.setString(3, book.getPublisher());
			stmt.setString(4, book.getId());
			int res = stmt.executeUpdate();
			return (res > 0);			
		} catch (SQLException e) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
		}				
		return false;
	}

	public Boolean deleteMember(Member member) {
		try {
			String deleteStmt = "DELETE FROM members WHERE id = ?";
			PreparedStatement stmt = conn.prepareStatement(deleteStmt);
			stmt.setString(1, member.getId());
			int res = stmt.executeUpdate();
			if (res == 1) {
				return true;
			}
		} catch (SQLException e) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
		}		
		
		return false;	
	}

	public boolean updateMember(Member member) {
		try {
			String update = "UPDATE members SET name=?, email=?, mobile=? WHERE id=?";
			PreparedStatement stmt = conn.prepareStatement(update);
			stmt.setString(1, member.getName());
			stmt.setString(2, member.getEmail());
			stmt.setString(3, member.getMobile());
			stmt.setString(4, member.getId());
			int res = stmt.executeUpdate();
			return (res > 0);			
		} catch (SQLException e) {
			Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
		}				
		return false;
	}

	
}
