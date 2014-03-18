package com.kruton.geocoder.helpers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kruton.geocoder.beans.LocationBean;
import com.kruton.geocoder.utils.Debug;
import com.kruton.geocoder.utils.Debug.LEVEL;


/*
 * The CountourHelper class contains various methods for modifying a weighted map.  
 */
public class ContourHelper {
	List<LocationBean> locations = new ArrayList<LocationBean>();
	
	public ContourHelper() {
		
	}
	
	public ContourHelper(ArrayList<LocationBean> locations) {
		this.locations = locations;
		
	}

	
	
	/* ***************************************************************************************
	 ****************************************************************************************
	 * Convenience methods
	 */
	
	/**
	 * This method uses the local variable locations to group the locations such that each point in a group is at most <b>distance</b> away from at least one other point in the group.<br><br>
	 * The points are grouped by creating an array of distances between each two points, and then working through that array to group the points.  Creating the array takes O(n^2) time, and the 
	 * grouping should take approximately O(n^2) time as well, although I haven't done an accurate analysis.  I believe best case scenario is O(n^2) when all points are close to each other 
	 * and worst case is something slower than that if there are lots of groups with lots of points.
	 * @param distance the maximum distance between two points for the points to be considered as in the same group
	 * @return hashmap containing the groups of points as Lists
	 */
	public Map<Integer, List<LocationBean>> clusterGroups(double distance) {
		double[][] distanceArray = createDistanceArray();
		
		return clusterFromArray(distanceArray, distance);
	}
	
	public Map<Integer, List<LocationBean>> clusterGroups(double distance, List<LocationBean> locations) {
		double[][] distanceArray = createDistanceArray();
		
		return clusterFromArray(distanceArray, locations, distance);
	}
	
	/**
	 * Creates a 2D array where each cell represents the distance between the locations with indexes x and y from the local variable locations.<br><br>
	 * In practice, this is half of a 2D array - values are not duplicated, so either [x][y] or [y][x] is the distance while the other is left at zero.  This is in part to save time, but also
	 * because the clusterFromArray method expects those values to be zero so it can group the points together.
	 * @return 2D array of distances
	 */
	public double[][] createDistanceArray() {
		return createDistanceArray(this.locations);
	}
	
	/**
	 * Prints out an map of the weights in the locationArray, prints "x" for null values<br><br>
	 * Convenience print method that defaults decimals to 2 places.  For other settings, use printLocationWeightsAsMap(LocationBean[][] locationArray, DecimalFormat df)
	 * @param locationArray the array to be printed
	 */
	public void printLocationWeightsAsMap(LocationBean[][] locationArray) {
		DecimalFormat df = new DecimalFormat("#.00");
		
		printLocationWeightsAsMap(locationArray, df);
	}
	
	/**
	 * Convenience print method that defaults decimals to 2 places.  For other settings, use printLatLonsAsMap(LocationBean[][] locationArray, DecimalFormat df)
	 * @param locationArray the array to be printed
	 */
	public void printLatLonsAsMap(LocationBean[][] locationArray) {
		DecimalFormat df = new DecimalFormat("#.00");
		printLatLonsAsMap(locationArray, df);
	}
	
	/**
	 * R
	 * Prints out an map of the lat, lon, weight triplets in the locationArray, prints "x" for null values<br><br>
	 * Convenience print method that defaults decimals to 2 places.  For other settings, use printLatsLonsWeightsAsMap(LocationBean[][] locationArray, DecimalFormat df)
	 * @param locationArray the array to be printed
	 */
	public void printLatLonsWeightsAsMap(LocationBean[][] locationArray) {
		DecimalFormat df = new DecimalFormat("#.00");
		printLatsLonsWeightsAsMap(locationArray, df);
	}
	
	/**
	 * Prints location data in a format that can easily be copied to CSV, i.e. "41.87124, -87.12497, 1748"
	 * @param locationArray the array to be printed
	 */
	public void printLocationsWithWeights(LocationBean[][] locationArray) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;

		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				System.out.println(locationArray[x][y].getLatitude() + ", " + locationArray[x][y].getLongitude() + ", " + locationArray[x][y].getWeight());
			}
		}
	}
	
	
	/* ***************************************************************************************
	 ****************************************************************************************
	 * Action Methods
	 */
	
	public void sortLocations() {
		Collections.sort(locations);
	}
	
	
	
	public Map<Integer, Map<Integer, List<LocationBean>>> createContourPointGroups(double distance) {
		Map<Integer, Map<Integer, List<LocationBean>>> groupings = new HashMap<Integer, Map<Integer, List<LocationBean>>>();
		
		Map<Integer, List<LocationBean>> weightedGroups = groupPointsByWeightForContours();
		
		System.out.println("Weights and Sizes");
		for (int key : weightedGroups.keySet()) {
			System.out.println(key + " " + weightedGroups.get(key).size());
		}
		
		for (int key : weightedGroups.keySet()) {
			List<LocationBean> currentGroup = weightedGroups.get(key);
			System.out.println("Current group has weight " + key + " and size " + currentGroup.size());
			
			Map<Integer, List<LocationBean>> clusters = new HashMap<Integer, List<LocationBean>>(clusterGroups(distance, currentGroup));
			
			groupings.put(key, clusters);
			
			System.out.println("Added weight " + key + ", with " + clusters.keySet().size() + " groups from group of size " + currentGroup.size());
		}
		
		return groupings;
	}
	
	
	public Map<Integer, List<LocationBean>> clusterFromArray(double[][] distanceArray, double distance) {
		return clusterFromArray(distanceArray, this.locations, distance);
	}
	
	public Map<Integer, List<LocationBean>> clusterFromArray(double[][] distanceArray, List<LocationBean> locations, double distance) {
		Map<Integer, List<LocationBean>> groupings = new HashMap<Integer, List<LocationBean>>();
		int grouping = 1;
		for(LocationBean location : locations) {
			location.setGroup(0);
		}
		
		for(int x = 0; x < locations.size(); x++) {
			if (locations.get(x).getGroup() == 0) {
				while (groupings.containsKey(grouping)) {
					grouping++;
				}
				//System.out.println("Creating new group: " + grouping + " for point at " + x + " with name " + locations.get(x).getName());
				locations.get(x).setGroup(grouping);
				groupings.put(grouping, new ArrayList<LocationBean>());
				groupings.get(grouping).add(locations.get(x));
			} else {
				grouping = locations.get(x).getGroup();
			}
			for (int y = 0; y < locations.size(); y++) {
				if (y > x) {  //// avoid processing the duplicate half of the array
					if (distanceArray[x][y] <= distance) {
						if (locations.get(y).getGroup() == 0) {
							locations.get(y).setGroup(grouping);
							groupings.get(grouping).add(locations.get(y));
							//System.out.print(" +" + locations.get(y).getName() + " ");
						} else if (locations.get(y).getGroup() == grouping) {
							////do nothing?
						} else { //combine groups
							int otherGroup = locations.get(y).getGroup();
							//System.out.println("Combining groups " + grouping + " (" + groupings.get(grouping).size() + ") and " + otherGroup + "(" + groupings.get(otherGroup).size() + ")");
							int originalSize = groupings.get(grouping).size();
							for (int i = 0; i < originalSize; i++) {
								//System.out.print(i + " ");
								groupings.get(otherGroup).add(groupings.get(grouping).remove(0));
							}
							locations.get(x).setGroup(otherGroup);
						}
						
					}
				}
			}
			//System.out.println("Group " + grouping + " has size " + groupings.get(grouping).size());
		}
		
		////Prune empty groupings - not a necessary step, but helps clean up the result a bit.
		Set<Integer> keys = groupings.keySet();
		int maxKey = 0;
		for (Integer key : keys) { //since there is no gaurantee about consecutive group numbers, we have to go through all possible keys
			if (key > maxKey)
				maxKey = key;
		}
		for (int i = 1; i <= maxKey; i++) {
			if (groupings.containsKey(i) && groupings.get(i).size() == 0) {
				//System.out.println("removing group " + i);
				groupings.remove(i);
			} 
		}
		
		return groupings;
	}

	public double[][] createDistanceArray(List<LocationBean> locations) {
		double[][] distanceArray = new double[locations.size()][locations.size()];
		
		for(int x = 0; x < locations.size(); x++) {

			LocationBean startLocation = locations.get(x);
			for(int y = 0; y < locations.size(); y++) {
				LocationBean endLocation = locations.get(y);
				
				if (y <= x) {
					distanceArray[x][y] = 0.0;
				} else {
					distanceArray[x][y] = startLocation.getDistanceFrom(endLocation);
				}
			}
		}
		
		return distanceArray;
	}
	
	/*
	 * Groups points together such that each group contains all points which have weights less than or equal to the key
	 */
	public Map<Integer, List<LocationBean>> groupPointsByWeightForContours() {
		return groupPointsByWeightForContours(3);
		
	}
	
	public Map<Integer, List<LocationBean>> groupPointsByWeightForContours(int interval) {
		Map<Integer, List<LocationBean>> groupings = new HashMap<Integer, List<LocationBean>>();
		boolean flag = false;
		int minutes = interval;
		
		while (flag == false) {
			List<LocationBean> currentGroup = new ArrayList<LocationBean>();
			for (LocationBean location : locations) {
				if(location.getGoogleContourWeightFloor() >= minutes) {
					LocationBean newLocation = new LocationBean(location);
					double newWeight = (double) minutes;
					newLocation.setWeight(newWeight);
					currentGroup.add(newLocation);
				}
			}
			
			if (currentGroup.size() == 0) {
				flag = true;
			} else {
				groupings.put(minutes, currentGroup);
			}
			
			minutes += interval;
		}
		
		return groupings;
	}
	
	
	
	
	
	
	public Map<Integer, List<LocationBean>> groupLocationsByWeight() {
		Map<Integer, List<LocationBean>> groupings = new HashMap<Integer, List<LocationBean>>();
		
		for(LocationBean location : this.locations) {
			int weight = location.getGoogleContourWeightFloor();
			
			if (!groupings.containsKey(weight)) {
				System.out.println("Creating new grouping for weight: " + weight + " from " + location.getWeight());
				groupings.put(weight, new ArrayList<LocationBean>());
			}
			groupings.get(weight).add(location);
		}
		
		return groupings;
	}
	
	
	
	/* ***************************************************************************************
	 ****************************************************************************************
	 * Getters, Setters, and Printers
	 * 
	 */

	public List<LocationBean> getLocations() {
		return locations;
	}

	public void setLocations(List<LocationBean> locations) {
		this.locations = locations;
	}
	

	/**
	 * Prints a 2d map of a double[][].  This works best for values that range from 0-999.  2 decimal Precision
	 * @param doubleArray - array / 2d map to be printed
	 */
	public void printDoubleArray(double[][] doubleArray) {
		DecimalFormat df = new DecimalFormat("#0.00");
		
		int length = doubleArray.length;
		for(int x = 0; x < length; x++) {
			for(int y = 0; y < length; y++) {
				if(doubleArray[x][y] < 1000)
					System.out.print(" ");
				if(doubleArray[x][y] < 100)
					System.out.print(" ");
				if(doubleArray[x][y] < 10)
					System.out.print(" ");
				System.out.print(df.format(doubleArray[x][y]));
			}
			System.out.println("");
		}
	}
	

	public void printLatLonsAsMap(LocationBean[][] locationArray, DecimalFormat df) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;
		
		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				System.out.print("(" + locationArray[x][y].getLatitude() + ", " + locationArray[x][y].getLongitude() + ") ");

			}
			System.out.println();
		}
	}
	

	public void printLocationWeightsAsMap(LocationBean[][] locationArray, DecimalFormat df) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;
		
		
		System.out.println("-------- Map of size [" + rows + "," + cols + "] ----------");
		
		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				
				if (locationArray[x][y] != null)
					System.out.print(df.format(locationArray[x][y].getWeight()) + " ");
				else 
					System.out.print("  x   ");
			}
			System.out.println();
		}
	}
	
	
	public void printLatsLonsWeightsAsMap(LocationBean[][] locationArray, DecimalFormat df) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;
		
		
		System.out.println("-------- Map of size [" + rows + "," + cols + "] ----------");
		
		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				
				if (locationArray[x][y] != null)
					System.out.print("(" + locationArray[x][y].getLatitude() + ", " + locationArray[x][y].getLongitude() + ", " + df.format(locationArray[x][y].getWeight()) + ") ");
				else 
					System.out.print("  x   ");
			}
			System.out.println();
		}
	}

	
	// very innefficient, but works well enough for simple testing
	public void printGrouping(List<LocationBean> grouping, LocationBean[][] locationArray) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;
		LocationBean[][] groupedLocationArray = new LocationBean[cols][rows];
		
		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				for (LocationBean location : grouping) {
					if (location == locationArray[x][y]) {
						groupedLocationArray[x][y] = new LocationBean(1.0,1.0, location.getWeight());
					}
				}
			}
		}
		
		printLocationWeightsAsMap(groupedLocationArray);
	}
	
	

	
	
/* ***************************************************************************************************************************************************************************************
 *****************************************************************************************************************************************************************************************
 *****************************************************************************************************************************************************************************************
 *****************************************************************************************************************************************************************************************
 *****************************************************************************************************************************************************************************************
 **
 ** Old methods that were not used in the final product or were scrapped (below, commented out) but that I like to keep around just because.
 ** 
 *************************/
	
	
	
	public LocationBean[][] fillInArray(LocationBean[][] locationArray) {
		return fillInArray(locationArray, Debug.LEVEL.NONE);
	}
	
	
	/*
	 * Returns a hashmap containing a list of location points that all have the same weight
	 */
	@Deprecated
	public Map<Integer, List<LocationBean>> groupLocationsByWeight(LocationBean[][] locationArray) {
		Map<Integer, List<LocationBean>> groupings = new HashMap<Integer, List<LocationBean>>();
		int rows = locationArray[0].length;
		int cols = locationArray.length;

		
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				int weight = locationArray[x][y].getTestRoundedWeight();
				
				/*
				List<LocationBean> weightGroup = groupings.get(weight);
				
				if (weightGroup == null) {
					weightGroup = new ArrayList<LocationBean>();
					System.out.println("Creating new grouping for weight: " + weight);
				}

				weightGroup.add(locationArray[x][y]);
				*/
				
				if (!groupings.containsKey(weight)) {
					System.out.println("Creating new grouping for weight: " + weight);
					groupings.put(weight, new ArrayList<LocationBean>());
					
				}
				groupings.get(weight).add(locationArray[x][y]);
			}
		}
		
		return groupings;
	}
	
	/*
	 * Create a LocationBean 2D array from the locations private variable
	 */
	@Deprecated
	public LocationBean[][] createLocationArrayFromList(int rows, int cols) {
		LocationBean[][] locationArray = new LocationBean[cols][rows];
		
		int count = 0;
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				System.out.println(y + " " + x);
				locationArray[x][y] = locations.get(count);
				count++;
			}
		}
		return locationArray;
	}
	
	/*
	 * Expands the location array by adding an extra locationBean in between every other 2 locationBeans
	 * 
	 * Given a 2x2 array, this function will return an array of size 3x3.
	 * 
	 * Ex (x indicates a null value):
	 * initial array:         New array: 
	 *        1 2				1 x 2
	 *        3 4				x x x
	 *    						3 x 4
	 */
	@Deprecated
	public LocationBean[][] expandArray(LocationBean[][] initialLocationArray) {
		int rows = initialLocationArray[0].length;
		int cols = initialLocationArray.length;
		
		LocationBean[][] newLocationArray = new LocationBean[cols * 2 - 1][rows * 2 - 1];
		
		int newRowLoc = 0;
		int newColLoc = 0;
		newLocationArray[newColLoc][newRowLoc] = initialLocationArray[0][0];
		
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				if (y == 0)
					newRowLoc = 0;
				else
					newRowLoc = y * 2;
				if (x == 0)
					newColLoc = 0;
				else
					newColLoc = x * 2;
				
				newLocationArray[newColLoc][newRowLoc] = initialLocationArray[x][y];

			}
		}
		
		return newLocationArray;
	}
	
	/**
	 * Fills in the null values of a locationArray by taking the average value of the null value's neighbors.
	 *   This method should be used after an array is expanded, and calls the methods firstPass and secondPass to fill in the data
	 *   
	 * @param locationArray - The LocationBean[][] to be filled in
	 * @param debug - the LEVEL of debugging
	 * @return the locationArray after completely filling in all null values
	 */
	@Deprecated
	public LocationBean[][] fillInArray(LocationBean[][] locationArray, LEVEL debug) {
		double xStep = 0.0;
		double yStep = 0.0;
		
		double lat1 = locationArray[0][0].getLatitude();
		double lat2 = locationArray[0][2].getLatitude();
		double lon1 = locationArray[0][0].getLongitude();
		double lon2 = locationArray[2][0].getLongitude();
		xStep = (lat2 - lat1) / 2;
		yStep = (lon2 - lon1) / 2;
		
		if (debug.getLevel() >= 3) System.out.println("Initial lats: " + lat1 + " " + lat2 + " | step = " + xStep);
		if (debug.getLevel() >= 3) System.out.println("Initial lons: " + lon1 + " " + lon2 + " | step = " + yStep);

		locationArray = firstPass(locationArray, debug);
		
		if (debug.getLevel() >= 2) System.out.println("After first pass");
		if (debug.getLevel() >= 2) printLocationWeightsAsMap(locationArray);
		
		locationArray = secondPass(locationArray, debug, xStep, yStep);
		
		return locationArray;
	}
	
	/**
	 * This method fills in all null values in the 2D array that are surrounded by other null values
	 * 
	 * Ex (x indicates a null value):
	 * initial array:         New array: 
	 *        1 x 2				1  x  2
	 *        x x x				x 2.5 x
	 *    	  3 x 4				3  x  4
	 *    
	 * @param locationArray - The LocationBean[][] to be filled in
	 * @param debug - the LEVEL of debugging
	 * @return the partially filled in locationArray
	 */
	@Deprecated
	public LocationBean[][] firstPass(LocationBean[][] locationArray, LEVEL debug) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;

		
		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				double sum = 0.0;
				if (debug.getLevel() >= 3) System.out.print(x + " " + y);
				if (locationArray[y][x] == null 
						&& 0 < y && y < cols - 1				//make sure it's not at an edge
						&& 0 < x && x < rows - 1
						&& locationArray[y-1][x] == null  		//make sure it's not "in between" two other items but is instead the center of an X
						&& locationArray[y-1][x-1] != null  	//make sure that all four points have values
						&& locationArray[y-1][x-1] != null
						&& locationArray[y-1][x-1] != null
						&& locationArray[y-1][x-1] != null) {
					sum += locationArray[y-1][x-1].getWeight()
						+ locationArray[y+1][x-1].getWeight()
						+ locationArray[y-1][x+1].getWeight()
						+ locationArray[y+1][x+1].getWeight();
					
					if (debug.getLevel() >= 3) System.out.print(sum);
					double average = sum / 4;
					double lat = (locationArray[y+1][x+1].getLatitude() + locationArray[y-1][x-1].getLatitude()) / 2;
					double lon = (locationArray[y+1][x+1].getLongitude() + locationArray[y-1][x-1].getLongitude()) / 2;
					locationArray[y][x] = new LocationBean(lat,lon,average);
				}
				if (debug.getLevel() >= 3) System.out.println();

			}
		}
		
		return locationArray;
	}

	/**
	 * This method fills in all null values in the 2D array that remain after the first pass
	 * 
	 * Ex (x indicates a null value):
	 * initial array:         New array: 
	 *        1  x  2         1 1.5 2
	 *        x 2.5 x         2 2.5 3
	 *    	  3  x  4   	  3 3.5 4
	 *    
	 * @param locationArray - The LocationBean[][] to be filled in
	 * @param debug - the LEVEL of debugging
	 * @param lonStep - double distance between longitudes
	 * @param latStep - double distance between latitudes
	 * @return the filled in locationArray
	 */
	@Deprecated
	public LocationBean[][] secondPass(LocationBean[][] locationArray, LEVEL debug, double lonStep, double latStep) {
		int rows = locationArray[0].length;
		int cols = locationArray.length;

		for(int x = 0; x < rows; x++) {
			for(int y = 0; y < cols; y++) {
				double sum = 0.0;
				int count = 0;
				if (debug.getLevel() >= 3) System.out.print(x + " " + y);
				if (locationArray[y][x] == null) {
					if (y > 0) {
						if (debug.getLevel() >= 4) System.out.print("a");
						sum += locationArray[y-1][x].getWeight();
						count++;
					}
					if (x > 0) {
						if (debug.getLevel() >= 4) System.out.print("b");
						sum += locationArray[y][x-1].getWeight();
						count++;
					}
					if (y < cols - 1) {
						if (debug.getLevel() >= 4) System.out.print("c");
						sum += locationArray[y+1][x].getWeight();
						count++;
					}
					if (x < rows - 1) {
						if (debug.getLevel() >= 4) System.out.print("d");
						sum += locationArray[y][x+1].getWeight();
						count++;
					}
					
					double average = sum / count;
					
					double lat = 0.0;
					double lon = 0.0;
					
					if (y == 0 || y == cols - 1) {
						lon = locationArray[y][x-1].getLongitude();
					} else {
						lon = (locationArray[y-1][x].getLongitude() + locationArray[y+1][x].getLongitude()) / 2;
					}
					if (x == 0 || x == rows - 1) {
						lat = locationArray[y-1][x].getLatitude();
					} else {
						lat = (locationArray[y][x-1].getLatitude() + locationArray[y][x+1].getLatitude()) / 2;
					}

					locationArray[y][x] = new LocationBean(lat,lon,average);
				}
				if (debug.getLevel() >= 3) System.out.println();

			}
		}
		
		return locationArray;
	}
	
	/*
	public Map<Integer, List<LocationBean>> separateGroupingsByDistance(List<LocationBean> locations, double distance) {
		Map<Integer, List<LocationBean>> groupings = new HashMap<Integer, List<LocationBean>>();
	
		for (int i = 0; i < locations.size(); i++) {
			for(LocationBean location : locations) {
				if (!location.isAdded()) {
					if (groupings.containsKey(i)) {
						
					} else {
						location.setAdded(true);
						groupings.put(i, new ArrayList<LocationBean>());
						groupings.get(i).add(location);
					}
				}
			}
		}
		
		
		return groupings;
	}
	
	public boolean isLocationNearGroup(List<LocationBean> locations, LocationBean origin, double distance) {
		boolean flag = false;
		
		for (LocationBean location : locations) {
			if (origin.isClose(location, distance))
				flag = true;
		}
		
		return flag;
	}
	*/
	
	
	/*
	public Map<Integer, List<LocationBean>> clusterFromArray(double[][] distanceArray, List<LocationBean> locations, double distance) {
		Map<Integer, List<LocationBean>> groupings = new HashMap<Integer, List<LocationBean>>();
		int grouping = 0;
		
		for(int x = 0; x < locations.size(); x++) {
			LocationBean startLocation = locations.get(x);
			if (startLocation.getGroup() == 0) {
				while (groupings.containsKey(grouping)) {
					grouping++;
				}
				System.out.print("Creating new group: " + grouping + " for point at " + x);
				startLocation.setGroup(grouping);
				groupings.put(grouping, new ArrayList<LocationBean>());
				groupings.get(grouping).add(startLocation);
			} else {
				grouping = startLocation.getGroup();
			}
			for (int y = 0; y < locations.size(); y++) {
				if (y > x) {  //avoid processing the duplicate half of the array
					if (distanceArray[x][y] <= distance && locations.get(y).getGroup() == 0) {
						locations.get(y).setGroup(grouping);
						groupings.get(grouping).add(locations.get(y));
					}
				}
			}
			System.out.println(" - " + groupings.get(grouping).size());
		}
		
		return groupings;
	}
	*/
}
