package util;

import api.EsriConnector.Location;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class FileWriterUtil {

    private static final Logger LOG = LogManager.getLogger(FileWriterUtil.class.getName());

    public static String outputPath = "output/result_" + java.time.LocalDateTime.now();

    public static boolean writeResultsToFile(ArrayList<Location> listOfResults){
        LOG.info("Starting write address and coordinates to file...");
        String extension = FileReaderUtil.extension;
        switch (extension){
            case "csv":
                LOG.info("File output will be written to csv");
                writeToCsv(listOfResults);
                return true;
            case "xlsx":
                LOG.info("File output will be written to xlsx");
                writeToXlsx(listOfResults);
                return true;
            default:
                LOG.warn("Extension could not be determined, no output file will be written!!");
                return false;
        }
    }

    private static void writeToCsv(ArrayList<Location> listOfResults){
        LOG.info("Starting write output to csv...");
        File filename = new File(outputPath + ".csv");
        try(FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
            int rowCount = 0;
            for (Location line : listOfResults) {
                String stringLine = line.getAddress() + "," + line.getX() +
                        "," + line.getY() + "\n";
                bufferedWriter.write(stringLine);
                rowCount += 1;
            }
            LOG.info("Address list successfully written to csv with " + rowCount + "rows." );
        } catch (IOException e){
            LOG.error("Write output to csv failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void writeToXlsx(ArrayList<Location> listOfResults){
        LOG.info("Starting write output to xlsx...");
        File filename = new File(outputPath + ".xlsx");
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)){
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Address_Coordinates");
            int rowCount = 0;
            for (Location line : listOfResults) {
                Row row = sheet.iterator().next();
                row.iterator().next().setCellValue(line.getAddress());
                row.iterator().next().setCellValue(line.getX());
                row.iterator().next().setCellValue(line.getY());
                rowCount +=1;
            }
            workbook.write(fileOutputStream);
            LOG.info("Address list successfully written to xlsx with " + rowCount + "rows." );
        } catch (IOException e) {
            LOG.error("Write output to xlsx failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
