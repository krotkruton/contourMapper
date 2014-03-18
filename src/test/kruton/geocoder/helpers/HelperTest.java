package test.kruton.geocoder.helpers;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.kruton.geocoder.beans.LocationBean;
import com.kruton.geocoder.helpers.CSVParser;
import com.kruton.geocoder.helpers.GoogleHelper;

public class HelperTest {
	
	
	private String hospitalsInputFile = "C:\\src\\Geocoder\\resources\\hospitals.csv";
	private String hospitalsOutputFile = "C:\\src\\Geocoder\\resources\\hospitals_geocoded.csv";
	private String gunshotsInputFile = "I:\\src\\Geocoder\\resources\\2013gunshots_address.csv";
	private String gunshotsOutputFile = "I:\\src\\Geocoder\\resources\\2013gunshots_geocoded.csv";
	
	@Before
	public void setUp() throws Exception {
	}
	
	//@Test
	public void CSVParserHospitalsTest() {

		//Get the hospitals and put them into the location array
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> hospitals = new ArrayList<LocationBean>();
		parser.parseNameAddressCSV(hospitals, hospitalsInputFile);
		
		
		//Use Google to put the lat/lon into each location
		ArrayList<LocationBean> locations = new ArrayList<LocationBean>();
		for (int i = 0; i < 30; i++) {  // only use about 10 hospitals for testing
			locations.add(hospitals.get(i));
		}
		GoogleHelper helper = new GoogleHelper();
		try {
			helper.gecodeLocations(locations);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(locations.get(0));
		
		parser.writeCSV(locations, hospitalsOutputFile);
		
	}
	
	//@Test
	public void geocodeHospitals() {
		//Get the hospitals and put them into the location array
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> hospitals = new ArrayList<LocationBean>();
		parser.parseNameAddressCSV(hospitals, hospitalsInputFile);
		
		
		//Use Google to put the lat/lon into each location
		GoogleHelper helper = new GoogleHelper();
		try {
			helper.gecodeLocations(hospitals);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(hospitals.get(0));
		
		parser.writeCSV(hospitals, hospitalsOutputFile);
	}
	
	@Test
	public void geocodeGunshots() {
		//Get the hospitals and put them into the location array
		CSVParser parser = new CSVParser();
		ArrayList<LocationBean> gunshots = new ArrayList<LocationBean>();
		parser.parseNameAddressCSV(gunshots, gunshotsInputFile);
		
		
		//Use Google to put the lat/lon into each location
		GoogleHelper helper = new GoogleHelper();
		try {
			helper.gecodeLocations(gunshots);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(gunshots.get(0));
		
		parser.writeCSV(gunshots, gunshotsOutputFile);
	}
	
	
	
	
}