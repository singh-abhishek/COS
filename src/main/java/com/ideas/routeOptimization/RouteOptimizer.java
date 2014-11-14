package com.ideas.routeOptimization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class RouteOptimizer {
	private static DataPoint ideasLoc = new DataPoint(18.56, 73.80, "IDeaS");
	private static Connection connection;

	public RouteOptimizer(Connection connection) {
		this.connection = connection;
	}

	public ArrayList<TreeMap<Time, ArrayList<ArrayList<DataPoint>>>> optimizeRoute()
			throws ClassNotFoundException, SQLException {
		RouteOptimizerHandler routeOptimizerHandler = new RouteOptimizerHandler(
				connection);
		//ArrayList<TreeMap<Time, ArrayList<DataPoint>>> timesMap = routeOptimizerHandler
		//		.getData(new Time(new Date().getTime()));
		//TreeMap<Time, ArrayList<DataPoint>> inTimesMap = timesMap.get(0);
		//TreeMap<Time, ArrayList<DataPoint>> outTimesMap = timesMap.get(1);
		//-------random data add
		TreeMap<Time, ArrayList<DataPoint>> inTimesMap = new TreeMap<Time, ArrayList<DataPoint>>();
		TreeMap<Time, ArrayList<DataPoint>> outTimesMap = new TreeMap<Time, ArrayList<DataPoint>>();
		ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
		datapoints.add(new DataPoint	(	18.5362084	,	73.8939748	,"	4	"));
		datapoints.add(new DataPoint	(	18.5332333	,	73.8499376	,"	5	"));
		datapoints.add(new DataPoint	(	18.4756393	,	73.8544034	,"	8	"));
		datapoints.add(new DataPoint	(	18.567929	,	73.9143179	,"	9	"));
		datapoints.add(new DataPoint	(	18.455773	,	73.869363	,"	10	"));
		datapoints.add(new DataPoint	(	18.4908277	,	73.8202559	,"	11	"));
		datapoints.add(new DataPoint	(	18.474476	,	73.8918635	,"	12	"));
		datapoints.add(new DataPoint	(	18.501373	,	73.832658	,"	13	"));
		datapoints.add(new DataPoint	(	18.4871029	,	73.9329838	,"	14	"));
		datapoints.add(new DataPoint	(	18.4542527	,	73.9315732	,"	15	"));
		datapoints.add(new DataPoint	(	18.4929052	,	73.8362379	,"	16	"));
		datapoints.add(new DataPoint	(	18.530556	,	73.913539	,"	17	"));
		datapoints.add(new DataPoint	(	18.5904905	,	73.8170213	,"	18	"));
		datapoints.add(new DataPoint	(	18.4980735	,	73.8043655	,"	19	"));


		inTimesMap.put(new Time(9,30,0),datapoints );
		TreeMap<Time, ArrayList<ArrayList<DataPoint>>> inTimesOutputMap = new TreeMap<Time, ArrayList<ArrayList<DataPoint>>>();
		TreeMap<Time, ArrayList<ArrayList<DataPoint>>> outTimesOutputMap = new TreeMap<Time, ArrayList<ArrayList<DataPoint>>>();

		for (Time time : inTimesMap.keySet()) {
			ArrayList<ArrayList<DataPoint>> clusters = basicClustering(inTimesMap
					.get(time));
			clusters = furtherCLusteringTillSizeReachesFour(clusters);
			clusters = clubbingClusterWIthSizeOneAndOtherClusters(clusters);
			clusters = clubbingClusterOfSizeTwoWIthOthers(clusters);
			inTimesOutputMap.put(time, clusters);
			printClusterInAFormat(clusters);

		}

		for (Time time : outTimesMap.keySet()) {
			ArrayList<ArrayList<DataPoint>> clusters = basicClustering(outTimesMap
					.get(time));
			clusters = furtherCLusteringTillSizeReachesFour(clusters);
			clusters = clubbingClusterWIthSizeOneAndOtherClusters(clusters);
			clusters = clubbingClusterOfSizeTwoWIthOthers(clusters);
			outTimesOutputMap.put(time, clusters);
			printClusterInAFormat(clusters);
		}
		ArrayList<TreeMap<Time, ArrayList<ArrayList<DataPoint>>>> timesMapOutput = new ArrayList<TreeMap<Time, ArrayList<ArrayList<DataPoint>>>>();
		timesMapOutput.add(inTimesOutputMap);
		timesMapOutput.add(outTimesOutputMap);
		return timesMapOutput;

	}

	private static void printClusterInAFormat(
			ArrayList<ArrayList<DataPoint>> vList) {

		for (int i = 0; i < vList.size(); i++) {
			System.out.println("-----------Cluster" + i + "---------");
			Iterator<DataPoint> iter = vList.get(i).iterator();
			while (iter.hasNext()) {
				DataPoint dpTemp = iter.next();
				System.out.println(dpTemp.getObjName() + "[" + dpTemp.getX()
						+ "," + dpTemp.getY() + "]");
			}
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		new RouteOptimizer(connection).optimizeRoute();
	}

	private static ArrayList<ArrayList<DataPoint>> clubbingClusterOfSizeTwoWIthOthers(
			ArrayList<ArrayList<DataPoint>> vList) {
		for (int i = 0; i < vList.size(); i++) {
			if (vList.get(i).size() == 2) {

				int actualCount = 0;
				double actualSumX = 0;
				double actualSumY = 0;
				for (int k = 0; k < vList.get(i).size(); k++) {
					DataPoint dp = vList.get(i).get(k);
					actualSumX += dp.getX();
					actualSumY += dp.getY();
					actualCount++;
				}
				Centroid actualCentroid = new Centroid(
						actualSumX / actualCount, actualSumY / actualCount);
				DataPoint actualDataPoint = new DataPoint(actualSumX
						/ actualCount, actualSumY / actualCount,
						"actualCentroid");
				double minDistance = Double.MAX_VALUE;
				int minDistanceIndex = 0;
				Centroid centroid = null;
				for (int j = 0; j < vList.size(); j++) {
					int count = 0;
					double sumX = 0;
					double sumY = 0;
					if (i != j && vList.get(j).size() == 2) {
						for (int k = 0; k < vList.get(j).size(); k++) {
							DataPoint dp = vList.get(j).get(k);
							sumX += dp.getX();
							sumY += dp.getY();
							count++;
						}
						centroid = new Centroid(sumX / count, sumY / count);
						if (actualDataPoint.testEuclideanDistance(centroid) < minDistance) {
							minDistance = actualDataPoint
									.testEuclideanDistance(centroid);
							minDistanceIndex = j;
						}
					}
				}

				try {
					double distanceCentroidIdeas = ideasLoc
							.testEuclideanDistance(centroid);
					double distanceActualCentroidIdeas = ideasLoc
							.testEuclideanDistance(actualCentroid);
					if ((minDistance + distanceCentroidIdeas < 1.4 * distanceActualCentroidIdeas)
							|| (minDistance + distanceActualCentroidIdeas < 1.4 * distanceCentroidIdeas)) {
						vList.get(minDistanceIndex).add(vList.get(i).get(0));
						vList.get(minDistanceIndex).add(vList.get(i).get(1));
						vList.remove(vList.get(i));
					}
				} catch (NullPointerException ex) {
					// do nothing
				}
			}
		}
		return vList;
	}

	private static ArrayList<ArrayList<DataPoint>> clubbingClusterWIthSizeOneAndOtherClusters(
			ArrayList<ArrayList<DataPoint>> vList) {
		for (int i = 0; i < vList.size(); i++) {
			if (vList.get(i).size() == 1) {
				DataPoint actualDataPoint = vList.get(i).get(0);
				double minDistance = Double.MAX_VALUE;
				int minDistanceIndex = 0;
				Centroid centroid = null;
				for (int j = 0; j < vList.size(); j++) {
					int count = 0;
					double sumX = 0;
					double sumY = 0;
					if (i != j && vList.get(j).size() < 4) {
						for (int k = 0; k < vList.get(j).size(); k++) {
							DataPoint dp = vList.get(j).get(k);
							sumX += dp.getX();
							sumY += dp.getY();
							count++;
						}
						centroid = new Centroid(sumX / count, sumY / count);
						if (actualDataPoint.testEuclideanDistance(centroid) < minDistance) {
							minDistance = actualDataPoint
									.testEuclideanDistance(centroid);
							minDistanceIndex = j;
						}
					}
				}
				try {
					double distanceCentroidIdeas = ideasLoc
							.testEuclideanDistance(centroid);
					double distanceActualLocIdeas = ideasLoc
							.testEuclideanDistance(new Centroid(actualDataPoint
									.getX(), actualDataPoint.getY()));
					if ((minDistance + distanceCentroidIdeas < 1.4 * distanceActualLocIdeas)
							|| (minDistance + distanceActualLocIdeas < 1.4 * distanceCentroidIdeas)) {
						vList.get(minDistanceIndex).add(actualDataPoint);
						vList.remove(vList.get(i));
					}
				} catch (NullPointerException ex) {
					// do nothing
				}
			}
		}
		return vList;
	}

	private static ArrayList<ArrayList<DataPoint>> furtherCLusteringTillSizeReachesFour(
			ArrayList<ArrayList<DataPoint>> v) {
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).size() > 4) {
				JCA jc = new JCA(2, 1000, v.get(i));
				jc.startAnalysis();
				ArrayList<ArrayList<DataPoint>> temp = jc.getClusterOutput();
				v.add(temp.get(0));
				v.add(temp.get(1));
				v.remove(v.get(i));
				i = i - 1;
			}
		}
		return v;
	}

	private static ArrayList<ArrayList<DataPoint>> basicClustering(
			ArrayList<DataPoint> arrayList) {
		int numberOfClusters = 1;
		if (arrayList.size() == 2) {
			numberOfClusters = 2;
		}
		if (arrayList.size() > 2) {
			numberOfClusters = Math.max(3, arrayList.size() / 2);
		}
		JCA jca = new JCA(numberOfClusters, 10000, arrayList);
		jca.startAnalysis();

		ArrayList<ArrayList<DataPoint>> v = jca.getClusterOutput();
		return v;
	}
}
