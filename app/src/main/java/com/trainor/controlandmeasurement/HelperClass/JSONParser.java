package com.trainor.controlandmeasurement.HelperClass;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class JSONParser {

    public static Document loadXMLString(String response) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(response));
        return db.parse(is);
    }

    public static JSONArray getFullData(Document document) throws Exception {
        JSONArray resultArray = new JSONArray();
        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("return")) {
                    JSONObject rootObject = new JSONObject();
                    NodeList childNodeList = nodeList.item(i).getChildNodes();
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        node = childNodeList.item(j);
                        String _name = node.getNodeName();
                        if (node.getChildNodes().getLength() != 0) {
                            String val = node.getFirstChild().getNodeValue();
                            rootObject.put(_name, val);
                        } else {
                            rootObject.put(_name, "");
                        }
                    }
                    resultArray.put(rootObject);
                }
            }
        }
        return resultArray;
    }
}
