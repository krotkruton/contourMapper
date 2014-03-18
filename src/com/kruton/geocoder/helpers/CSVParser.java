package com.kruton.geocoder.helpers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.kruton.geocoder.beans.LocationBean;


/*
 * This class contains methods for reading and writing specific files.  They are generally not reusable.
 */
public class CSVParser {


	public ArrayList<LocationBean> parseLatLonCSV(ArrayList<LocationBean> locations, String filename) {
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename));

		    String [] nextLine;
		    nextLine = reader.readNext();
		    System.out.println(nextLine[0] + nextLine[1]);
		    if (!(nextLine[0].toLowerCase().equals("latitude") || nextLine[1].toLowerCase().equals("longitude")))
		    	System.out.println("First line was not latitude and longitude - check format and retry");
		    else {
			    while ((nextLine = reader.readNext()) != null) {
			        // nextLine[] is an array of values from the line
			        //System.out.println(nextLine[0] + nextLine[1]);
			        locations.add(new LocationBean(Double.valueOf(nextLine[0]), Double.valueOf(nextLine[1])));
			    }
		    }
		    
		    reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	    return locations;
	}
	
	public ArrayList<LocationBean> parseLatLonWeightTypeCSV(ArrayList<LocationBean> locations, String filename) {
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename));

		    String [] nextLine;
		    nextLine = reader.readNext();
		    System.out.println(nextLine[0] + nextLine[1]);
		    if (!(nextLine[0].toLowerCase().equals("latitude") || nextLine[1].toLowerCase().equals("longitude") || nextLine[2].toLowerCase().equals("weight") || nextLine[3].toLowerCase().equals("type")))
		    	System.out.println("First line was not latitude, longitude, weight, type - check format and retry");
		    else {
			    while ((nextLine = reader.readNext()) != null) {
			        // nextLine[] is an array of values from the line
			        //System.out.println(nextLine[0] + nextLine[1]);
			        locations.add(new LocationBean(Double.valueOf(nextLine[0]), Double.valueOf(nextLine[1]), Double.valueOf(nextLine[2])));
			    }
		    }
		    
		    reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	    return locations;
	}
	
	public ArrayList<LocationBean> parseNameLatLonWeightTypeCSV(ArrayList<LocationBean> locations, String filename) {
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename));

		    String [] nextLine;
		    nextLine = reader.readNext();
		    System.out.println(nextLine[0] + " " + nextLine[1] + " " + nextLine[2] + " " + nextLine[3] + " " + nextLine[4]);
		    if (!(nextLine[0].toLowerCase().equals("name") || nextLine[1].toLowerCase().equals("latitude") || nextLine[2].toLowerCase().equals("longitude") || nextLine[3].toLowerCase().equals("weight") || nextLine[4].toLowerCase().equals("type")))
		    	System.out.println("First line was not name, latitude, longitude, weight, type - check format and retry");
		    else {
			    while ((nextLine = reader.readNext()) != null) {
			        // nextLine[] is an array of values from the line
			        //System.out.println(nextLine[0] + nextLine[1]);
			        locations.add(new LocationBean(nextLine[0], Double.valueOf(nextLine[1]), Double.valueOf(nextLine[2]), Double.valueOf(nextLine[3])));
			    }
		    }
		    
		    reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	    return locations;
	}
	
	
	public ArrayList<LocationBean> parseNameAddressCSV(ArrayList<LocationBean> locations, String filename) {
		
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename));

		    String [] nextLine;
		    nextLine = reader.readNext();
		    System.out.println(nextLine[0] + nextLine[1]);
		    if (!(nextLine[0].toLowerCase().equals("name") || nextLine[1].toLowerCase().equals("address")))
		    	System.out.println("First line was not name and address - check format and retry");
		    else {
			    while ((nextLine = reader.readNext()) != null) {
			        // nextLine[] is an array of values from the line
			        //System.out.println(nextLine[0] + nextLine[1]);
			        locations.add(new LocationBean(nextLine[0], nextLine[1]));
			    }
		    }
		    
		    reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	    return locations;
	}
	
	public void writeCSV(ArrayList<LocationBean> locations, String filename) {
		CSVWriter writer;
		
		try {
			writer = new CSVWriter(new FileWriter(filename));
		     
		    for(LocationBean location : locations) {
		    	writer.writeNext(location.getStringArray());
		    }
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void writeFullCSV(ArrayList<LocationBean> locations, String filename) {
		CSVWriter writer;
		
		try {
			writer = new CSVWriter(new FileWriter(filename));
		     
		    for(LocationBean location : locations) {
		    	writer.writeNext(location.getNameLatLonWeightStringArray());
		    }
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Have not tested this yet, so using Deprecated to indicate that
	public void writeFullCSVWithGrouping(Map<Integer, Map<Integer, List<LocationBean>>> locations, String filename) {
		CSVWriter writer;
		
		try {
			writer = new CSVWriter(new FileWriter(filename));
		    
			for (int weight : locations.keySet()) {
				Map<Integer, List<LocationBean>> groups = locations.get(weight);
				for (int group : groups.keySet()) {
					System.out.println("w " + weight + " g " + group);
					List<LocationBean> clusters = groups.get(group); 

				    for(LocationBean location : clusters) {
				    	String[] stringArray = new String[5];
				    	stringArray[0] = location.getName();
				    	stringArray[1] = location.getLatitude().toString();
				    	stringArray[2] = location.getLongitude().toString();
				    	stringArray[3] = Double.toString(location.getWeight());
				    	stringArray[4] = Integer.toString(group);
				    	writer.writeNext(stringArray);
				    }
				}

			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	//This works if you only want to print one group
	@Deprecated
	public void writeFullCSVWithGrouping(List<LocationBean> locations, int grouping, String filename) {
		CSVWriter writer;
		
		try {
			writer = new CSVWriter(new FileWriter(filename));
		     
		    for(LocationBean location : locations) {
		    	String[] stringArray = new String[5];
		    	stringArray[0] = location.getName();
		    	stringArray[1] = location.getLatitude().toString();
		    	stringArray[2] = location.getLongitude().toString();
		    	stringArray[3] = location.getWeight().toString();
		    	stringArray[4] = Integer.toString(grouping);
		    	writer.writeNext(stringArray);
		    }
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
