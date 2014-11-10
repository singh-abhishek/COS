package com.ideas.controller;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.ideas.domain.EmployeeSchedule;

public class COSServiceLayerTest {

	@Test
	public void shouldConvertEmpScheduleToJson() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 10, 22, 18, 45, 22);
		HashMap<String, Time> eventTimeTestMap = new HashMap<String, Time>();
		TreeMap<Date, HashMap<String, Time>> eventDateTestMap = new TreeMap<Date, HashMap<String, Time>>();
		ArrayList<JSONObject> jsonObjArray = new ArrayList<JSONObject>();
		Time sqlTime = new Time(calendar.getTimeInMillis());
		eventTimeTestMap.put("In-Time", sqlTime);
		Date sqlDate = new Date(calendar.getTimeInMillis());
		eventDateTestMap.put(sqlDate, eventTimeTestMap);
		EmployeeSchedule empSchedule = new EmployeeSchedule("idnpam",
				eventDateTestMap);
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("title", "In-Time");
		jsonObject.put("allDay", false);
		jsonObject.put("start", "2014-11-22 18:45:22");
		jsonObjArray = new COSServiceLayer()
				.convertEmpScheduleToJson(empSchedule);
		JSONAssert.assertEquals(jsonObject, jsonObjArray.get(0), true);
	}

	@Test
	public void shouldConvertJsonToEmployeeSchedule() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "In-Time");
		jsonObject.put("allDay", false);
		jsonObject.put("start", "2014-11-22T18:45:22");
		String events = jsonObject.toString();
		TreeMap<Date, HashMap<String, Time>> eventDateMap = new TreeMap<Date, HashMap<String, Time>>();
		HashMap<String, Time> eventTimeMap = new HashMap<String, Time>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 10, 22, 18, 45, 22);
		calendar.set(calendar.MILLISECOND, 0);
		eventTimeMap.put("In-Time", new Time(calendar.getTimeInMillis()));
		calendar.set(2014, 10, 22, 0, 0, 0);
		eventDateMap.put(new Date(calendar.getTimeInMillis()), eventTimeMap);
		EmployeeSchedule empSchedule = new COSServiceLayer()
				.jsonToEmployeeSchedule(events, "idnais");
		EmployeeSchedule employeeScheduleSimulated = new EmployeeSchedule(
				"idnais", eventDateMap);
		assertEquals(employeeScheduleSimulated, empSchedule);

	}
	@Test
	public void shouldConvertTreeMapToJsonArray() throws Exception {
		ArrayList<JSONObject> simulatedJsonArrayList = new ArrayList<JSONObject>();
		TreeMap<Date, String> companyHolidays = new TreeMap<Date, String>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 7, 15, 00, 00, 00);
		calendar.set(calendar.MILLISECOND, 0);
		companyHolidays.put(new Date(calendar.getTimeInMillis()), "Independence Day");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", "Independence Day");
		jsonObject.put("start", new Date(calendar.getTimeInMillis()));
		jsonObject.put("textColor", "black");
		jsonObject.put("backgroundColor", "#80ACED");
		simulatedJsonArrayList.add(jsonObject);
		ArrayList<JSONObject> jsonArrayList = new COSServiceLayer().convertToJSONArray(companyHolidays);
		assertTrue(compareArray(jsonArrayList,simulatedJsonArrayList));
	}

	private boolean compareArray(ArrayList<JSONObject> first,
			ArrayList<JSONObject> second) {
		if ((first.size() != second.size()) || (first == null && second!= null) || (first != null && second== null)){
	        return false;
	    }

		 if (first == null && second == null) return true;
		 
		 for(int i=0;i<first.size();i++){
			 if(!jsonObjsAreEqual(first.get(i),second.get(i))) return false;
		 }
		 
		return true;
	}

	
	public static boolean jsonObjsAreEqual (JSONObject js1, JSONObject js2) throws JSONException {
	    if (js1 == null || js2 == null) {
	        return (js1 == js2);
	    }

	    List<String> l1 =  Arrays.asList(JSONObject.getNames(js1));
	    Collections.sort(l1);
	    List<String> l2 =  Arrays.asList(JSONObject.getNames(js2));
	    Collections.sort(l2);
	    if (!l1.equals(l2)) {
	        return false;
	    }
	    for (String key : l1) {
	        Object val1 = js1.get(key);
	        Object val2 = js2.get(key);
	        if (val1 instanceof JSONObject) {
	            if (!(val2 instanceof JSONObject)) {
	                return false;
	            }
	            if (!jsonObjsAreEqual((JSONObject)val1, (JSONObject)val2)) {
	                return false;
	            }
	        }

	        if (val1 == null) {
	            if (val2 != null) {
	                return false;
	            }
	        }  else if (!val1.equals(val2)) {
	            return false;
	        }
	    }
	    return true;
	}
}
