package com.trainor.controlandmeasurement.HelperClass;

import android.util.Log;

import org.json.JSONArray;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebServiceCall {

    public static JSONArray callSoapAPI(String SoapBody, String SoapAction) {
        JSONArray jsonArray = null;
        String xmlstring = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:log=\"http://letter.services.ws.measurements.trainor.no/\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                SoapBody +
                "   </soap:Body>\n" +
                "</soap:Envelope>";
        String response = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URLs.URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8");
            connection.setRequestProperty("SOAPAction", SoapAction);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            bufferedWriter.write(xmlstring);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                InputStream inputStream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
            Document document = JSONParser.loadXMLString(response);
            jsonArray = JSONParser.getFullData(document);
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
        return jsonArray;
    }
}
