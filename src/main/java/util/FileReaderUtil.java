package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class FileReaderUtil {


    public ArrayList<String> getAddressesFromCsv(String path, int addressColumnIndex){
        ArrayList<String> addressColumn= new ArrayList<>();
        try (FileReader fileReader = new FileReader(new File(path));
             BufferedReader csvReader = new BufferedReader(fileReader)){
            String row;
            while (!(row = csvReader.readLine()).equals(null)){
                addressColumn.add(row.split(",")[addressColumnIndex]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressColumn;
    }

    public ArrayList<String> getAddressFromXlsx(String path, String sheetName, int addressColumnIndex){
        ArrayList<String> addressColumn= new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(path));
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)){
            XSSFSheet sheet = workbook.getSheet(sheetName);
            while (sheet.iterator().hasNext()){
                Row currentRow = sheet.iterator().next();
                Cell addressCell = currentRow.getCell(addressColumnIndex);
                String address = addressCell.getStringCellValue();
                addressColumn.add(address);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressColumn;
    }
}
