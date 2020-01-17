package com.moneydance.modules.features.jasperreports;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.moneydance.modules.features.databeans.AccountBean;
import com.moneydance.modules.features.mrbutil.MRBDirectoryUtils;

public class Database {
	private Connection conn;
	private Statement stmt;
	private Parameters params;
	private String dataDirectory;
	private File jasperDirectory;
	private int rs;

	public Database(Parameters paramsp) {
		params = paramsp;
		dataDirectory = params.getDataDirectory();
		jasperDirectory = MRBDirectoryUtils.getExtensionDataDirectory("jasperreports");
		try {
			Class.forName("org.h2.Driver");
		}
		catch (ClassNotFoundException a) {
			OptionMessage.displayMessage("Database Driver Not Found");
			a.printStackTrace();			
		}
		
		try {
			conn = DriverManager.getConnection("jdbc:h2:file:"+dataDirectory+"/jasperDB","sa","");
		}
		catch (SQLException e) {
			OptionMessage.displayMessage("Could not create database");
			e.printStackTrace();
		}
	}
	public void createAccount() {
		AccountBean acct = new AccountBean();
		String sql;
		sql = "Drop Table "+acct.getTableName()+" if exists";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (SQLException e) {
			OptionMessage.displayMessage("Could not drop Account Table");
			e.printStackTrace();
		}
		sql = acct.createTable();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (SQLException e) {
			OptionMessage.displayMessage("Could not create Account Table");
			e.printStackTrace();
		}
	}
	public int executeUpdate(String sql) {
		try {
			stmt = conn.createStatement();
			rs = stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (SQLException e) {
			OptionMessage.displayMessage("SQL failed "+sql);
			e.printStackTrace();
			rs = 0;
		}
		return rs;
	}
	public void close() {
		try {
			conn.close();
		}
		catch (SQLException e) {
			OptionMessage.displayMessage("Could not close database");
			e.printStackTrace();
		}	}
}
