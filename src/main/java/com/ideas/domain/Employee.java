package com.ideas.domain;

public class Employee {
	private String username;
	private String employeeID;
	private String name;
	private String emailId;
	private String mobile = null;
	private Address address = null;
	
	public Employee(String employeeID, String name, String email) {
		this.employeeID = employeeID;
		this.name = name;
		this.emailId = email.substring(0, email.indexOf("@"));
	}
	
	public Employee(String username, String mobile, Address address){
		this.username = username;
		this.mobile = mobile;
		this.address = address;
	}
	
	public Employee(String username, String name, String mobile, Address address){
		this.username = username;
		this.name = name;
		this.mobile = mobile;
		this.address = address;
	}

	public String getUsername(){
		return username;
	}
	
	public String getEmployeeID() {
		return employeeID;
	}

	public String getName() {
		return name;
	}

	public String getEmailId() {
		return emailId;
	}
	
	public String getMobile(){
		return mobile;
	}
	
	public Address getAddress(){
		return address;
	}
	
	@Override
	public boolean equals(Object other){
		if(this == null || other == null)
			return false;
		Employee that = (Employee) other;
		if(this.getClass() == that.getClass())
			return true;
		return this.employeeID.equals(that.employeeID)
				&& this.name.equals(that.name)
				&& this.emailId.equals(that.emailId);
	}
	
	public void setMobile(String mobile) {
		this.mobile=mobile;
		
	}
}