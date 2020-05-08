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

public class FileReaderUtil {
    static String extension;

    private static final Logger LOG = LogManager.getLogger(FileReaderUtil.class.getName());

    public static ArrayList<String> getAddressesFromFile(String path, int sheetIndex, int addressColumnIndex){
        LOG.info("Starting read address from file...");
        extension = FilenameUtils.getExtension(path);
        switch (extension){
            case "csv":
                LOG.info("File is csv");
                return getAddressesFromCsv(path, addressColumnIndex);
            case "xlsx":
                LOG.info("File is xlsx");
                return getAddressFromXlsx(path, sheetIndex, addressColumnIndex);
            default:
                LOG.warn("File extension not determined, empty address list will be returned!!!");
                return new ArrayList<>();
        }
    }

    private static ArrayList<String> getAddressesFromCsv(String path, int addressColumnIndex){
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
            LOG.error("get address from csv failed: " + e.getMessage());
            e.printStackTrace();
            LOG.warn("Empty address list will be returned!!!");
            return new ArrayList<>();
        }
    }

    private static ArrayList<String> getAddressFromXlsx(String path, int sheetIndex, int addressColumnIndex){
        LOG.info("Starting get address from xlsx...");
        ArrayList<String> addressList= new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(new File(path));
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)){
            XSSFSheet sheet = workbook.getSheetAt(sheetIndex);

            LOG.info("Located workbook with sheet name: " + sheet.getSheetName());

            while (sheet.iterator().hasNext()){
                Cell addressCell = sheet.iterator().next().getCell(addressColumnIndex);
                String address = addressCell.getStringCellValue();
                LOG.info(address);
                addressList.add(address);
            }
            LOG.info("Address list successfully populated from xlsx with " + addressList.size() + " addresses");
            LOG.info(addressList.get(0) + "\n" + addressList.get(1) + "\n" + addressList.get(3) + "\n" +  "..." + "\n" +  "...");
            return addressList;

        } catch (IOException e) {
            LOG.error("get address from xlsx failed: " + e.getMessage());
            e.printStackTrace();
            LOG.warn("Empty address list will be returned!!!");
            return new ArrayList<>();
        }
    }
}
