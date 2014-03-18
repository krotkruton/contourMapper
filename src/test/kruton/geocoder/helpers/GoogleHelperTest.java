package test.kruton.geocoder.helpers;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.kruton.geocoder.beans.LocationBean;
import com.kruton.geocoder.helpers.CSVParser;
import com.kruton.geocoder.helpers.GoogleHelper;

public class GoogleHelperTest {
	
	private String testAddress = "1600 Amphitheatre Parkway Mountain View CA";
	private String gridPointsInputFile = "I:\\src\\Geocoder\\resources\\chicago_boundary_grid_points.csv";
	private String gridPointsOutputFile = "I:\\src\\Geocoder\\resources\\chicago_boundary_grid_points_weighted_advocate.csv";
	
	ArrayList<LocationBean> additionalHospitals = new ArrayList<LocationBean>();
	ArrayList<LocationBean> traumaHospitals = new ArrayList<LocationBean>();
	ArrayList<LocationBean> potentialTraumaHospitals = new ArrayList<LocationBean>();
	
	@Before
	public void setUp() throws Exception {
		additionalHospitals.add(new LocationBean("Advocate", 41.721245, -87.732248));
		
		traumaHospitals.add(new LocationBean("Advocate Illinois Masonic Medical Center", 41.9363313, -87.6525749));
		//traumaHospitals.add(new LocationBean("Ann & Robert H. Lurie Children's Hospital of Chicago", 41.8965342, -87.621529));
		traumaHospitals.add(new LocationBean("John H. Stroger, Jr. Hospital of Cook County", 41.873813, -87.67421));
		traumaHospitals.add(new LocationBean("Mount Sinai Hospital", 41.8601109, -87.6957067));
		traumaHospitals.add(new LocationBean("Northwestern Memorial Hospital", 41.894459, -87.62222));
		
		potentialTraumaHospitals.add(new LocationBean("Provident Hospital of Cook County", 41.8024886, -87.6135662));
		potentialTraumaHospitals.add(new LocationBean("University of Chicago Medical Center", 41.7882876, -87.6047495));
		potentialTraumaHospitals.add(new LocationBean("St. Bernard Hospital", 41.778199, -87.633328));
	}
	
	//@Test
	public void convertTest() {
		GoogleHelper helper = new GoogleHelper();
		
		String xml = helper.getGeocodingXml(testAddress);
		System.out.println(xml);
		
		LocationBean location = new LocationBean();
		location = helper.parseGeocodeResponse(xml, location);
		System.out.println(location.getLatitude());
	}
	
	//@Test
	public void distanceMatrixTest() {
		GoogleHelper helper = new GoogleHelper();
		
		ArrayList<LocationBean> destinations = new ArrayList<LocationBean>();
		ArrayList<LocationBean> origins = new ArrayList<LocationBean>();
		
		destinations.add(new LocationBean("Mt. Sinai", 41.8601109, -87.6957067));
		destinations.add(new LocationBean("Northwestern Memorial", 41.894459, -87.62222));
		
		origins.add(new LocationBean(41.899155, -87.726526));
		origins.add(new LocationBean(41.760564, -87.572815));
		origins.add(new LocationBean(41.8368979, -87.7219179));
		origins.add(new LocationBean(41.750759, -87.6540459));
		origins.add(new LocationBean(41.7935373, -87.7011292));
		origins.add(new LocationBean(41.6922565, -87.6253253));
		//Roseland Community Hospital (south side hospital)  41.6922565 -87.6253253

		try {
			origins = helper.getDistances(destinations, origins);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (LocationBean origin : origins) {
			origin.printLatLonWeightAsTime();
		}

	}
	
	
	//@Test
	public void CreateGridWeights() {

		//Get the hospitals and put them into the location array
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> gridPoints = new ArrayList<LocationBean>();
		parser.parseLatLonCSV(gridPoints, gridPointsInputFile);
		
		GoogleHelper helper = new GoogleHelper();
		try {
			gridPoints = helper.getDistances(traumaHospitals, gridPoints);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (LocationBean gridPoint : gridPoints) {
			gridPoint.printLatLonWeightAsTime();
		}
		
		parser.writeCSV(gridPoints, gridPointsOutputFile);
	}
	
	
	@Test
	public void CreateGridPotentialWeights() {

		//Get the hospitals and put them into the location array
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> gridPoints = new ArrayList<LocationBean>();
		parser.parseLatLonCSV(gridPoints, gridPointsInputFile);
		
		GoogleHelper helper = new GoogleHelper();
		try {
			gridPoints = helper.getDistances(additionalHospitals, gridPoints);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (LocationBean gridPoint : gridPoints) {
			gridPoint.printLatLonWeightAsTime();
		}
		
		parser.writeCSV(gridPoints, gridPointsOutputFile);
	}
}
