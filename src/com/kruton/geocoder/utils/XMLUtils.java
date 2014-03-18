package com.kruton.geocoder.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kruton.geocoder.beans.LocationBean;

public class XMLUtils {

	/*
	public LocationBean parse(String request) {
		try {
			Document xmlDocument = setDataIntoXMLDocument(request);
			parseHeader(xmlDocument);
			parseCustomer(xmlDocument);
			parseProgram(xmlDocument);
			userInfo.setMessageDescription(error.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public void parseHeader(Document xmlDocument) {
		NodeList nList = xmlDocument.getElementsByTagName("HEADER");
		Element eElement = (Element) nList.item(0);
		userInfo.setTransactionType(getTagValue("TRANSACTIONTYPE", eElement, true));
		userInfo.setLicense(getTagValue("LICENSE", eElement, true));
		userInfo.setExtTransactionId(getTagValue("EXTERNALID", eElement, true));
		userInfo.setSubId(getTagValue("SUBID", eElement, true));
		userInfo.setClientId(getTagValue("CLIENTID", eElement, false));
		userInfo.setTest(getTagValue("TEST", eElement, true));
		userInfo.setLeadId(getTagValue("LEADID", eElement, false));
	}
	*/
}
