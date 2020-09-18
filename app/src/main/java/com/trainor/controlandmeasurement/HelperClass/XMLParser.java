package com.trainor.controlandmeasurement.HelperClass;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class XMLParser {
    public static volatile XMLParser instance;
    public static String basedata = "[" +
            "    {" +
            "        \"version\": \"7.21\"," +
            "        \"climbFactors\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"P2-P3-P4\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"P3-P4-P5\"" +
            "            }," +
            "            {" +
            "                \"id\": \"3\"," +
            "                \"name\": \"P4-P5-P6\"" +
            "            }," +
            "            {" +
            "                \"id\": \"4\"," +
            "                \"name\": \"P5-P6-P7\"" +
            "            }," +
            "            {" +
            "                \"id\": \"5\"," +
            "                \"name\": \"P6-P7-P8\"" +
            "            }," +
            "            {" +
            "                \"id\": \"6\"," +
            "                \"name\": \"P7-P8-P9\"" +
            "            }," +
            "            {" +
            "                \"id\": \"7\"," +
            "                \"name\": \"P8-P9-P10\"" +
            "            }," +
            "            {" +
            "                \"id\": \"8\"," +
            "                \"name\": \"P2-P4-P6\"" +
            "            }," +
            "            {" +
            "                \"id\": \"9\"," +
            "                \"name\": \"P3-P5-P7\"" +
            "            }," +
            "            {" +
            "                \"id\": \"10\"," +
            "                \"name\": \"P4-P6-P8\"" +
            "            }," +
            "            {" +
            "                \"id\": \"11\"," +
            "                \"name\": \"P5-P7-P9\"" +
            "            }," +
            "            {" +
            "                \"id\": \"12\"," +
            "                \"name\": \"P6-P8-P10\"" +
            "            }," +
            "            {" +
            "                \"id\": \"13\"," +
            "                \"name\": \"P2-P5-P8\"" +
            "            }," +
            "            {" +
            "                \"id\": \"14\"," +
            "                \"name\": \"P3-P6-P9\"" +
            "            }," +
            "            {" +
            "                \"id\": \"15\"," +
            "                \"name\": \"P4-P7-P10\"" +
            "            }," +
            "            {" +
            "                \"id\": \"16\"," +
            "                \"name\": \"P2-P6-P10\"" +
            "            }" +
            "        ]," +
            "        \"moisture\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"Tørt\"," +
            "                \"value\": \"0.0\"," +
            "                \"default\": \"true\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"Fuktig\"," +
            "                \"value\": \"0.2\"" +
            "            }," +
            "            {" +
            "                \"id\": \"3\"," +
            "                \"name\": \"Vått\"," +
            "                \"value\": \"0.4\"" +
            "            }" +
            "        ]," +
            "        \"earthtypes\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"Leire\"," +
            "                \"value\": \"1.0\"," +
            "                \"default\": \"true\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"Jord\"," +
            "                \"value\": \"1.1\"" +
            "            }," +
            "            {" +
            "                \"id\": \"3\"," +
            "                \"name\": \"Sand\"," +
            "                \"value\": \"1.25\"" +
            "            }," +
            "            {" +
            "                \"id\": \"4\"," +
            "                \"name\": \"Kalk, kritt\"," +
            "                \"value\": \"1.3\"" +
            "            }," +
            "            {" +
            "                \"id\": \"5\"," +
            "                \"name\": \"Fyllmasse\"," +
            "                \"value\": \"1.35\"" +
            "            }," +
            "            {" +
            "                \"id\": \"6\"," +
            "                \"name\": \"Skifer\"," +
            "                \"value\": \"1.4\"" +
            "            }," +
            "            {" +
            "                \"id\": \"7\"," +
            "                \"name\": \"Granitt\"," +
            "                \"value\": \"1.5\"" +
            "            }," +
            "            {" +
            "                \"id\": \"8\"," +
            "                \"name\": \"Rullestein, morene\"," +
            "                \"value\": \"1.6\"" +
            "            }" +
            "        ]," +
            "        \"seasons\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"Vinter (des - feb)\"," +
            "                \"value\": \"0.05\"," +
            "                \"default\": \"true\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"Vår (mars - mai)\"," +
            "                \"value\": \"0.13\"" +
            "            }," +
            "            {" +
            "                \"id\": \"3\"," +
            "                \"name\": \"Sommer (juni - aug)\"," +
            "                \"value\": \"0.0\"" +
            "            }," +
            "            {" +
            "                \"id\": \"4\"," +
            "                \"name\": \"Høst (sept - nov)\"," +
            "                \"value\": \"0.1\"" +
            "            }" +
            "        ]," +
            "        \"voltage\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"Lavspenning\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"Høyspenning\"," +
            "                \"default\": \"true\"" +
            "            }," +
            "            {" +
            "                \"id\": \"3\"," +
            "                \"name\": \"Lavspenning i forsyningsanlegg\"" +
            "            }" +
            "        ]," +
            "        \"electrodes\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"Gjennomgående jord\"," +
            "                \"default\": \"true\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"Lokal elektrode\"" +
            "            }" +
            "        ]," +
            "        \"disconnectTimes\": [" +
            "            {" +
            "                \"id\": \"1\"," +
            "                \"name\": \"0,05 s\"," +
            "                \"seconds\": \"0.05\"," +
            "                \"voltage\": \"716\"" +
            "            }," +
            "            {" +
            "                \"id\": \"2\"," +
            "                \"name\": \"0,07 s\"," +
            "                \"seconds\": \"0.07\"," +
            "                \"voltage\": \"680\"" +
            "            }," +
            "            {" +
            "                \"id\": \"3\"," +
            "                \"name\": \"0,10 s\"," +
            "                \"seconds\": \"0.10\"," +
            "                \"voltage\": \"654\"" +
            "            }," +
            "            {" +
            "                \"id\": \"4\"," +
            "                \"name\": \"0,15 s\"," +
            "                \"seconds\": \"0.15\"," +
            "                \"voltage\": \"600\"" +
            "            }," +
            "            {" +
            "                \"id\": \"5\"," +
            "                \"name\": \"0,20 s\"," +
            "                \"seconds\": \"0.20\"," +
            "                \"voltage\": \"537\"" +
            "            }," +
            "            {" +
            "                \"id\": \"6\"," +
            "                \"name\": \"0,25 s\"," +
            "                \"seconds\": \"0.25\"," +
            "                \"voltage\": \"460\"" +
            "            }," +
            "            {" +
            "                \"id\": \"7\"," +
            "                \"name\": \"0,30 s\"," +
            "                \"seconds\": \"0.30\"," +
            "                \"voltage\": \"420\"" +
            "            }," +
            "            {" +
            "                \"id\": \"8\"," +
            "                \"name\": \"0,40 s\"," +
            "                \"seconds\": \"0.40\"," +
            "                \"voltage\": \"300\"" +
            "            }," +
            "            {" +
            "                \"id\": \"9\"," +
            "                \"name\": \"0,50 s\"," +
            "                \"seconds\": \"0.50\"," +
            "                \"voltage\": \"220\"," +
            "                \"default\": \"true\"" +
            "            }," +
            "            {" +
            "                \"id\": \"10\"," +
            "                \"name\": \"0,70 s\"," +
            "                \"seconds\": \"0.70\"," +
            "                \"voltage\": \"150\"" +
            "            }," +
            "            {" +
            "                \"id\": \"11\"," +
            "                \"name\": \"1,00 s\"," +
            "                \"seconds\": \"1.00\"," +
            "                \"voltage\": \"117\"" +
            "            }," +
            "            {" +
            "                \"id\": \"12\"," +
            "                \"name\": \"1,50 s\"," +
            "                \"seconds\": \"1.50\"," +
            "                \"voltage\": \"103\"" +
            "            }," +
            "            {" +
            "                \"id\": \"13\"," +
            "                \"name\": \"2,00 s\"," +
            "                \"seconds\": \"2.00\"," +
            "                \"voltage\": \"96\"" +
            "            }," +
            "            {" +
            "                \"id\": \"14\"," +
            "                \"name\": \"2,50 s\"," +
            "                \"seconds\": \"2.50\"," +
            "                \"voltage\": \"95\"" +
            "            }," +
            "            {" +
            "                \"id\": \"15\"," +
            "                \"name\": \"3,00 s\"," +
            "                \"seconds\": \"3.00\"," +
            "                \"voltage\": \"90\"" +
            "            }," +
            "            {" +
            "                \"id\": \"16\"," +
            "                \"name\": \"4,00 s\"," +
            "                \"seconds\": \"4.00\"," +
            "                \"voltage\": \"88\"" +
            "            }," +
            "            {" +
            "                \"id\": \"17\"," +
            "                \"name\": \"5,00 s\"," +
            "                \"seconds\": \"5.00\"," +
            "                \"voltage\": \"86\"" +
            "            }," +
            "            {" +
            "                \"id\": \"18\"," +
            "                \"name\": \"6,00 s\"," +
            "                \"seconds\": \"6.00\"," +
            "                \"voltage\": \"85\"" +
            "            }," +
            "            {" +
            "                \"id\": \"19\"," +
            "                \"name\": \"7,00 s\"," +
            "                \"seconds\": \"7.00\"," +
            "                \"voltage\": \"85\"" +
            "            }," +
            "            {" +
            "                \"id\": \"20\"," +
            "                \"name\": \"8,00 s\"," +
            "                \"seconds\": \"8.00\"," +
            "                \"voltage\": \"85\"" +
            "            }," +
            "            {" +
            "                \"id\": \"21\"," +
            "                \"name\": \"9,00 s\"," +
            "                \"seconds\": \"9.00\"," +
            "                \"voltage\": \"85\"" +
            "            }," +
            "            {" +
            "                \"id\": \"22\"," +
            "                \"name\": \"10,00 s\"," +
            "                \"seconds\": \"10.00\"," +
            "                \"voltage\": \"85\"" +
            "            }," +
            "            {" +
            "                \"id\": \"23\"," +
            "                \"name\": \"> 10,00 s\"," +
            "                \"seconds\": \"-1.00\"," +
            "                \"voltage\": \"80\"" +
            "            }" +
            "        ]," +
            "        \"referenceDistances\": [" +
            "            {" +
            "                \"u\": \"0.40\"," +
            "                \"p\": \"0.6432\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.41\"," +
            "                \"p\": \"0.6418\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.42\"," +
            "                \"p\": \"0.6403\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.43\"," +
            "                \"p\": \"0.6389\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.44\"," +
            "                \"p\": \"0.6374\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.45\"," +
            "                \"p\": \"0.6360\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.46\"," +
            "                \"p\": \"0.6346\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.47\"," +
            "                \"p\": \"0.6331\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.48\"," +
            "                \"p\": \"0.6317\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.49\"," +
            "                \"p\": \"0.6302\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.50\"," +
            "                \"p\": \"0.6288\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.51\"," +
            "                \"p\": \"0.6273\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.52\"," +
            "                \"p\": \"0.6258\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.53\"," +
            "                \"p\": \"0.6242\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.54\"," +
            "                \"p\": \"0.6227\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.55\"," +
            "                \"p\": \"0.6212\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.56\"," +
            "                \"p\": \"0.6197\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.57\"," +
            "                \"p\": \"0.6182\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.58\"," +
            "                \"p\": \"0.6166\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.59\"," +
            "                \"p\": \"0.6151\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.60\"," +
            "                \"p\": \"0.6136\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.61\"," +
            "                \"p\": \"0.6120\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.62\"," +
            "                \"p\": \"0.6104\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.63\"," +
            "                \"p\": \"0.6087\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.64\"," +
            "                \"p\": \"0.6071\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.65\"," +
            "                \"p\": \"0.6055\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.66\"," +
            "                \"p\": \"0.6039\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.67\"," +
            "                \"p\": \"0.6023\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.68\"," +
            "                \"p\": \"0.6006\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.69\"," +
            "                \"p\": \"0.5990\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.70\"," +
            "                \"p\": \"0.5974\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.71\"," +
            "                \"p\": \"0.5957\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.72\"," +
            "                \"p\": \"0.5940\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.73\"," +
            "                \"p\": \"0.5923\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.74\"," +
            "                \"p\": \"0.5906\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.75\"," +
            "                \"p\": \"0.5889\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.76\"," +
            "                \"p\": \"0.5871\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.77\"," +
            "                \"p\": \"0.5854\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.78\"," +
            "                \"p\": \"0.5837\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.79\"," +
            "                \"p\": \"0.5820\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.80\"," +
            "                \"p\": \"0.5803\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.81\"," +
            "                \"p\": \"0.5785\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.82\"," +
            "                \"p\": \"0.5766\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.83\"," +
            "                \"p\": \"0.5748\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.84\"," +
            "                \"p\": \"0.5729\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.85\"," +
            "                \"p\": \"0.5711\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.86\"," +
            "                \"p\": \"0.5692\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.87\"," +
            "                \"p\": \"0.5674\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.88\"," +
            "                \"p\": \"0.5655\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.89\"," +
            "                \"p\": \"0.5637\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.90\"," +
            "                \"p\": \"0.5618\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.91\"," +
            "                \"p\": \"0.5598\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.92\"," +
            "                \"p\": \"0.5578\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.93\"," +
            "                \"p\": \"0.5557\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.94\"," +
            "                \"p\": \"0.5537\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.95\"," +
            "                \"p\": \"0.5517\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.96\"," +
            "                \"p\": \"0.5497\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.97\"," +
            "                \"p\": \"0.5477\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.98\"," +
            "                \"p\": \"0.5456\"" +
            "            }," +
            "            {" +
            "                \"u\": \"0.99\"," +
            "                \"p\": \"0.5436\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.00\"," +
            "                \"p\": \"0.5416\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.01\"," +
            "                \"p\": \"0.5394\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.02\"," +
            "                \"p\": \"0.5371\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.03\"," +
            "                \"p\": \"0.5349\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.04\"," +
            "                \"p\": \"0.5327\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.05\"," +
            "                \"p\": \"0.5305\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.06\"," +
            "                \"p\": \"0.5282\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.07\"," +
            "                \"p\": \"0.5260\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.08\"," +
            "                \"p\": \"0.5238\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.09\"," +
            "                \"p\": \"0.5215\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.10\"," +
            "                \"p\": \"0.5193\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.11\"," +
            "                \"p\": \"0.5168\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.12\"," +
            "                \"p\": \"0.5143\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.13\"," +
            "                \"p\": \"0.5118\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.14\"," +
            "                \"p\": \"0.5093\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.15\"," +
            "                \"p\": \"0.5068\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.16\"," +
            "                \"p\": \"0.5042\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.17\"," +
            "                \"p\": \"0.5017\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.18\"," +
            "                \"p\": \"0.4992\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.19\"," +
            "                \"p\": \"0.4967\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.20\"," +
            "                \"p\": \"0.4942\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.21\"," +
            "                \"p\": \"0.4913\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.22\"," +
            "                \"p\": \"0.4884\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.23\"," +
            "                \"p\": \"0.4855\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.24\"," +
            "                \"p\": \"0.4826\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.25\"," +
            "                \"p\": \"0.4797\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.26\"," +
            "                \"p\": \"0.4768\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.27\"," +
            "                \"p\": \"0.4739\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.28\"," +
            "                \"p\": \"0.4710\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.29\"," +
            "                \"p\": \"0.4681\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.30\"," +
            "                \"p\": \"0.4652\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.31\"," +
            "                \"p\": \"0.4618\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.32\"," +
            "                \"p\": \"0.4583\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.33\"," +
            "                \"p\": \"0.4549\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.34\"," +
            "                \"p\": \"0.4515\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.35\"," +
            "                \"p\": \"0.4481\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.36\"," +
            "                \"p\": \"0.4446\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.37\"," +
            "                \"p\": \"0.4412\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.38\"," +
            "                \"p\": \"0.4378\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.39\"," +
            "                \"p\": \"0.4343\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.40\"," +
            "                \"p\": \"0.4309\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.41\"," +
            "                \"p\": \"0.4267\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.42\"," +
            "                \"p\": \"0.4225\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.43\"," +
            "                \"p\": \"0.4183\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.44\"," +
            "                \"p\": \"0.4141\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.45\"," +
            "                \"p\": \"0.4099\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.46\"," +
            "                \"p\": \"0.4056\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.47\"," +
            "                \"p\": \"0.4014\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.48\"," +
            "                \"p\": \"0.3972\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.49\"," +
            "                \"p\": \"0.3930\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.50\"," +
            "                \"p\": \"0.3888\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.51\"," +
            "                \"p\": \"0.3840\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.52\"," +
            "                \"p\": \"0.3791\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.53\"," +
            "                \"p\": \"0.3740\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.54\"," +
            "                \"p\": \"0.3688\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.55\"," +
            "                \"p\": \"0.3635\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.56\"," +
            "                \"p\": \"0.3580\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.57\"," +
            "                \"p\": \"0.3523\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.58\"," +
            "                \"p\": \"0.3465\"" +
            "            }," +
            "            {" +
            "                \"u\": \"1.59\"," +
            "                \"p\": \"0.3405\"" +
            "            }" +
            "        ]," +
            "        \"touchLowVoltage\": \"50.0\"" +
            "    }" +
            "]";

    public static synchronized XMLParser getInstance() {
        if (instance == null) {
            instance = new XMLParser();
        }
        return instance;
    }

    public static double getValue(String tag, int id, double val) {
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            if (tag.equals("disconnectTimes")) {
                if (jsonObj.has(tag)) {
                    JSONArray js_arr = jsonObj.getJSONArray(tag);
                    for (int i = 0; i < js_arr.length(); i++) {
                        JSONObject obj = js_arr.getJSONObject(i);
                        int _id = Integer.parseInt(obj.getString("id"));
                        if (_id >= id) {
                            return Double.parseDouble(obj.getString("voltage"));
                        }
                    }
                }
            } else if (tag.equals("touchLowVoltage")) {
                return Double.parseDouble(jsonObj.getString("touchLowVoltage"));
            } else if (tag.equals("referenceDistances")) {
                if (jsonObj.has(tag)) {
                    JSONArray js_arr = jsonObj.getJSONArray(tag);
                    if (id == 0) {
                        return Double.parseDouble(js_arr.getJSONObject(0).getString("u"));
                    } else {
                        return Double.parseDouble(js_arr.getJSONObject(js_arr.length() - 1).getString("u"));
                    }
                }
            } else if (tag.equals("checkRefDistance")) {
                JSONArray js_arr = jsonObj.getJSONArray("referenceDistances");
                for (int i = 0; i < js_arr.length(); i++) {
                    JSONObject obj = js_arr.getJSONObject(i);
                    double u_value = Double.parseDouble(obj.getString("u"));
                    if (u_value == val) {
                        return Double.parseDouble(obj.getString("p"));
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
        return 0.0D;
    }

    public static List<String> getListValues(String tag, String field) {
        List<String> climbFactorList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            if (jsonObj.has(tag)) {
                JSONArray js_arr = jsonObj.getJSONArray(tag);
                for (int i = 0; i < js_arr.length(); i++) {
                    JSONObject obj = js_arr.getJSONObject(i);
                    climbFactorList.add(obj.getString(field));
                }
            }
        } catch (Exception ex) {

        }
        return climbFactorList;
    }

    public static int getDisconnectVoltage(String val, String tag, String type) {
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            if (tag.equals("disconnectTimes")) {
                if (jsonObj.has(tag)) {
                    JSONArray js_arr = jsonObj.getJSONArray(tag);
                    for (int i = 0; i < js_arr.length(); i++) {
                        JSONObject obj = js_arr.getJSONObject(i);
                        if (type.equals("ID")) {
                            double _id = Double.parseDouble(obj.getString("id"));
                            double idVal = Double.parseDouble(val);
                            if (_id == idVal) {
                                return Integer.parseInt(obj.getString("voltage"));
                            }
                        } else {
                            double seconds = Double.parseDouble(obj.getString("seconds"));
                            double secondVal = Double.parseDouble(val);
                            if (seconds == secondVal) {
                                return Integer.parseInt(obj.getString("voltage"));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            return 0;
        }
        return 0;
    }

    public static int getDisconnectName(String val, String tag, String type) {
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            if (tag.equals("disconnectTimes")) {
                if (jsonObj.has(tag)) {
                    JSONArray js_arr = jsonObj.getJSONArray(tag);
                    for (int i = 0; i < js_arr.length(); i++) {
                        JSONObject obj = js_arr.getJSONObject(i);
                        if (type.equals("ID")) {
                            double _id = Double.parseDouble(obj.getString("id"));
                            double id = Double.parseDouble(val);
                            if (_id == id) {
                                return Integer.parseInt(obj.getString("id"));
                            }
                        } else {
                            double seconds = Double.parseDouble(obj.getString("seconds"));
                            double secondVal = Double.parseDouble(val);
                            if (seconds == secondVal) {
                                return Integer.parseInt(obj.getString("id"));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            return 0;
        }
        return 0;
    }

    public static int getDisconnectID(double seconds, String tag) {
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            if (tag.equals("disconnectTimes")) {
                if (jsonObj.has(tag)) {
                    JSONArray js_arr = jsonObj.getJSONArray(tag);
                    for (int i = 0; i < js_arr.length(); i++) {
                        JSONObject obj = js_arr.getJSONObject(i);
                        double _seconds = Double.parseDouble(obj.getString("seconds"));
                        if (_seconds == seconds) {
                            return Integer.parseInt(obj.getString("id"));
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }
        return 0;
    }

    public static String getDisconnectSeconds(double val, String tag) {
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            if (tag.equals("disconnectTimes")) {
                if (jsonObj.has(tag)) {
                    JSONArray js_arr = jsonObj.getJSONArray(tag);
                    for (int i = 0; i < js_arr.length(); i++) {
                        JSONObject obj = js_arr.getJSONObject(i);

                            double _seconds = Double.parseDouble(obj.getString("id"));
                            double secondVal = Double.parseDouble(String.valueOf(val));
                            if (_seconds == secondVal) {
                                return obj.getString("seconds");
                            }

                    }
                }
            }
        } catch (Exception ex) {
            return "0";
        }
        return "0";
    }

    public static double getTouchLowVoltage() {
        double returnVal = 0;
        try {
            JSONArray jsonArray = new JSONArray(basedata);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            String val = jsonObj.getString("touchLowVoltage");
            returnVal = Double.parseDouble(val);
        } catch (Exception ex) {
            return returnVal;
        }
        return returnVal;
    }
}