package com.ideas.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ideas.domain.EmployeeSchedule;
import com.ideas.domain.Repository;

@WebServlet(urlPatterns = "/dashboard")
public class DashboardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Repository repository;
    private ControllerHelper helper;
    
	@Override
	public void init(ServletConfig config) throws ServletException {
		repository = (Repository) config.getServletContext().getAttribute("repository");
		helper = (ControllerHelper) config.getServletContext().getAttribute("helper");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = (String) request.getSession().getAttribute("username");
		EmployeeSchedule schedule = repository.getEmployeeSchedule(username);
		ArrayList<JSONObject> jsonObjArray= new COSServiceLayer().convertEmpScheduleToJson(schedule);
		request.setAttribute("eventScheduleArray", jsonObjArray);
		Map<String, String> shiftTimings = repository.getShiftTimings();
		List<String> inTime = getIndividualTimings(shiftTimings, "in");
		request.setAttribute("inTime", inTime);
		List<String> outTime = getIndividualTimings(shiftTimings, "out");
		request.setAttribute("outTime", outTime);
		helper.sendRequest(request, response, "Dashboard.jsp");
	}

	private List<String> getIndividualTimings(Map<String, String> shiftTimings, String slot) {
		List<String> timings = new ArrayList<String>();
		for(Entry<String, String> entry : shiftTimings.entrySet()){
			if(entry.getValue().equals(slot))
				timings.add(entry.getKey());
		}
		return timings;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = (String) request.getSession().getAttribute("username");
		String action = request.getParameter("action");
			repository.fillDefaultTimingsInEmployeeSchedule(username);
/*			if(month == 0)
				month = 12;
			int year = Integer.parseInt(request.getParameter("year"));
			repository.importSchedule(username, month, year);
*/		
		
			String events = (request.getParameter("events"));
			EmployeeSchedule schedule = null;
			schedule = new COSServiceLayer().jsonToEmployeeSchedule(events, username);
			Boolean isDone = repository.updateSchedule(schedule);
			if(isDone)
				helper.sendServerResponse(response, isDone.toString());
		
	}

}
