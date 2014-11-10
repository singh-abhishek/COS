package com.ideas.routeOptimization;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

public class MapSolutions {
	private GeoApiContext context = new GeoApiContext();
	private final String apiKey = "AIzaSyCTlxwHduFeKFsF-xhyPxZHjyKPyKqr1BM";
	private final LatLng ideas = new LatLng(18.567054, 73.769757);
	private int loopOverTimesMap;

	public ArrayList<TreeMap<Time, ArrayList<ArrayList<Object>>>> computeRoute(
			ArrayList<TreeMap<Time, ArrayList<ArrayList<DataPoint>>>> timesMapList) throws Exception {
		this.setInitialContext();
		ArrayList<TreeMap<Time, ArrayList<ArrayList<Object>>>> timesMapOutputList = new ArrayList<TreeMap<Time, ArrayList<ArrayList<Object>>>>();
		for (loopOverTimesMap = 0; loopOverTimesMap < timesMapList.size(); loopOverTimesMap++) {
			TreeMap<Time, ArrayList<ArrayList<Object>>> timesMap = new TreeMap<Time, ArrayList<ArrayList<Object>>>();
			for (Time time : timesMapList.get(loopOverTimesMap).keySet()) {
				ArrayList<ArrayList<Object>> clusterArray = new ArrayList<ArrayList<Object>>();
				for (int loopInsideArrayOfClusters = 0; loopInsideArrayOfClusters < timesMapList
						.get(loopOverTimesMap).get(time).size(); loopInsideArrayOfClusters++) {
					ArrayList<LatLng> originLatLng = new ArrayList<LatLng>();
					for (int loopInsideOneCluster = 0; loopInsideOneCluster < timesMapList
							.get(loopOverTimesMap).get(time)
							.get(loopInsideArrayOfClusters).size(); loopInsideOneCluster++) {
						double latitutde = timesMapList.get(loopOverTimesMap)
								.get(time).get(loopInsideArrayOfClusters)
								.get(loopInsideOneCluster).getX();
						double longitude = timesMapList.get(loopOverTimesMap)
								.get(time).get(loopInsideArrayOfClusters)
								.get(loopInsideOneCluster).getY();
						originLatLng.add(new LatLng(latitutde, longitude));
					}
					ArrayList<Object> destinationsArrayAndSortableMapList = computeDistanceMatrix(originLatLng);
					String[] destinations = (String[]) destinationsArrayAndSortableMapList.get(0);
					Map<Double,String> sortableMap = (Map<Double, String>) destinationsArrayAndSortableMapList.get(1);
					Route route = computeStartpointWaypointEndpoint(destinations, sortableMap);
					ArrayList<Object> orderedLatLngWithDistance = calculateRoute(route);
					clusterArray.add(orderedLatLngWithDistance);
					Thread.sleep(600);
				}
				timesMap.put(time, clusterArray);
			}
			timesMapOutputList.add(timesMap);
		}
		return timesMapOutputList;
	}

	private ArrayList<Object> computeDistanceMatrix(ArrayList<LatLng> originLatLng)
			throws Exception {
		
		DistanceMatrixApiRequest distanceMatrixApiRequest = setDirectionMatrixApiRequest(originLatLng.toArray(new LatLng[originLatLng.size()]));
		DistanceMatrix result = distanceMatrixApiRequest.await();
		ArrayList<Object> destinationsArrayAndSortableMapList = callbackDistanceMatrixService(result);
		return destinationsArrayAndSortableMapList;
	}

	private DistanceMatrixApiRequest setDirectionMatrixApiRequest(
			LatLng[] templatLng) {
		DistanceMatrixApiRequest distanceMatrixApiRequest = new DistanceMatrixApiRequest(context);
		if (loopOverTimesMap==0){
		distanceMatrixApiRequest.origins(templatLng);
		distanceMatrixApiRequest.destinations(ideas);
		}
		else {
			distanceMatrixApiRequest.origins(ideas);
			distanceMatrixApiRequest.destinations(templatLng);
		}
		distanceMatrixApiRequest.mode(TravelMode.DRIVING);
		distanceMatrixApiRequest.units(Unit.METRIC);
		return distanceMatrixApiRequest;
	}

	private ArrayList<Object> callbackDistanceMatrixService(DistanceMatrix result) throws Exception {
		ArrayList<Object> destinationsArrayAndSortableMapList = new ArrayList<Object>();
		Map<Double, String> sortableMap = new TreeMap<Double, String>();
		ArrayList<String[]> originDestinations = new ArrayList<String[]>();
		originDestinations.add(result.originAddresses);
		originDestinations.add(result.destinationAddresses);
		sortWaypointsAccordingToDistance(result, sortableMap, originDestinations.get(loopOverTimesMap));
		destinationsArrayAndSortableMapList.add(originDestinations.get(loopOverTimesMap==0?1:0));
		destinationsArrayAndSortableMapList.add(sortableMap);
		return destinationsArrayAndSortableMapList; 
	}

	private void sortWaypointsAccordingToDistance(DistanceMatrix result,
			Map<Double, String> sortableMap, String[] places) {
		for (int i = 0; i < places.length; i++) {
			DistanceMatrixElement distanceMatrixElement = result.rows[i].elements[0];
			double distance = distanceMatrixElement.distance.inMeters;
			sortableMap.put(distance, places[i]);
		}
	}
	
	private Route computeStartpointWaypointEndpoint(String[] destinations,
			Map<Double, String> sortableMap) throws Exception {
		ArrayList<String> wayPoints = new ArrayList<String>();
		int counter = 0;
		String start = "";
		for (Double distance : sortableMap.keySet()) {
			if (counter < sortableMap.size() - 1)
				wayPoints.add(sortableMap.get(distance));
			else
				start = sortableMap.get(distance);
			counter++;
		}

		String end = destinations[0];
		Route route = loopOverTimesMap==0?(new Route(start,end,wayPoints)):(new Route(end,start,wayPoints));
		return route;
	}

	private ArrayList<Object> calculateRoute(Route route) throws Exception {

		DirectionsApiRequest directionsApiRequest = setDirectionsApiRequest(
				route.getStart(), route.getEnd(), route.getWayPoints());
		DirectionsRoute[] result = directionsApiRequest.await();
		ArrayList<Object>orderedLatLngArrayWithDistance = callbackDirectionService(result);
		return orderedLatLngArrayWithDistance;
	}

	private DirectionsApiRequest setDirectionsApiRequest(String start,
			String end, ArrayList<String> wayPoints) {
		DirectionsApiRequest directionsApiRequest = new DirectionsApiRequest(
				context);
		directionsApiRequest.origin(start);
		directionsApiRequest.destination(end);
		directionsApiRequest.waypoints(wayPoints.toArray(new String[wayPoints
				.size()]));
		directionsApiRequest.optimizeWaypoints(true);
		directionsApiRequest.mode(TravelMode.DRIVING);
		return directionsApiRequest;
	}

	private ArrayList<Object> callbackDirectionService(DirectionsRoute[] result) {
		double distance = calculateTotalDistance(result[0]);
		ArrayList<Object> orderedLatLngArrayWithDistance = createOrderedLatLngArray(result);
		orderedLatLngArrayWithDistance.add(distance);
		return orderedLatLngArrayWithDistance;
	}

	private ArrayList<Object> createOrderedLatLngArray(DirectionsRoute[] result) {
		ArrayList<Object> orderedLatLngArray = new ArrayList<>();
		for (int i=0;i<result[0].legs.length;i++){
			orderedLatLngArray.add(result[0].legs[i].startLocation);
		}
		return orderedLatLngArray;
	}

	private double calculateTotalDistance(DirectionsRoute directionsRoute) {
		double distance = 0;
		for (int i = 0; i < directionsRoute.legs.length; i++) {
			distance += directionsRoute.legs[i].distance.inMeters;
		}
		return distance;
	}

	private void setInitialContext() {
		context.setApiKey(apiKey);
	}

}
