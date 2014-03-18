package com.kruton.geocoder.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;


public class BasicURLRequest {
	
	public static final String CONTENT_TYPE_APP = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_TEXT_XML = "text/xml; charset=ISO-8859-1";

	public static String makeRequest(String urlStr, String body, int timeout, String type) {

		//System.out.println("Inside BasicURLRequest.makeRequest");
		//System.out.println("-- URL        - " + urlStr);
		
		String response = "";
		BufferedReader rd = null;
		OutputStreamWriter wr = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setReadTimeout(timeout);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.flush();

			// Get the response
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buff = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				buff.append(line);
			}
			response = buff.toString();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Convenience method to make a connection to a remote object referred to by the URL. 
	 * @param request the URL identifying the remote object
	 * @param timeout in milliseconds for the request
	 * @return the response from the remote object
	 * @throws IOException
	 */
	public static String makeRequest(String requestURL, int timeout) throws IOException {
		return makeRequest(requestURL, "", timeout, CONTENT_TYPE_APP);
	}
	
	/**
	 * Convenience method to make a connection to a remote object referred to by the URL. 
	 * @param request the URL identifying the remote object
	 * @param the name value pairs that should be appended to the URL.
	 * @param timeout in milliseconds for the request
	 * @return the response from the remote object
	 * @throws IOException
	 */
	public static String makeRequest(String requestURL, Properties props, int timeout) throws IOException {
		return makeRequest(requestURL, props, timeout, CONTENT_TYPE_APP);
	}
	
	/**
	 * Convenience method to make a connection to a remote object referred to by the URL with additional properties
	 * @param url the URL identifying the remote object
	 * @param the name value pairs that should be appended to the URL.
	 * @param timeout in milliseconds for the request
	 * @param content type
	 * @return the response from the remote object
	 * @throws IOException
	 */
	public static String makeRequest(String requestURL, Properties props, int timeout, String type) throws IOException {
		StringBuffer newUrl = new StringBuffer();
		newUrl.append(requestURL);
		newUrl.append("?");
		
		Iterator<?> iter = props.keySet().iterator();
		String key = "";
		while (iter.hasNext()) {
			key = (String) iter.next();
			newUrl.append(URLEncoder.encode(key, "UTF-8"));
			newUrl.append("=");
			newUrl.append(URLEncoder.encode(props.getProperty(key), "UTF-8"));
			newUrl.append("&");
		}

		//delete the last ampersand(&)
		newUrl.deleteCharAt(newUrl.length()-1);

	    return makeRequest(newUrl.toString(), newUrl.toString(), timeout, type);
	    
	}


}
