package util;

import api.EsriConnector.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileWriterUtil {

    private static final Logger LOG = LogManager.getLogger(FileWriterUtil.class.getName());

    static String localDateTime = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'_'HH;mm;ss"));
    public static String outputPath = "output" + File.separator + "result_" + localDateTime;

    static String directoryName = "output" + File.separator;
    static File directory = new File(directoryName);

    public static boolean writeResultsToFile(ArrayList<Location> listOfResults){
        if(!directory.exists()){
            directory.mkdirs();
        }

        LOG.info("Starting write address and coordinates to file...");
        String extension = FileReaderUtil.extension;
        switch (extension){
            case "csv":
                LOG.info("File output will be written to csv");
                return writeToCsv(listOfResults);
            case "xlsx":
                LOG.info("File output will be written to xlsx");
                return writeToXlsx(listOfResults);
            default:
                LOG.warn("Extension could not be determined, no output file will be written!!");
                return false;
        }
    }

    private static boolean writeToCsv(ArrayList<Location> listOfResults){
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
            return true;
        } catch (IOException e){
            LOG.error("Write output to csv failed!!!  Reason: " + e.getMessage());
            return false;
        }
    }

    private static boolean writeToXlsx(ArrayList<Location> listOfResults){
        LOG.info("Starting write output to xlsx...");
        File filename = new File(outputPath + ".xlsx");
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)){
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Address_Coordinates");
            int rowCount = 0;
            for (Location line : listOfResults) {
                Row row = sheet.createRow(rowCount);

                Cell cell = row.createCell(1);
                cell.setCellValue(line.getAddress());

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(line.getX());

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(line.getY());
                rowCount +=1;
            }
            workbook.write(fileOutputStream);
            LOG.info("Address list successfully written to xlsx with " + rowCount + "rows." );
            return true;
        } catch (IOException e) {
            LOG.error("Write output to xlsx failed!!!  Reason: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
