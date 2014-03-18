package test.kruton.geocoder.helpers;

import static org.junit.Assert.*;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;

import com.kruton.geocoder.beans.LocationBean;

public class LocationBeanTest {
	
	private LocationBean location1 = new LocationBean(10.0,10.0);
	private LocationBean location2 = new LocationBean(11.0,11.0);
	private LocationBean location3 = new LocationBean(42.00392,-87.9351);
	private LocationBean location4 = new LocationBean(41.96392,-87.9301);
	private LocationBean location5 = new LocationBean(41.96892,-87.9301);
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void LocationBeanDistanceTest() {
		double distance = location1.getDistanceFrom(location2);
		DecimalFormat df = new DecimalFormat("#.#####");
		
		assertEquals(df.format(distance), "1.41421");
	}
	
	@Test
	public void LocationBeanClosenessTest() {
		//double distance = location1.getDistanceFrom(location2);
		
		assertEquals(location1.isClose(location2, 2), true);
		assertEquals(location1.isClose(location2, 1.5), true);
		assertEquals(location1.isClose(location2, 1.4), false);
		assertEquals(location1.isClose(location2, 1), false);
	}
	
	
	@Test
	public void LocationBeanRealDistanceTest() {
		//double distance = location1.getDistanceFrom(location2);
		
		
		System.out.println(location3.getDistanceFrom(location4));
		System.out.println(location3.getDistanceFrom(location5));
		System.out.println(location4.getDistanceFrom(location5));
		
		assertEquals(location3.isClose(location4, .04), false);
		assertEquals(location3.isClose(location4, .05), true);
		assertEquals(location3.isClose(location5, .04), true);
		assertEquals(location3.isClose(location5, .03), false);
		
		assertEquals(location4.isClose(location5, .04), true);
	}
}
