package com.ideas.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import com.ideas.domain.Address;
import com.ideas.domain.Repository;
import com.ideas.domain.Employee;
import com.ideas.sso.ActiveDirectoryUserInfo;
import com.ideas.sso.AuthenticationError;

@WebServlet(urlPatterns = "/authenticate/*")
public class AuthenticationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Repository repository; 
	private ControllerHelper helper;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		repository = (Repository) config.getServletContext().getAttribute("repository");
		helper = (ControllerHelper) config.getServletContext().getAttribute("helper");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = null;
		String username = request.getRemoteUser().substring(4);		
		Employee employeeDetails = getEmployeeDetailsFromActiveDirectory(username);
		request.setAttribute("employeeDetails", employeeDetails);
		if (repository.isEmployeeAdmin(username))
			path = "/admin";
		else
			path = repository.isEmployeeRegistered(username) ? "/dashboard" : "Maps.jsp";
		helper.sendRequest(request, response, path);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getRemoteUser().substring(4);	
		Employee employeeLocationDetails = getEmployeeLocationDetailsFromRequest(request, username);
		request.setAttribute("locationDetails", employeeLocationDetails);
		Employee employeeDetails = getEmployeeDetailsFromActiveDirectory(username);
		request.setAttribute("employeeDetails", employeeDetails);
		helper.sendRequest(request, response, "/EmployeeDetails.jsp");
	}

	private Employee getEmployeeLocationDetailsFromRequest(HttpServletRequest request, String username) {
		String pickUpLocation = request.getParameter("userAddress");
		double latitude = Double.parseDouble(request.getParameter("latitude"));
		double longitude = Double.parseDouble(request.getParameter("longitude"));
		Address address = new Address(latitude, longitude, pickUpLocation);
		Employee employeeLocationDetails = new Employee(username, null, null, address);
		return employeeLocationDetails;
	}
	
	private Employee getEmployeeDetailsFromActiveDirectory(String username) {
		WindowsAuthProviderImpl provider = new WindowsAuthProviderImpl();
		IWindowsAccount account = provider.lookupAccount(username);
		String requestedFields = "employeeID,sn,givenName,mail";
		ActiveDirectoryUserInfo userInfo = null;
		Employee employeeDetails = null;
		userInfo = new ActiveDirectoryUserInfo(account.getFqn(), requestedFields);
		employeeDetails = userInfo.getUserDetails();
		String mobile = repository.getMobileForEmployee(username);
		employeeDetails.setMobile(mobile);
		return employeeDetails;
	}
}