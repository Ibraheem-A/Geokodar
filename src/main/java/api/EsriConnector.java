package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.*;

public class EsriConnector {

    private static String[][] UMLAUT_REPLACEMENTS = { { "Ä", "Ae" }, { "Ü", "Ue" }, { "Ö", "Oe" }, { "ä", "ae" }, { "ü", "ue" }, { "ö", "oe" }, { "ß", "ss" } };

    private static final Logger LOG = LogManager.getLogger(EsriConnector.class.getName());

    public static Location getCoordinatesFromServer(String address){
        String url = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/" +
                "findAddressCandidates?singleLine="+ address + "&forStorage=false&outSR=5684&f=pjson";
        url = url.replace(" ", "%20");
        url = replaceUmlaut(url);

        String json = downloadJsonFromServer(url);
        double[] coord = getCoordinatesFromJson(json);

        return new Location(address, coord[0], coord[1]);
    }

    private static String downloadJsonFromServer(String... strings){
        LOG.info("Starting json download from server...");
        StringBuilder json = new StringBuilder();
        URL url;
        HttpURLConnection httpURLConnection;
        try{
            url = new URL(strings[0]);
            LOG.info("URL: " + url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while (data != -1){
                char current = (char)data;
                json.append(current);
                data = reader.read();
            }
            LOG.info("JSON download completed successfully with: " + json.toString().toCharArray().length + " characters");
            return json.toString();
        } catch (IOException e){
            LOG.error("JSON download failed: " + e.getMessage());
            e.printStackTrace();
            return "Failed";
        }
    }
    
    private static double[] getCoordinatesFromJson(String json){
        double x, y;
        try {
            LOG.info("Starting parse JSON to retrieve coordinates...");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray candidateArray = jsonObject.getJSONArray("candidates");
            JSONObject partObject = candidateArray.getJSONObject(0);
            JSONObject locationObject = partObject.getJSONObject("location");

            x = locationObject.getDouble("x");
            y = locationObject.getDouble("y");
            LOG.info("Coordinates retrieved :- x: " + x + ", y: " + y);

            return new double[] {x, y};
        } catch (JSONException e){
            LOG.error("parse JSON failed " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        LOG.warn("Empty coordinates list will be returned!!!");
        return new double[2];
    }

    private static String replaceUmlaut(String input) {

        //replace all lower Umlauts
        String output = input.replace("ü", "ue")
                .replace("ö", "oe")
                .replace("ä", "ae")
                .replace("ß", "ss");

        //first replace all capital umlaute in a non-capitalized context (e.g. Übung)
        output = output.replace("Ü(?=[a-zäöüß ])", "Ue")
                .replace("Ö(?=[a-zäöüß ])", "Oe")
                .replace("Ä(?=[a-zäöüß ])", "Ae");

        //now replace all the other capital umlaute
        output = output.replace("Ü", "UE")
                .replace("Ö", "OE")
                .replace("Ä", "AE");

        return output;
    }

    public static class Location{
        private String address;
        private double x;
        private double y;

        public Location(String address, double x, double y){
            this.address = address;
            this.x = x;
            this.y = y;
        }

        public String getAddress() {
            return address;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
