package com.ideas.sso;

import com.ideas.domain.Employee;
import com4j.COM4J;
import com4j.ComException;
import com4j.Variant;
import com4j.typelibs.activeDirectory.IADs;
import com4j.typelibs.ado20.ClassFactory;
import com4j.typelibs.ado20.Fields;
import com4j.typelibs.ado20._Command;
import com4j.typelibs.ado20._Connection;
import com4j.typelibs.ado20._Recordset;

public class ActiveDirectoryUserInfo {
	static String defaultNamingContext = null;
	private Fields userData;
	private Employee userDetails;

	public ActiveDirectoryUserInfo(String username, String requestedFields) {
		initNamingContext();
		_Connection connection = ClassFactory.createConnection();
		connection.provider("ADsDSOObject");
		connection.open("Active Directory Provider", "", "", -1);
		_Command command = ClassFactory.createCommand();
		command.activeConnection(connection);
		String searchField = "userPrincipalName";
		int pSlash = username.indexOf('\\');
		if (pSlash > 0) {
			searchField = "sAMAccountName";
			username = username.substring(pSlash + 1);
		}
		command.commandText("<LDAP://" + defaultNamingContext + ">;(" + searchField + "=" + username + ");" + requestedFields + ";subTree");
		_Recordset rs = command.execute(null, Variant.getMissing(), -1);
		if (!rs.eof()) {
			userData = rs.fields();
			if (userData != null)
				userDetails = extractUserInfo();
			else
				try {
					throw new AuthenticationError("User information not found");
				} catch (AuthenticationError e) {}
			rs.close();
			connection.close();
		} else
			try {
				throw new AuthenticationError("Username cannot be found");
			} catch (AuthenticationError e) {}
	}

	void initNamingContext() {
		if (defaultNamingContext == null) {
			IADs rootDSE = COM4J.getObject(IADs.class, "LDAP://RootDSE", null);
			defaultNamingContext = (String) rootDSE.get("defaultNamingContext");
		}
	}

	private Employee extractUserInfo() {
		String employeeID;
		String firstName;
		String lastName;
		String email;
		Object object;
		try {
			object = this.userData.item("employeeID").value();
			System.out.println("accessing...");
			System.out.println("class of thumbnailphoto is :"+this.userData.item(4).value());
			employeeID = object.toString();
		} catch (ComException e) {
			employeeID = "";
		}
		try {
			object = this.userData.item("givenName").value();
			firstName = object.toString();
			object = this.userData.item("sn").value();
			lastName = object.toString();
		} catch (ComException e) {
			firstName = "";
			lastName = "";
		}
		try {
			object = this.userData.item("mail").value();
			email = object.toString();
		} catch (ComException e) {
			email = "";
		}
		Employee userDTO = new Employee(employeeID, firstName + " " + lastName, email);
		return userDTO;
	}

	public Employee getUserDetails() {
		return this.userDetails;
	}
}