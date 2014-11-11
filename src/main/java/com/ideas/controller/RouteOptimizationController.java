package com.ideas.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vendorreport.CreateExcelFile;
import vendorreport.RouteReport;

import com.google.maps.model.LatLng;
import com.ideas.domain.Employee;
import com.ideas.domain.Repository;
import com.ideas.routeOptimization.DataPoint;
import com.ideas.routeOptimization.MapSolutions;
import com.ideas.routeOptimization.RouteOptimizer;

@WebServlet(urlPatterns = "/routeOptimize")
public class RouteOptimizationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RouteOptimizer routeOptimizer;
	private ControllerHelper helper;
	private Repository repository;
	private CreateExcelFile excelFile;
	


	@Override
	public void init(ServletConfig config) throws ServletException {
		routeOptimizer = (RouteOptimizer) config.getServletContext()
				.getAttribute("routeOptimizer");
		helper = (ControllerHelper) config.getServletContext().getAttribute(
				"helper");
		repository = (Repository) config.getServletContext().getAttribute(
				"repository");
		excelFile = (CreateExcelFile) config.getServletContext().getAttribute(
				"excelFile");
		
	}

	public RouteOptimizationController() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			excelFile.createExcel();
			ArrayList<TreeMap<Time, ArrayList<ArrayList<DataPoint>>>> timeMapList = routeOptimizer
					.optimizeRoute();
			ArrayList<TreeMap<Time, ArrayList<ArrayList<Object>>>> outputTimeMapList = new MapSolutions()
					.computeRoute(timeMapList);
			TreeMap<Time, ArrayList<ArrayList<Object>>> outputInTimeMap = outputTimeMapList
					.get(0);
			TreeMap<Time, ArrayList<ArrayList<Object>>> outputOutTimeMap = outputTimeMapList
					.get(1);
			TreeMap<Time, ArrayList<ArrayList<DataPoint>>> inputInTimeMap = timeMapList
					.get(0);
			TreeMap<Time, ArrayList<ArrayList<DataPoint>>> inputOutTimeMap = timeMapList
					.get(1);
			constructMapFindingNearestDatapoint(outputInTimeMap, inputInTimeMap);
			constructMapFindingNearestDatapoint(outputOutTimeMap,
					inputOutTimeMap);
			convertTimeDatapointMapToTimeEmployeeMap(outputInTimeMap);
			convertTimeDatapointMapToTimeEmployeeMap(outputOutTimeMap);
			ArrayList<TreeMap<Time, ArrayList<ArrayList<Object>>>> timeMapListToBeConsumedByPDF = new ArrayList<TreeMap<Time, ArrayList<ArrayList<Object>>>>();
			timeMapListToBeConsumedByPDF.add(outputInTimeMap);
			timeMapListToBeConsumedByPDF.add(outputOutTimeMap);
			new RouteReport().createPDF(timeMapListToBeConsumedByPDF);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	private void constructMapFindingNearestDatapoint(
			TreeMap<Time, ArrayList<ArrayList<Object>>> timeMap,
			TreeMap<Time, ArrayList<ArrayList<DataPoint>>> inputInTimeMap) {
		for (Time time : timeMap.keySet()) {
			for (int i = 0; i < timeMap.get(time).size(); i++) {
				for (int j = 0; j < timeMap.get(time).get(i).size() - 1; j++) {
					double minDistance = Double.MAX_VALUE;
					int minIndex = 0;
					double latitude2 = ((LatLng) timeMap.get(time).get(i)
							.get(j)).lat;
					double longitude2 = ((LatLng) timeMap.get(time).get(i)
							.get(j)).lng;
					for (int k = 0; k < inputInTimeMap.get(time).get(i).size(); k++) {
						double latitude = inputInTimeMap.get(time).get(i)
								.get(k).getX();
						double longitude = inputInTimeMap.get(time).get(i)
								.get(k).getY();
						double distance = distFrom(latitude, longitude,
								latitude2, longitude2);
						if (distance < minDistance) {
							minDistance = distance;
							minIndex = k;
						}
					}
					timeMap.get(time).get(i).remove(j);
					timeMap.get(time)
							.get(i)
							.add(j,
									inputInTimeMap.get(time).get(i)
											.get(minIndex));
				}
			}
		}
	}

	public TreeMap<Time, ArrayList<ArrayList<Object>>> convertTimeDatapointMapToTimeEmployeeMap(
			TreeMap<Time, ArrayList<ArrayList<Object>>> timeMap) {
		for (Time time : timeMap.keySet()) {
			for (int i = 0; i < timeMap.get(time).size(); i++) {
				for (int j = 0; j < timeMap.get(time).get(i).size() - 1; j++) {
					String username = ((DataPoint) timeMap.get(time).get(i)
							.get(j)).getObjName();
					Employee employee = repository
							.getEmployeeFromUserName(username);
					timeMap.get(time).get(i).remove(j);
					timeMap.get(time).get(i).add(j, employee);
				}
			}
		}
		return timeMap;
	}

	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
		/*
		 * double earthRadius = 6371; //kilometers double dLat =
		 * Math.toRadians(lat2-lat1); double dLng = Math.toRadians(lng2-lng1);
		 * double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		 * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		 * Math.sin(dLng/2) * Math.sin(dLng/2); double c = 2 *
		 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); double dist = (double)
		 * (earthRadius * c);
		 */
		double dist = Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2);

		return dist;
	}

}
