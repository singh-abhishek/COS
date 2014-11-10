package com.ideas.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeRepositorySupport {
	private Connection connection;
	
	public Repository createTableAndRepository() throws ClassNotFoundException, SQLException{
		Class.forName("org.hsqldb.jdbcDriver");
		connection = DriverManager.getConnection("jdbc:hsqldb:mem:cos", "SA", "");
		createTables(connection);
		return new Repository(connection);
	}
	
	public void createTables(Connection connection) throws SQLException{
		StringBuilder createTable = new StringBuilder();
		createTable.append("CREATE TABLE employee_info(")
		.append("username VARCHAR(10) PRIMARY KEY,")
		.append("name VARCHAR(50) NOT NULL,")
		.append("address VARCHAR(200) NOT NULL,")
		.append("latitude DOUBLE NOT NULL,")
		.append("longitude DOUBLE NOT NULL,")
		.append("mobile VARCHAR(10) NOT NULL)");
		connection.createStatement().executeUpdate(createTable.toString());
		createTable.delete(0, createTable.length());
		createTable.append("CREATE TABLE holidays (")
		.append("holiday_date DATE PRIMARY KEY,")
		.append("reason VARCHAR(50) NOT NULL)");
		connection.createStatement().executeUpdate(createTable.toString());
	}
	
	public void insert(Employee employee) throws SQLException{
		PreparedStatement insertEmployeeDetails = connection.prepareStatement("insert into employee_info values(?, ?, ?, ?, ?)");
		insertEmployeeDetails.setString(1, employee.getEmployeeID());
		insertEmployeeDetails.setString(2, employee.getName());
		insertEmployeeDetails.setString(3, employee.getAddress().getPickUpLocation());
		insertEmployeeDetails.setDouble(4, employee.getAddress().getLatitude());
		insertEmployeeDetails.setDouble(5, employee.getAddress().getLongitude());
		insertEmployeeDetails.setString(6, employee.getMobile());
		insertEmployeeDetails.executeUpdate();
	}
	
	public void cleanTable() throws SQLException{
		queryExecuter("delete from employee_info");
		queryExecuter("delete from holidays");
	}

	private void queryExecuter(String query) throws SQLException {
		connection.createStatement().executeQuery(query);
	}
	
	public void dropTable() throws SQLException{
		queryExecuter("drop table employee_info");
		queryExecuter("drop table holidays");
	}
}
