package utils;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvReader {

    private static Logger log = LoggerFactory.getLogger(CsvReader.class);

    public static ArrayList<String> readCsv(String file) {
        ArrayList<String> data_list = new ArrayList<>();
        try {
            CSVReader read = new CSVReader(new FileReader(file));
            List<String[]> allRows;
            try {
                allRows = read.readAll();
                if(allRows.size() > 0){
                    for (String[] row : allRows) {
                        if(row.length > 1){
                            data_list.add(row[1]);
                        }else{
                            data_list.add(Arrays.toString(row).toString().replaceAll("[\\[\\]\\(\\)]", "")); // full line once
                        }
                    }
                }else{
                    log.error(file+ "have no data found!");
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info(file+ " not found!");
        }
        return data_list;
    }
}
