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

    public static ArrayList<String> readCsv(String filename) {
        ArrayList<String> data_list = new ArrayList<>();
        String filepath = System.getProperty("user.dir")+"/src/test/resources/data/"+filename;
        try {
            CSVReader read = new CSVReader(new FileReader(filepath));
            List<String[]> allRows;
            try {
                allRows = read.readAll();
                for (String[] row : allRows) {
                    data_list.add(Arrays.toString(row).toString().replaceAll("[\\[\\]\\(\\)]", ""));
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log.info(data_list.toString());
        return data_list;
    }
}
