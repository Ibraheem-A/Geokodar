package util;

import api.EsriConnector.Location;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class FileWriterUtil {

    String outputPath = "output/result_" + java.time.LocalDateTime.now();

    public boolean writeResultsToFile(ArrayList<Location> listOfResults, String extension){
        switch (extension){
            case "csv":
                writeToCsv(listOfResults);
                return true;
            case "xlsx":
                writeToXlsx(listOfResults);
                return true;
            default:
                return false;
        }
    }

    private void writeToCsv(ArrayList<Location> listOfResults){
        File filename = new File(outputPath + ".csv");
        try(FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
            for (Location line : listOfResults) {
                String stringLine = line.getAddress() + "," + line.getX() +
                        "," + line.getY() + "\n";
                bufferedWriter.write(stringLine);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void writeToXlsx(ArrayList<Location> listOfResults){
        File filename = new File(outputPath + ".xlsx");
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)){
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Address_Coordinates");
            for (Location line : listOfResults) {
                Row row = sheet.iterator().next();
                row.iterator().next().setCellValue(line.getAddress());
                row.iterator().next().setCellValue(line.getX());
                row.iterator().next().setCellValue(line.getY());
            }
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
