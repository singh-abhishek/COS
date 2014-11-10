package com.ideas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ideas.domain.Address;
import com.ideas.domain.Employee;
import com.ideas.domain.Repository;

@WebServlet(urlPatterns = "/employee")
public class EmployeeActionController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Repository repository;
    
	@Override
	public void init(ServletConfig config) throws ServletException {
		repository = (Repository) config.getServletContext().getAttribute("repository");
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = (String) request.getSession()
				.getAttribute("username");
		String mobile = request.getParameter("mobile");
		boolean isAdded = repository.updateMobileForEmployee(username, mobile);
		repository.fillDefaultTimingsInEmployeeSchedule(username);
		response.sendRedirect("authenticate");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = (String) request.getSession().getAttribute("username");
		String address = request.getParameter("userAddress");
		String name = request.getParameter("name");
		double latitude = Double.parseDouble(request.getParameter("latitude"));
		double longitude = Double.parseDouble(request.getParameter("longitude"));
		String mobile = request.getParameter("mobile");
		Address employeeAddress = new Address(latitude, longitude, address);
		Employee employeeDetails = new Employee(username, name, mobile, employeeAddress);
		boolean isAdded = repository.addEmployee(employeeDetails);
		repository.fillDefaultTimingsInEmployeeSchedule(username);
		response.sendRedirect("authenticate");
	}
}
