import api.EsriConnector;
import api.EsriConnector.Location;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.FileReaderUtil;
import util.FileWriterUtil;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class GeokodarApplication {

    private static final Logger LOG = LogManager.getLogger(GeokodarApplication.class.getName());

    public static void main(String[] args) {
        LOG.info("--------------STARTING GEOKODAR--------------");
        LocalDateTime startTime = java.time.LocalDateTime.now();

        String path = "/C:/Users/adeni/OneDrive/Desktop/Amazon_Fullfillment-Zentren.xlsx";
        path = path.replace("/", File.separator);

        LOG.info("Loading input file from " + path);

        ArrayList<Location> locationsList = new ArrayList<>();
        ArrayList<String> addressesList = FileReaderUtil.getAddressesFromFile(path, 1, 2, 1);

        assert !addressesList.isEmpty();

        for (String address : addressesList) {
            Location location = EsriConnector.getCoordinatesFromServer(address);
            locationsList.add(location);
        }

        if(FileWriterUtil.writeResultsToFile(locationsList)){
            LOG.info("File output written as " + FilenameUtils.getBaseName(FileWriterUtil.outputPath) + "successfully to: " + FileWriterUtil.outputPath);
        }

        LocalDateTime finishTime = java.time.LocalDateTime.now();
        LOG.info("Application finished. Total Duration: " + Duration.between(startTime, finishTime).getSeconds() + "seconds");

    }
}