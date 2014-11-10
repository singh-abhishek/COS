package com.ideas.routeOptimization;

import java.util.ArrayList;

public class Route {

	private String start;
	private String end;
	private ArrayList<String> wayPoints;

	public Route(String start, String end, ArrayList<String> wayPoints) {
		this.start = start;
		this.end = end;
		this.wayPoints = wayPoints;
	}
	
	public String getStart() {
		return start;
	}
	
	public String getEnd() {
		return end;
	}
	
	public ArrayList<String> getWayPoints() {
		return wayPoints;
	}

}
