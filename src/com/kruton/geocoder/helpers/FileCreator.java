package com.kruton.geocoder.helpers;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.kruton.geocoder.beans.LocationBean;


/*
 * This class contains methods for reading and writing specific files.  They are generally not reusable.
 */
public class FileCreator {

	
	
	public void buildPolygonFile(List<LocationBean> locations, String outputFileName) {
		StringBuffer output = new StringBuffer();
		
		//create top of file
		
		buildPolygonText(locations, output);
		
		//create end of file
		
		createFile(outputFileName, output);
	}
	
	public void buildPolygonText(List<LocationBean> locations, StringBuffer output) {
		int count = locations.size();
		
		for(LocationBean location : locations) {
			output.append("		new google.maps.LatLng(");
			output.append(location.getLatitude());
			output.append(", ");
			output.append(location.getLongitude());
			output.append(")");
			if (count > 0)
				output.append(",");
			output.append("\r\n");
		}
	}
	
	
	
	public void createFile(String outputFileName, StringBuffer output) {
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(outputFileName), "utf-8"));
		    writer.write(output.toString());
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	

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
}
