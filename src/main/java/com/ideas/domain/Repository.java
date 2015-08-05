package com.ideas.domain;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Repository {
	private final Connection connection;
	
	public Repository(Connection connection) {
		if(connection == null)
			throw new IllegalArgumentException("Empty connection");
		this.connection = connection;
	}

	public boolean isEmployeeAdmin(String username) {
		try {
			ResultSet rs = connection.createStatement().executeQuery("select 1 from admin_info where username = '" + username + "'");
			if(rs.next())
				return true;
		} catch (SQLException e) {}
		return false;
	}

	public Boolean isEmployeeRegistered(String username) {
		try {
			ResultSet rs = connection.createStatement().executeQuery("select 1 from employee_info where username = '" + username + "'");
			if(rs.next())
				return true;
		} catch (SQLException e) {}
		return false;
	}

	public boolean addEmployee(Employee employee) {
		try {
			PreparedStatement insertEmployeeInfo = connection.prepareStatement("insert into employee_info values(?, ?, ?, ?, ?, ?)");
			insertEmployeeInfo.setString(1, employee.getUsername());
			insertEmployeeInfo.setString(2, employee.getName());
			insertEmployeeInfo.setString(3, employee.getAddress().getPickUpLocation());
			insertEmployeeInfo.setDouble(4, employee.getAddress().getLatitude());
			insertEmployeeInfo.setDouble(5, employee.getAddress().getLongitude());
			insertEmployeeInfo.setString(6, employee.getMobile());
			insertEmployeeInfo.executeUpdate();
		} catch (Exception e) {
		}
		return true;
	}

	public EmployeeSchedule getEmployeeSchedule(String username) {
		TreeMap<Date, HashMap<String, Time>> eventsDateMap = new TreeMap<Date, HashMap<String, Time>>();
		try {
			ResultSet rs = connection.createStatement().executeQuery("select * from employee_dashboard where username = '"	+ username + "'");
			while (rs.next()) {
				HashMap<String, Time> eventsTimeMap = new HashMap<String, Time>();
				if (!eventsDateMap.containsKey(rs.getDate(2))) {
					eventsTimeMap.put(rs.getString(3), rs.getTime(4));
					eventsDateMap.put(rs.getDate(2), eventsTimeMap);
				} else {
					eventsTimeMap = eventsDateMap.get(rs.getDate(2));
					eventsTimeMap.put(rs.getString(3), rs.getTime(4));
					eventsDateMap.put(rs.getDate(2), eventsTimeMap);
				}
				
			}
			
			Time time = new Time(0, 0, 0);
			rs = connection.createStatement().executeQuery("SELECT * FROM holidays");
			while (rs.next()) {
				HashMap<String, Time> eventsTimeMap = new HashMap<String, Time>();
				if (!eventsDateMap.containsKey(rs.getDate(1))) {
					eventsTimeMap.put(rs.getString(2), time);
					eventsDateMap.put(rs.getDate(1), eventsTimeMap);
				} else {
					eventsTimeMap = eventsDateMap.get(rs.getDate(1));
					eventsTimeMap.put(rs.getString(2), time);
					eventsDateMap.put(rs.getDate(1), eventsTimeMap);
				}
		}
			
			
		} catch (SQLException e) {}
		return new EmployeeSchedule(username, eventsDateMap);
	}

	public void fillDefaultTimingsInEmployeeSchedule(String username) {
		try {
			CallableStatement procCall = connection.prepareCall("{call fillDefaultTiming(?)}");
			procCall.setString(1, username);
			procCall.execute();
		} catch (SQLException e) {
		}
	}
	
	public boolean updateSchedule(EmployeeSchedule schedule) {
		PreparedStatement ps;
		try {
			connection.createStatement().execute(
					"delete from employee_dashboard where username = '"
							+ schedule.getUsername() + "'");
			for (Date dateKey : schedule.getEventsDateMap().keySet()) {
				for (String eventKey : schedule.getEventsDateMap().get(dateKey)
						.keySet()) {
					if (eventKey.equalsIgnoreCase("In-Time")
							|| eventKey.equalsIgnoreCase("Out-Time")) {
						ps = connection.prepareStatement("insert into employee_dashboard values(?, ?, ?, ?)");
						ps.setString(1, schedule.getUsername());
						ps.setDate(2, dateKey);
						ps.setString(3, eventKey);
						ps.setTime(4, schedule.getEventsDateMap().get(dateKey)
								.get(eventKey));
						ps.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public Map<String, String> getShiftTimings(){
		ResultSet rs;
		Map<String, String> timings = new TreeMap<String, String>();
		try {
			rs = connection.createStatement().executeQuery("select * from shift_details");
			while(rs.next())
				timings.put(rs.getString(1), rs.getString(2));
		} catch (SQLException e) {}
		return timings;
	}

	public boolean addCompanyHoliday(Date holiday, String reason){
		if(!isHolidayForCurrentYear(holiday))
			return false;
		try {
			PreparedStatement ps = connection.prepareStatement("insert into holidays values(?, ?)");
			ps.setDate(1, holiday);
			ps.setString(2, "IDeaS Holiday: "+reason);
			ps.executeUpdate();
		} catch (SQLException e) {}
		return true;
	}

	private boolean isHolidayForCurrentYear(Date holiday){
		Calendar current = Calendar.getInstance();
		Calendar proposedHoliday = Calendar.getInstance();
		proposedHoliday.setTime(holiday);
		return current.get(Calendar.YEAR) == proposedHoliday.get(Calendar.YEAR);
	}
	
	public TreeMap<Date, String> getCompanyHolidays() {
		TreeMap<Date, String> companyHolidays = new TreeMap<Date, String>();
		try {
			String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
			ResultSet rs = connection.createStatement().executeQuery("select * from holidays where holiday_date like '%" + year + "%'");
			while(rs.next())
				companyHolidays.put(rs.getDate(1), rs.getString(2));
		} catch (SQLException e) {}
		return companyHolidays;
	}

	public boolean removeCompanyHoliday(Date holiday) {
		try {
			PreparedStatement ps = connection.prepareStatement("delete from holidays where holiday_date = ?");
			ps.setDate(1, holiday);
			ps.executeUpdate();
		} catch (SQLException e) {}
		
		return true;
	}
	
	public boolean addNewShifts(String inTime, String outTime){
		boolean newInTimeFlag = true;
		boolean newOutTimeFlag = true;
		try {
			ResultSet rs;
			PreparedStatement check = connection.prepareStatement("select * from shift_details where time = ? and slot = ?");
			check.setString(1, inTime);
			check.setString(2, "in");
			rs = check.executeQuery();
			if(rs.next())
				newInTimeFlag = false;
			check.setString(1, outTime);
			check.setString(2, "out");
			rs = check.executeQuery();
			if(rs.next())
				newOutTimeFlag = false;
			PreparedStatement ps = connection.prepareStatement("insert into shift_details values(?, ?)");
			if(inTime != ""){
				ps.setString(1, inTime);
				ps.setString(2, "in");
				ps.executeUpdate();
			}
			if(outTime != ""){
				ps.setString(1, outTime);
				ps.setString(2, "out");
				ps.executeUpdate();
			}
		} catch (SQLException e) {}
		return newInTimeFlag && newOutTimeFlag;
	}
	
	public boolean removeShift(String time, String slot){
		boolean success = false;
		PreparedStatement check;
		try {
			check = connection.prepareStatement("DELETE FROM shift_details WHERE TIME=? AND slot=?");
			check.setString(1, time);
			check.setString(2, slot);
			success = check.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
		
	}
	
	public Employee getEmployeeFromUserName(String username){
		ResultSet rs;
		try {
			rs = connection.createStatement().executeQuery("select username,address,latitude,longitude,mobile,name  from employee_info where username = '" + username + "'");
			if(rs.next()){
				return new Employee(rs.getString(1), rs.getString(6), rs.getString(5), new Address(rs.getDouble(3), rs.getDouble(4), rs.getString(2)));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
		throw new RuntimeException();
	}
	
	public String getMobileForEmployee(String username) {
		ResultSet rs;
		String mobile= "";
		try {
			rs = connection.createStatement().executeQuery("select mobile from employee_info where username = '"+ username+"'");
			if(rs.next()) 
				mobile =rs.getString(1);
			
		} catch (SQLException e) {}
		return mobile;
		
	}
	
	public boolean updateMobileForEmployee(String username, String mobile) {
		try {
			PreparedStatement insertEmployeeInfo = connection.prepareStatement("update employee_info set mobile=? where username=?");
			insertEmployeeInfo.setString(1, mobile);
			insertEmployeeInfo.setString(2, username);
			insertEmployeeInfo.executeUpdate();
		} catch (Exception e) {}
		return true;
	}

}