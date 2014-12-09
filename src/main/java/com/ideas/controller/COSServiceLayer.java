package com.ideas.controller;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONObject;
import com.ideas.domain.EmployeeSchedule;

public class COSServiceLayer {

	public ArrayList<JSONObject> convertEmpScheduleToJson(
			EmployeeSchedule schedule) {
		ArrayList<JSONObject> jsonObjArray = new ArrayList<JSONObject>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (Date dateKey : schedule.getEventsDateMap().keySet()) {
			for (String eventKey : schedule.getEventsDateMap().get(dateKey)
					.keySet()) {
				map.put("backgroundColor", "#3a87ad");
				map.put("allDay", false);
				map.put("title", eventKey);
				map.put("start", dateKey
						+ " "
						+ schedule.getEventsDateMap().get(dateKey)
								.get(eventKey));
				if(eventKey.contains("IDeaS Holiday")){
					map.put("start", dateKey);
					map.put("backgroundColor", "#330033");
					map.remove("allDay");
				}
				jsonObjArray.add(new JSONObject(map));
			}
		}
		return jsonObjArray;
	}


	public EmployeeSchedule jsonToEmployeeSchedule(String events, String username) {
		events = events + ",";
		String[] jsonArrayList = events.split("},");
		TreeMap<Date, HashMap<String, Time>> eventDateMap = new TreeMap<Date, HashMap<String, Time>>();
		for (int i = 0; i < jsonArrayList.length; i++) {
			HashMap<String, Time> eventTimeMap = new HashMap<String, Time>();
			String parsableString = jsonArrayList[i] + "}";

			HashMap<String, String> event = (HashMap<String, String>) JSON.parse(parsableString);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			java.util.Date utilDate = null;
			try {
				utilDate = format.parse(event.get("start"));
			} catch (ParseException e) {}
			Calendar cal = Calendar.getInstance(); 
			cal.setTimeInMillis(utilDate.getTime()); 
			cal.set(Calendar.HOUR_OF_DAY, 0); 
			cal.set(Calendar.MINUTE, 0); 
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0); 
			java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
			java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
			if (!eventDateMap.containsKey(sqlDate)) {
				eventTimeMap.put(event.get("title"), sqlTime);
				eventDateMap.put(sqlDate, eventTimeMap);
			} else {
				eventTimeMap = eventDateMap.get(sqlDate);
				eventTimeMap.put(event.get("title"), sqlTime);
				eventDateMap.put(sqlDate, eventTimeMap);
			}
		}
		return new EmployeeSchedule(username, eventDateMap);
	}

	public TreeMap<Time,ArrayList<ArrayList<Object>>> jsonToMap(String timesMap) {
		TreeMap<Time, ArrayList<ArrayList<Object>>> timeMap =  new TreeMap<Time, ArrayList<ArrayList<Object>>>();
		ArrayList<ArrayList<Object>> clusters = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> cluster = new ArrayList<Object>();
		String [] tokens = timesMap.split("\"");
		for(int i=1;i<tokens.length;i=i+2){
			String clusterArray= tokens[i+1].substring(1,tokens[i+1].length()-2);
			String [] clusterTokens = clusterArray.split("],");
			
			for(int j=0;j<clusterTokens.length;j++){
				clusterTokens[j]=clusterTokens[j].replace("[", "");
				clusterTokens[j]=clusterTokens[j].replace("]", "");
				if (clusterTokens[j].contains(",")){
					String[] datapointTokens = clusterTokens[j].split(",");
					double[] cordinates = new double[2];
					for(int k=0;k<datapointTokens.length;k++){
						cordinates[k] = Double.parseDouble(datapointTokens[k]);
					}
					cluster.add(cordinates);
				}
				else {
					cluster.add(Double.parseDouble(clusterTokens[j]));
					clusters.add(cluster);
					cluster = new ArrayList<Object>();
				}
			}
			
			timeMap.put(Time.valueOf(tokens[i]), clusters);
			clusters = new ArrayList<ArrayList<Object>>();
			
		}
		return timeMap;
	}
	
	public ArrayList<JSONObject> convertToJSONArray(
			TreeMap<Date, String> companyHolidays) {
		ArrayList<JSONObject> holidayList = new ArrayList<JSONObject>();
		for (Map.Entry<Date, String> entry : companyHolidays.entrySet()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("title", entry.getValue());
			jsonObject.put("start", entry.getKey());
			jsonObject.put("textColor", "black");
			jsonObject.put("backgroundColor", "#80ACED");
			holidayList.add(jsonObject);
		}
		return holidayList;
	}
}
