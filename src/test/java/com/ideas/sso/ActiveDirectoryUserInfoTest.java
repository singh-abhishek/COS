package com.ideas.sso;

import static org.junit.Assert.*;
import org.junit.Test;
import com.ideas.domain.Employee;
import com.ideas.sso.ActiveDirectoryUserInfo;
import com.ideas.sso.AuthenticationError;

public class ActiveDirectoryUserInfoTest {
	private String REQUESTEDFIELDS = "employeeID,sn,givenName,mail";
//	
//	@Test(expected = AuthenticationError.class)
//	public void SearchForNonExistingUserThrowsException() throws AuthenticationError{
//		Employee retrievedUserInfo = new ActiveDirectoryUserInfo("someUsername", REQUESTEDFIELDS).getUserDetails();
//		fail("Test was supposed to throw exception");
//	}
	
//	@Test
//	public void SearchForExistingUserReturnsCorrectDetails(){
//		String requestedFields = "employeeID,sn,givenName,mail";
//		Employee userInfo = new Employee("32560", "Sonam Rasal", "Sonam.Rasal@ideas.com");
//		Employee retrievedUserInfo = null;
//		retrievedUserInfo = new ActiveDirectoryUserInfo("ROW\\idnsor", REQUESTEDFIELDS).getUserDetails();
//		assertTrue(userInfo.equals(retrievedUserInfo));
//	}
}
