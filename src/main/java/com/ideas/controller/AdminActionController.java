package com.ideas.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.ideas.domain.Repository;

@WebServlet(urlPatterns = "/admin")
public class AdminActionController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Repository repository; 
	private ControllerHelper helper;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		repository = (Repository) config.getServletContext().getAttribute("repository");
		helper = (ControllerHelper) config.getServletContext().getAttribute("helper");
	}
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		COSServiceLayer service = new COSServiceLayer();
		TreeMap<Date, String> companyHolidays = repository.getCompanyHolidays();
		ArrayList<JSONObject> holidayList = service.convertToJSONArray(companyHolidays);
		request.setAttribute("holidays", holidayList);
		Map<String, String> shiftTimings = repository.getShiftTimings();
		List<String> inTime = getIndividualTimings(shiftTimings, "in");
		request.setAttribute("inTime", inTime);
		List<String> outTime = getIndividualTimings(shiftTimings, "out");
		request.setAttribute("outTime", outTime);
		helper.sendRequest(request, response, "AdminDashboard.jsp");
	}

	private List<String> getIndividualTimings(Map<String, String> shiftTimings, String slot) {
		List<String> timings = new ArrayList<String>();
		for (Entry<String, String> entry : shiftTimings.entrySet()) {
			if (entry.getValue().equals(slot))
				timings.add(entry.getKey());
		}
		return timings;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("action").equals("addShift")) {
			String inTime = getTime(request.getParameter("start"));
			String outTime = getTime(request.getParameter("end"));
			Boolean timeAdded = repository.addNewShifts(inTime, outTime);
			helper.sendServerResponse(response, timeAdded.toString());
		} else {
			String reason = request.getParameter("title");
			long timeInMillis = Long.valueOf(request.getParameter("start"));
			Date holiday = new Date(timeInMillis);
			String action = request.getParameter("action");
			if (action.equals("add")){
				Boolean isAdded = repository.addCompanyHoliday(holiday, reason);
				helper.sendServerResponse(response, isAdded.toString());
			}
			else
				repository.removeCompanyHoliday(holiday);
		}
	}
	
	private String getTime(String time) {
		if(time != "")
			time = time.substring(0, 5);
		return time;
	}
	
}
