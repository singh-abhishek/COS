package com.ideas.sso;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import com.ideas.domain.Employee;

public class ExtractPhoto {
	
	public static void main(String[] args) {
		WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
		IWindowsAccount account = provider.lookupAccount("idnais");
		String requestedFields = "employeeID,sn,givenName,mail";
		ActiveDirectoryUserInfo userInfo = null;
		Employee employeeDetails = null;
		userInfo = new ActiveDirectoryUserInfo(account.getFqn(), requestedFields);
	}

}
