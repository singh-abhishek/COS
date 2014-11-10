package com.ideas.routeOptimization;

import java.util.ArrayList;

public class JCA {
	private Cluster[] clusters;
	private int miter;
	private ArrayList<DataPoint> mDataPoints = new ArrayList<DataPoint>();
	private double mSWCSS;

	public JCA(int k, int iter, ArrayList<DataPoint> arrayList) {
		clusters = new Cluster[k];
		for (int i = 0; i < k; i++)
			clusters[i] = new Cluster("Cluster" + i);
		this.miter = iter;
		this.mDataPoints = arrayList;
	}

	private void calcSWCSS() {
		double temp = 0;
		for (Cluster cluster : clusters)
			temp = temp + cluster.getSumSqr();
		mSWCSS = temp;
	}

	public void startAnalysis() {
		// set Starting centroid positions - Start of Step 1
		setInitialCentroids();
		int n = 0;
		// assign DataPoint to clusters
		loop1: while (true)
			for (int l = 0; l < clusters.length; l++) {
				clusters[l].addDataPoint(mDataPoints.get(n));
				n++;
				if (n >= mDataPoints.size())
					break loop1;
			}

		// calculate E for all the clusters
		calcSWCSS();

		// recalculate Cluster centroids - Start of Step 2
		for (Cluster cluster : clusters)
			cluster.getCentroid().calcCentroid();

		// recalculate E for all the clusters
		calcSWCSS();

		for (int i = 0; i < miter; i++)
			for (Cluster cluster : clusters)
				for (int k = 0; k < cluster.getNumDataPoints(); k++) {

					// pick the first element of the first cluster
					// get the current Euclidean distance
					double tempEuDt = cluster.getDataPoint(k).getCurrentEuDt();
					Cluster tempCluster = null;
					boolean matchFoundFlag = false;

					// call testEuclidean distance for all clusters
					for (Cluster cluster2 : clusters)
						if (tempEuDt > cluster.getDataPoint(k).testEuclideanDistance(cluster2.getCentroid())) {
							tempEuDt = cluster.getDataPoint(k).testEuclideanDistance(cluster2.getCentroid());
							tempCluster = cluster2;
							matchFoundFlag = true;
						}
					// if statement - Check whether the Last EuDt is > Present
					// EuDt

					if (matchFoundFlag) {
						tempCluster.addDataPoint(cluster.getDataPoint(k));
						cluster.removeDataPoint(cluster.getDataPoint(k));
						for (Cluster cluster2 : clusters)
							cluster2.getCentroid().calcCentroid();

						// for variable 'm' - Recalculating centroids for all
						// Clusters

						calcSWCSS();
					}

					// if statement - A Data Point is eligible for transfer
					// between Clusters.
				}
		// for variable 'k' - Looping through all Data Points of the current
		// Cluster.
	}

	public ArrayList<ArrayList<DataPoint>> getClusterOutput() {
		ArrayList<ArrayList<DataPoint>> v = new ArrayList<ArrayList<DataPoint>>();
		for (int i = 0; i < clusters.length; i++)
			v.add(clusters[i].getDataPoints());
		return v;
	}

	private void setInitialCentroids() {
		// kn = (round((max-min)/k)*n)+min where n is from 0 to (k-1).
		double cx = 0, cy = 0;
		for (int n = 1; n <= clusters.length; n++) {
			cx = (((getMaxXValue() - getMinXValue()) / (clusters.length + 1)) * n) + getMinXValue();
			cy = (((getMaxYValue() - getMinYValue()) / (clusters.length + 1)) * n) + getMinYValue();
			Centroid c1 = new Centroid(cx, cy);
			clusters[n - 1].setCentroid(c1);
			c1.setCluster(clusters[n - 1]);
		}
	}

	private double getMaxXValue() {
		double temp;
		temp = mDataPoints.get(0).getX();
		for (int i = 0; i < mDataPoints.size(); i++) {
			DataPoint dp = mDataPoints.get(i);
			temp = (dp.getX() > temp) ? dp.getX() : temp;
		}
		return temp;
	}

	private double getMinXValue() {
		double temp = 0;
		temp = mDataPoints.get(0).getX();
		for (int i = 0; i < mDataPoints.size(); i++) {
			DataPoint dp = mDataPoints.get(i);
			temp = (dp.getX() < temp) ? dp.getX() : temp;
		}
		return temp;
	}

	private double getMaxYValue() {
		double temp = 0;
		temp = mDataPoints.get(0).getY();
		for (int i = 0; i < mDataPoints.size(); i++) {
			DataPoint dp = mDataPoints.get(i);
			temp = (dp.getY() > temp) ? dp.getY() : temp;
		}
		return temp;
	}

	private double getMinYValue() {
		double temp = 0;
		temp = mDataPoints.get(0).getY();
		for (int i = 0; i < mDataPoints.size(); i++) {
			DataPoint dp = mDataPoints.get(i);
			temp = (dp.getY() < temp) ? dp.getY() : temp;
		}
		return temp;
	}

	public int getKValue() {
		return clusters.length;
	}

	public int getIterations() {
		return miter;
	}

	public int getTotalDataPoints() {
		return mDataPoints.size();
	}

	public double getSWCSS() {
		return mSWCSS;
	}

	public Cluster getCluster(int pos) {
		return clusters[pos];
	}
}
