import api.EsriConnector;
import api.EsriConnector.Location;
import util.FileReaderUtil;
import util.FileWriterUtil;

import java.io.File;
import java.util.ArrayList;

public class GeokodarApplication {
    public static void main(String[] args) {
        String path = "/C:/Users/adeni/OneDrive/Desktop/Amazon_Fullfillment-Zentren.xlsx";
        path = path.replace("/", File.separator);
        ArrayList<Location> locationsList = new ArrayList<>();
        ArrayList<String> addressesList = FileReaderUtil.getAddressesFromFile(path, 1, 2);
        assert !addressesList.isEmpty();
        for (String address : addressesList) {
            Location location = EsriConnector.getCoordinatesFromServer(address);
            locationsList.add(location);
        }
        FileWriterUtil.writeResultsToFile(locationsList, "xlsx");
    }
}