package test.kruton.geocoder.helpers;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.kruton.geocoder.beans.LocationBean;
import com.kruton.geocoder.helpers.CSVParser;

public class CSVParserTest {
	
	
	private String hospitalsFile = "C:\\src\\Geocoder\\resources\\hospitals.csv";
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void CSVParserHospitalsTest() {
		CSVParser parser = new CSVParser();
		
		ArrayList<LocationBean> locations = new ArrayList<LocationBean>();
		
		parser.parseNameAddressCSV(locations, hospitalsFile);
		
		LocationBean location = locations.get(0);
		
		System.out.println(location.getName());
		System.out.println(location.getAddress());
	}
}
