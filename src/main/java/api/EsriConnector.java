package api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.*;

public class EsriConnector {

    public static Location getCoordinatesFromServer(String address){
        String url = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/" +
                "findAddressCandidates?singleLine="+ address + "&forStorage=false&outSR=5684&f=pjson";
        String json = downloadJsonFromServer(url);
        double[] coord = getCoordinatesFromJson(json);

        return new Location(address, coord[0], coord[1]);
    }

    private static String downloadJsonFromServer(String... strings){
        StringBuilder json = new StringBuilder();
        URL url;
        HttpURLConnection httpURLConnection;
        try{
            url = new URL(strings[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while (data != -1){
                char current = (char)data;
                json.append(current);
                data = reader.read();
            }
            return json.toString();
        } catch (IOException e){
            e.printStackTrace();
            return "Failed";
        }
    }
    
    private static double[] getCoordinatesFromJson(String json){
        double x, y;

        JSONObject jsonObject = new JSONObject(json);
        String candidates = jsonObject.getString("candidates");
        JSONArray candidateArray = new JSONArray(candidates);
        JSONObject partObject = candidateArray.getJSONObject(0);
        String location = partObject.getString("location");
        JSONObject locationObject = new JSONObject(location);

        x = locationObject.getDouble("x");
        y = locationObject.getDouble("y");

        return new double[] {x, y};
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
