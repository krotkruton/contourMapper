package test.kruton.geocoder.helpers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.kruton.geocoder.beans.LocationBean;
import com.kruton.geocoder.helpers.CSVParser;
import com.kruton.geocoder.helpers.ContourHelper;
import com.kruton.geocoder.utils.Debug;
import com.kruton.geocoder.utils.Debug.LEVEL;

public class ContourMapperTest {
	LEVEL debug_level = Debug.LEVEL.NONE;
	
	private double maxDistance = 0.0071; //From LocationBeanTest on actual data, all points should be within .0070711 of each other, which will include diagonal points.  For only NSEW points, use .005.
	private String hospitalsFile = "C:\\src\\Geocoder\\resources\\hospitals.csv";
	private String traumaWeightsFile = "I:\\src\\Geocoder\\resources\\chicago_trauma_for_import.csv";  //file with drivetimes to current trauma 1 hospitals
	private String traumaPotentialWeightsFile = "I:\\src\\Geocoder\\resources\\chicago_trauma_potential_for_import.csv"; //file with drivetimes to current and potential trauma 1 hospitals
	private String traumaOutputFile = "I:\\src\\Geocoder\\resources\\chicago_trauma_output.csv";
	private String traumaPotentialOutputFile = "I:\\src\\Geocoder\\resources\\chicago_trauma_potential_output.csv";
	ArrayList<LocationBean> testLocations1 = new ArrayList<LocationBean>();
	ArrayList<LocationBean> testLocations2 = new ArrayList<LocationBean>();
	ArrayList<LocationBean> testLocations3 = new ArrayList<LocationBean>();
	ArrayList<LocationBean> testLocations4 = new ArrayList<LocationBean>();
	
	/*
	 * ***********************************************
	 * Tests used for creating files (aka cheating)
	 */

	@Test
	public void TraumaOutputTest() {
		System.out.println("************ TraumaOutputTest");
		
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> locations = new ArrayList<LocationBean>();
		parser.parseNameLatLonWeightTypeCSV(locations, traumaWeightsFile);
		ContourHelper contourHelper = new ContourHelper(locations);
		
		Map<Integer, Map<Integer, List<LocationBean>>> clusteredGroups = contourHelper.createContourPointGroups(maxDistance);
		
		for (int weight : clusteredGroups.keySet()) {
			//System.out.println("Weight " + key + " has " + groupings.get(key).size());
			Map<Integer, List<LocationBean>> groups = clusteredGroups.get(weight);
			for (int group : groups.keySet()) {
				System.out.println(weight + ", " + group + ", " + groups.get(group).size());
			}
		}
		
		parser.writeFullCSVWithGrouping(clusteredGroups, traumaOutputFile);
	}

	//@Test
	public void ContourTraumaOutput() {
		System.out.println("************ ContourTraumaOutput ");
		
		//parse the CSV file and put it in the CountourHelper
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> locations = new ArrayList<LocationBean>();
		parser.parseNameAddressCSV(locations, traumaWeightsFile);
		ContourHelper contourHelper = new ContourHelper(locations);
		
		double[][] distanceArray = contourHelper.createDistanceArray();
		
		
		
		
		Map<Integer, List<LocationBean>> groupings = contourHelper.clusterFromArray(distanceArray, maxDistance);
		
		int count = 0;
		for(int i : groupings.keySet()) {
			count += groupings.get(i).size();
			//System.out.println("Group " + i + " has " + groupings.get(i).size());
		}
		//System.out.println("Total results = " + count);
		
		assertEquals(count, 32);
		assertEquals(groupings.entrySet().size(), 2);
	}
	
	
	/*
	 * ***********************************************
	 * Actual Automated Tests using Static Data
	 */
	
	//@Test
	public void LocationSortTest() {
		System.out.println("************ LocationSortTest");
		ContourHelper counterHelper = new ContourHelper(testLocations1);
		counterHelper.sortLocations();
		
		for(LocationBean location : counterHelper.getLocations())
			System.out.println(location.getLatitude() + ", " + location.getLongitude());
		
		System.out.println();
		
		assertEquals(testLocations1.get(0).getWeight(), (Double) 10.0);
		assertEquals(testLocations1.get(15).getWeight(), (Double) 13.3);
		
		
		System.out.println(testLocations1.get(5).getWeight() + " " + Math.round(testLocations1.get(5).getWeight()));
		System.out.println(testLocations1.get(15).getWeight() + " " + Math.round(testLocations1.get(15).getWeight()));
	}
	
	//@Test
	public void GoogleWeightedGroupingTest() {
		System.out.println("************ GoogleWeightedGroupingTest");
		ContourHelper contourHelper = new ContourHelper(testLocations4);

		Map<Integer, List<LocationBean>> groupings = contourHelper.groupLocationsByWeight();
		
		for (int key : groupings.keySet()) {
			System.out.println("Weight " + key + " has " + groupings.get(key).size());
		}
		
		//33 minutes ranges between 1980 to 2160, there are 5 in testLocations4
		assertEquals(groupings.get(33).size(), 5);
		
		
		System.out.println("************ GoogleWeightedGroupingTest2");
		groupings = contourHelper.groupPointsByWeightForContours();
		
		assertEquals(groupings.get(33).size(), 7);  
		assertEquals(groupings.get(3).size(), 32);  //smallest group should contain everything
		assertEquals(groupings.size(), 12);
		
		for (int i = 0; i < 39; i += 3) {
			if (groupings.containsKey(i))
				System.out.println("Weight " + i + " has " + groupings.get(i).size());
		}
	}
	

	//@Test
	public void GoogleClusteringTest() {
		System.out.println("************ GoogleClusteringTest");
		ContourHelper contourHelper = new ContourHelper(testLocations4);

		Map<Integer, Map<Integer, List<LocationBean>>> clusteredGroups = contourHelper.createContourPointGroups(1.5);
		
		for (int weight : clusteredGroups.keySet()) {
			//System.out.println("Weight " + key + " has " + groupings.get(key).size());
			Map<Integer, List<LocationBean>> groups = clusteredGroups.get(weight);
			for (int group : groups.keySet()) {
				System.out.println(weight + ", " + group + ", " + groups.get(group).size());
			}
		}
	}
	
	
	
	//@Test
	public void DistanceArrayTest() {
		System.out.println("************ DistanceArrayTest");
		ContourHelper contourHelper = new ContourHelper(testLocations4);
		
		double[][] distanceArray = contourHelper.createDistanceArray(testLocations4);
		
		//contourHelper.printDoubleArray(distanceArray);
		
		Map<Integer, List<LocationBean>> groupings = contourHelper.clusterFromArray(distanceArray, testLocations4, 1.5);
		
		int count = 0;
		for(int i : groupings.keySet()) {
			count += groupings.get(i).size();
			//System.out.println("Group " + i + " has " + groupings.get(i).size());
		}
		//System.out.println("Total results = " + count);
		
		assertEquals(count, 32);
		assertEquals(groupings.entrySet().size(), 2);
	}
	
	
	
	

	@Before
	public void setUp() throws Exception {
		testLocations1.add(new LocationBean(10.0, 10.0, 10.0));
		testLocations1.add(new LocationBean(10.0, 11.0, 11.1));
		testLocations1.add(new LocationBean(10.0, 12.0, 12.2));
		testLocations1.add(new LocationBean(13.0, 13.0, 13.3));
		testLocations1.add(new LocationBean(11.0, 13.0, 14.4));
		testLocations1.add(new LocationBean(10.0, 13.0, 15.5));
		testLocations1.add(new LocationBean(11.0, 10.0, 16.6));
		testLocations1.add(new LocationBean(11.0, 11.0, 17.7));
		testLocations1.add(new LocationBean(11.0, 12.0, 18.8));
		testLocations1.add(new LocationBean(12.0, 12.0, 19.9));
		testLocations1.add(new LocationBean(12.0, 13.0, 20.0));
		testLocations1.add(new LocationBean(13.0, 10.0, 21.1));
		testLocations1.add(new LocationBean(13.0, 11.0, 22.2));
		testLocations1.add(new LocationBean(13.0, 12.0, 23.3));
		testLocations1.add(new LocationBean(12.0, 10.0, 24.4));
		testLocations1.add(new LocationBean(12.0, 11.0, 25.5));
		
		testLocations2.add(new LocationBean(10.0, 10.0, 10.0));
		testLocations2.add(new LocationBean(10.0, 12.0, 11.1));
		testLocations2.add(new LocationBean(11.0, 10.0, 12.2));
		testLocations2.add(new LocationBean(11.0, 12.0, 13.3));
		
		
		testLocations3.add(new LocationBean(10.0, 10.0, 10.0));
		testLocations3.add(new LocationBean(10.0, 11.0, 18.1));
		testLocations3.add(new LocationBean(10.0, 12.0, 15.2));
		testLocations3.add(new LocationBean(13.0, 13.0, 13.3));
		testLocations3.add(new LocationBean(11.0, 13.0, 22.4));
		testLocations3.add(new LocationBean(10.0, 13.0, 13.5));
		testLocations3.add(new LocationBean(11.0, 10.0, 16.6));
		testLocations3.add(new LocationBean(11.0, 11.0, 17.7));
		testLocations3.add(new LocationBean(11.0, 12.0, 18.8));
		testLocations3.add(new LocationBean(12.0, 12.0, 19.9));
		testLocations3.add(new LocationBean(12.0, 13.0, 20.0));
		testLocations3.add(new LocationBean(13.0, 10.0, 21.1));
		testLocations3.add(new LocationBean(13.0, 11.0, 22.2));
		testLocations3.add(new LocationBean(13.0, 12.0, 18.3));
		testLocations3.add(new LocationBean(12.0, 10.0, 24.4));
		testLocations3.add(new LocationBean(12.0, 11.0, 18.5));
		
		testLocations4.add(new LocationBean("1", 10.0, 10.0, 2110.0));
		testLocations4.add(new LocationBean("2", 10.0, 11.0, 1218.1));
		testLocations4.add(new LocationBean("3", 10.0, 12.0, 1215.2));
		testLocations4.add(new LocationBean("4", 13.0, 13.0, 1313.3));
		testLocations4.add(new LocationBean("5", 11.0, 13.0, 1422.4));
		testLocations4.add(new LocationBean("6", 10.0, 13.0, 1513.5));
		testLocations4.add(new LocationBean("7", 11.0, 10.0, 1716.6));
		testLocations4.add(new LocationBean("8", 11.0, 11.0, 1217.7));
		testLocations4.add(new LocationBean("9", 11.0, 12.0, 1318.8));
		testLocations4.add(new LocationBean("10", 12.0, 12.0, 1419.9));
		testLocations4.add(new LocationBean("11", 12.0, 13.0, 1520.0));
		testLocations4.add(new LocationBean("12", 13.0, 10.0, 1321.1));
		testLocations4.add(new LocationBean("13", 13.0, 11.0, 1222.2));
		testLocations4.add(new LocationBean("14", 13.0, 12.0, 1318.3));
		testLocations4.add(new LocationBean("15", 12.0, 10.0, 1424.4));
		testLocations4.add(new LocationBean("16", 12.0, 11.0, 1518.5));
		testLocations4.add(new LocationBean("17", 110.0, 110.0, 1110.0));
		testLocations4.add(new LocationBean("18", 110.0, 111.0, 1718.1));
		testLocations4.add(new LocationBean("19", 110.0, 112.0, 1815.2));
		testLocations4.add(new LocationBean("20", 113.0, 113.0, 1913.3));
		testLocations4.add(new LocationBean("21", 111.0, 113.0, 2022.4));
		testLocations4.add(new LocationBean("22", 110.0, 113.0, 2113.5));
		testLocations4.add(new LocationBean("23", 111.0, 110.0, 2116.6));
		testLocations4.add(new LocationBean("24", 111.0, 111.0, 2217.7));
		testLocations4.add(new LocationBean("25", 111.0, 112.0, 2318.8));
		testLocations4.add(new LocationBean("26", 112.0, 112.0, 2119.9));
		testLocations4.add(new LocationBean("27", 112.0, 113.0, 1520.0));
		testLocations4.add(new LocationBean("28", 113.0, 110.0, 1721.1));
		testLocations4.add(new LocationBean("29", 113.0, 111.0, 1222.2));
		testLocations4.add(new LocationBean("30", 113.0, 112.0, 1418.3));
		testLocations4.add(new LocationBean("31", 112.0, 110.0, 1324.4));
		testLocations4.add(new LocationBean("32", 112.0, 111.0, 1618.5));
	}
	
	
	
	/* Tests for unused functions:
	
	//@Test
	public void LocationArrayConversionTest() {
		System.out.println("************ LocationArrayConversionTest");
		ContourHelper counterHelper = new ContourHelper(testLocations1);
		counterHelper.sortLocations();

		int rows = 4;
		int cols = 4;
		LocationBean[][] locationArray = counterHelper.createLocationArrayFromList(rows, cols);
		
		System.out.println(locationArray[0].length + " " + locationArray.length);
		
		counterHelper.printLocationWeightsAsMap(locationArray);
		System.out.println();
		System.out.flush();
		
		
	}
	
	
	//@Test
	public void LocationArrayExpansionTest() {
		System.out.println("************ LocationArrayExpansionTest");
		ContourHelper counterHelper = new ContourHelper(testLocations1);
		counterHelper.sortLocations();

		int rows = 4;
		int cols = 4;
		LocationBean[][] locationArray = counterHelper.createLocationArrayFromList(rows, cols);
		
		System.out.println("array width and height: " + locationArray[0].length + " " + locationArray.length);
		locationArray = counterHelper.expandArray(locationArray);
		System.out.println("array width and height: " + locationArray[0].length + " " + locationArray.length);
		
		counterHelper.printLocationWeightsAsMap(locationArray);
		counterHelper.fillInArray(locationArray, debug_level);
		counterHelper.printLocationWeightsAsMap(locationArray);
		
		locationArray = counterHelper.expandArray(locationArray);
		counterHelper.fillInArray(locationArray, debug_level);
		
		locationArray = counterHelper.expandArray(locationArray);
		counterHelper.fillInArray(locationArray, debug_level);
		
		locationArray = counterHelper.expandArray(locationArray);
		counterHelper.fillInArray(locationArray, debug_level);

		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);
		
		
		//counterHelper.printLocationWeightsAsMap(locationArray);
		counterHelper.printLocationsWithWeights(locationArray);
	}
	
	//@Test
	public void SettingLatsAndLonsTest() {
		System.out.println("************ SettingLatsAndLonsTest");
		ContourHelper counterHelper = new ContourHelper(testLocations2);
		counterHelper.sortLocations();

		int rows = 2;
		int cols = 2;
		LocationBean[][] locationArray = counterHelper.createLocationArrayFromList(rows, cols);
		
		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);
		
		System.out.println(locationArray[0][0].getLatitude() + " " + locationArray[0][0].getLongitude());
		System.out.println(locationArray[0][1].getLatitude() + " " + locationArray[0][1].getLongitude());
		System.out.println(locationArray[1][0].getLatitude() + " " + locationArray[1][0].getLongitude());
		System.out.println(locationArray[1][1].getLatitude() + " " + locationArray[1][1].getLongitude());
		
		counterHelper.printLocationsWithWeights(locationArray);
		//counterHelper.printLocationWeightsAsMap(locationArray);
		counterHelper.printLatLonsWeightsAsMap(locationArray);
		
	}

	//@Test
	public void CreateTestDataForPostGres() {
		System.out.println("************ CreateTestDataForPostGres");
		ContourHelper counterHelper = new ContourHelper(testLocations3);
		counterHelper.sortLocations();

		int rows = 4;
		int cols = 4;
		LocationBean[][] locationArray = counterHelper.createLocationArrayFromList(rows, cols);
		
		System.out.println("array width and height: " + locationArray[0].length + " " + locationArray.length);
		
		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.printLocationWeightsAsMap(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);
		
		counterHelper.printLocationWeightsAsMap(locationArray);
		
		
		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);
		
		
		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);
		
		
		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);

		
		//counterHelper.printLocationWeightsAsMap(locationArray);
		
		counterHelper.printLocationsWithWeights(locationArray);
	}
	
	//@Test
	public void WeightedGroupingText() {
		System.out.println("************ WeightedGroupingText");
		ContourHelper counterHelper = new ContourHelper(testLocations1);
		counterHelper.sortLocations();

		int rows = 4;
		int cols = 4;
		LocationBean[][] locationArray = counterHelper.createLocationArrayFromList(rows, cols);
		
		locationArray = counterHelper.expandArray(locationArray);
		
		counterHelper.fillInArray(locationArray, debug_level);
		
		counterHelper.groupLocationsByWeight(locationArray);
		
		Map<Integer, List<LocationBean>> groupings = counterHelper.groupLocationsByWeight(locationArray);
		
		System.out.println(groupings.size());
		
		counterHelper.printLocationWeightsAsMap(locationArray);
		
		List<LocationBean> grouping = groupings.get(14);
		counterHelper.printGrouping(grouping, locationArray);
		
		assertEquals(grouping.size(), 4);
	
		grouping = groupings.get(15);
		
		assertEquals(grouping.size(), 4);
	}
	*/
}
