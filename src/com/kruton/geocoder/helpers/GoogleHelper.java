package com.kruton.geocoder.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kruton.geocoder.beans.LocationBean;
import com.kruton.geocoder.utils.BasicURLRequest;

public class GoogleHelper {
	
	String distanceMatrixUrl = "http://maps.googleapis.com/maps/api/distancematrix/xml";
	String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/xml";
	int timeout = 30000;

	public ArrayList<LocationBean> gecodeLocations(ArrayList<LocationBean> locations) throws InterruptedException {
		for (LocationBean location : locations){
			String xml = getGeocodingXml(location.getAddress());
			parseGeocodeResponse(xml, location);
			
			TimeUnit.MILLISECONDS.sleep(200);
		}
		
		return locations;
	}
	
	public ArrayList<LocationBean> getDistances(ArrayList<LocationBean> destinations, ArrayList<LocationBean> origins) throws InterruptedException {
		for (LocationBean origin : origins){
			String xml = getDistanceMatrixXml(origin, destinations);
			parseDistanceMatrixResponse(xml, origin);
			
			TimeUnit.MILLISECONDS.sleep(400);
		}
		
		return origins;
	}

	
	/*
	 * Request methods
	 */
	public String getGeocodingXml(String address) {
		Properties props = new Properties();
		props.put("address", address);
		props.put("sensor", "false");  //indicates whether or not the request comes from a device with a location sensor
		
		return getXml(props, geocodeUrl);
	}
	
	public String getDistanceMatrixXml(LocationBean origin, List<LocationBean> destinations) {
		String destinationsString = "";
		boolean flag = false;
		for (LocationBean destination : destinations) {
			if (flag) destinationsString += "|";
			destinationsString += destination.getGoogleLatLon();
			flag = true;
		}
		
		Properties props = new Properties();
		props.put("origins", origin.getGoogleLatLon());
		props.put("destinations", destinationsString);
		props.put("mode", "driving");  //this is the default if it's not set, but we'll set it anyway to be thorough
		props.put("units", "imperial");  //metric is the default, so we'll set to imperial
		props.put("sensor", "false");  //indicates whether or not the request comes from a device with a location sensor
		
		return getXml(props, distanceMatrixUrl);
	}

	public String getXml(Properties props, String url) {
		String xml = "";

		try {
			xml = BasicURLRequest.makeRequest(url, props, timeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(xml);
		
		return xml;
	}
	
	public LocationBean parseDistanceMatrixResponse(String response, LocationBean locationBean) {
		try {
			//System.out.println(response);
			Document xmlDocument = setDataIntoXMLDocument(response);
			
			NodeList nList = xmlDocument.getElementsByTagName("element");
			
			//System.out.println("Min / Current Drivetimes at each step: ");
			
			int minDrivetime = -1;
			int size = nList.getLength();
			for (int i = 0; i < size; i++) {
				Element eElement = (Element) nList.item(i);
				NodeList durationList = eElement.getElementsByTagName("duration");
				Element durationElement = (Element) durationList.item(0);
				int drivetime = Integer.valueOf(getTagValue("value", durationElement, true));
				//System.out.println("   " + minDrivetime + " " + drivetime);
				if (minDrivetime == -1 || drivetime < minDrivetime)
					minDrivetime = drivetime;
			}

			locationBean.setWeight(Double.valueOf(minDrivetime));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		locationBean.printLatLonWeight();
		
		return locationBean;
	}
	
	public LocationBean parseGeocodeResponse(String request, LocationBean locationBean) {
		try {
			Document xmlDocument = setDataIntoXMLDocument(request);
			
			NodeList nList = xmlDocument.getElementsByTagName("GeocodeResponse");
			Element eElement = (Element) nList.item(0);
			String error = getTagValue("status", eElement, true);
			locationBean.setLatitude(Double.valueOf(getTagValue("lat", eElement, true)));
			locationBean.setLongitude(Double.valueOf(getTagValue("lng", eElement, true)));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return locationBean;
	}


	
	/*
	 * XML Methods
	 */
	private Document setDataIntoXMLDocument(String response) throws SAXException, IOException, ParserConfigurationException {

		StringReader reader = new StringReader(response);
		InputSource inputSource = new InputSource(reader);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document xmlDocument = dBuilder.parse(inputSource);
		xmlDocument.getDocumentElement().normalize();
		reader.close();

		return xmlDocument;
	}
	
	private String getTagValue(String sTag, Element eElement, boolean isRequired) {
		String value = null;
		try {
			if (eElement.getElementsByTagName(sTag) != null) {
				NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
				if (nlList != null) {
					Node nValue = (Node) nlList.item(0);

					if (nValue != null) {
						value = nValue.getNodeValue();
					}
				}
			}
		} catch (NullPointerException npe) {
			System.out.println("Tag with the name '" + sTag + "' not found!");

		}
		if (value == null && isRequired) {
			System.out.println("<ERROR>'" + sTag + "' is required field!</ERROR>");
		}
		return value;
	}
}
