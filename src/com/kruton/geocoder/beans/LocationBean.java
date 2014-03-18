package com.kruton.geocoder.beans;

import java.text.DecimalFormat;


public class LocationBean implements Comparable<LocationBean>{
	private String name;
	private String address;
	private Double latitude = 0.0;
	private Double longitude = 0.0;
	private Double weight = 0.0;
	private boolean added = false;
	private int group = 0;

	public LocationBean() {

	}
	
	public LocationBean(LocationBean location) {
		this.name = location.name;
		this.address = location.address;
		this.latitude = location.latitude;
		this.longitude = location.longitude;
		this.weight = location.weight;
		this.added = location.added;
		this.group = location.group;
	}
	
	public LocationBean(String name, String address) {
		this.setName(name);
		this.address = address;
	}
	
	
	public LocationBean(Double lat, Double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public LocationBean(Double lat, Double lon, Double weight) {
		this.latitude = lat;
		this.longitude = lon;
		this.weight = weight;
	}
	
	public LocationBean(String name, Double lat, Double lon) {
		this.name = name;
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public LocationBean(String name, Double lat, Double lon, Double weight) {
		this.name = name;
		this.latitude = lat;
		this.longitude = lon;
		this.weight = weight;
	}
	

	public int compareTo(LocationBean otherLocation) {
    	
    	int xComp = Double.compare(this.getLatitude(), otherLocation.getLatitude());
        if(xComp == 0.0)
            return Double.compare(this.getLongitude(), otherLocation.getLongitude());
        else
            return xComp;
	}
	
	
	/*
	 * Comparisons
	 */
	public boolean isClose(LocationBean location, double closeness) {
		double distance = getDistanceFrom(location);
		
		return distance <= closeness;
	}
	
	public double getDistanceFrom(LocationBean location) {
		double distance = 0.0;
		
		double latDistance = Math.abs(this.latitude - location.getLatitude());
		double lonDistance = Math.abs(this.longitude - location.getLongitude());
		
		distance = Math.sqrt(Math.pow(latDistance, 2) + Math.pow(lonDistance, 2));
		
		return distance;
	}
	
	
	/*
	 * Specialty Getters and Setters
	 */

	public int getTestRoundedWeight() {
		return (int) Math.round(weight);
	}
	
	//Since google returns drivetimes in seconds (result data min=13 max=2330), I'm going to use 3 minute intervals to display the data, which is where the 180 comes from, then multiplying 
	// by 3 again will get the actual drivetime in minutes.  This way, all results will be a multiple of 3.  Also, using Math.floor so all within a group are at least that many minutes away
	public int getGoogleContourWeightFloor() {
		return (int) Math.floor(weight/180) * 3;
	}
	
	public int getGoogleContourWeightFloor(int minutes) {
		return (int) Math.floor(weight/(60 * minutes)) * minutes;
	}
	
	public String[] getStringArray() {
		return new String[]{name, address, latitude.toString(), longitude.toString()}; 
	}
	
	public String[] getNameLatLonWeightStringArray() {
		return new String[]{name, latitude.toString(), longitude.toString(), weight.toString()};
	}
	
	// Google requires lat / lon pairs to be in the form 41.43206,-81.38992 (no spaces) for the Distance Matrix
	public String getGoogleLatLon() {
		//DecimalFormat df = new DecimalFormat("#.00000");
		//return df.format(this.latitude) + "," + df.format(this.longitude);
		
		return this.latitude + "," + this.longitude;
	}

	
	
	/*
	 * Standard Getters and Setters
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public void setAdded(boolean value) {
		this.added = value;
	}
	
	public boolean isAdded() {
		return added;
	}
	
	/*
	 * Print methods
	 */
	public void printLatLonWeight() {
		DecimalFormat df = new DecimalFormat("#.00000");
		printLatLonWeight(df);
	}
	
	public void printLatLonWeight(DecimalFormat df) {
		System.out.println(df.format(this.latitude) + " " + df.format(this.longitude) + " " + this.weight);
	}
	
	public void printLatLonWeightAsTime() {
		DecimalFormat df = new DecimalFormat("#.00000");
		int hours = (int) Math.floor(this.weight / 3600);
		int minutes = (int) Math.floor((this.weight % 3600) / 60);
		int seconds = (int) Math.floor((this.weight % 60 ));
		
		System.out.println(df.format(this.latitude) + " " + df.format(this.longitude) + " " + hours + ":" + minutes + ":" + seconds);
	}
}
