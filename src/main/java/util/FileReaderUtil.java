package util;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class FileReaderUtil {
    static String extension;

    private static final Logger LOG = LogManager.getLogger(FileReaderUtil.class.getName());

    public static ArrayList<String> getAddressesFromFile(String path, int sheetIndex, int addressColumnIndex, int numberOfHeaderRows){
        LOG.info("Starting read address from file...");
        extension = FilenameUtils.getExtension(path);
        switch (extension){
            case "csv":
                LOG.info("File is csv");
                return getAddressesFromCsv(path, addressColumnIndex, numberOfHeaderRows);
            case "xlsx":
                LOG.info("File is xlsx");
                return getAddressFromXlsx(path, sheetIndex, addressColumnIndex, numberOfHeaderRows);
            default:
                LOG.warn("File extension not determined, empty address list will be returned!!!");
                return new ArrayList<>();
        }
    }

    private static ArrayList<String> getAddressesFromCsv(String path, int addressColumnIndex, int numberOfHeaderRows){
        LOG.info("Starting get address from csv...");
        ArrayList<String> addressList= new ArrayList<>();
        try (FileReader fileReader = new FileReader(new File(path));
             BufferedReader csvReader = new BufferedReader(fileReader)){
            String row;
            while (!(row = csvReader.readLine()).equals(null)){
                addressList.add(row.split(",")[addressColumnIndex]);
            }
            LOG.info("Address list successfully populated from csv with " + addressList.size() + " addresses");
            LOG.info(addressList.get(0) + "\n" + addressList.get(1) + "\n" + addressList.get(3) + "\n" +  "..." + "\n" +  "...");
            return addressList;
        } catch (IOException e) {
            LOG.error("get address from csv failed!!!  Reason: " + e.getMessage());
            LOG.warn("Empty address list will be returned!!!");
            return new ArrayList<>();
        }
    }

    private static ArrayList<String> getAddressFromXlsx(String path, int sheetIndex, int addressColumnIndex, int numberOfHeaderRows){
        LOG.info("Starting get address from xlsx...");
        ArrayList<String> addressList= new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(new File(path));
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)){
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);

            LOG.info("Located workbook with sheet name: " + sheet.getSheetName());

            Iterator<Row> rowIterator = sheet.iterator();
            Row currentRow;
            while (rowIterator.hasNext() && !((currentRow = rowIterator.next()).toString().isEmpty())){
                Cell addressCell = currentRow.getCell(addressColumnIndex);
                String address = addressCell.getStringCellValue();
                LOG.info(address);
                addressList.add(address);
            }
            if(numberOfHeaderRows > 0){
                addressList.subList(0, numberOfHeaderRows).clear();
                addressList.removeIf(s -> s.trim().isEmpty());
            }
            LOG.info("Address list successfully populated from xlsx with " + addressList.size() + " addresses");
            LOG.info(addressList.get(0) + "\n" + addressList.get(1) + "\n" + addressList.get(2) + "\n" +  "..." + "\n" +  "...");
            return addressList;

        } catch (IOException e) {
            LOG.error("get address from xlsx failed!!!  Reason: " + e.getMessage());
            LOG.warn("Empty address list will be returned!!!");
            return new ArrayList<>();
        }
    }
}
