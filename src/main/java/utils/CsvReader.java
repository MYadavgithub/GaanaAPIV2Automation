package utils;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import org.slf4j.Logger;
import java.util.HashMap;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import org.slf4j.LoggerFactory;
import com.opencsv.CSVReaderBuilder;
import java.io.FileNotFoundException;
import com.opencsv.exceptions.CsvException;

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

    public static Map<Integer, ArrayList<String>> readCsvLineWise(String file) {
        int row_count = 1;
        Map<Integer, ArrayList<String>> csv_data = new HashMap<>();
        try {
            FileReader reader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();

            for(String[] row : allData) {
                int count = 0;
                ArrayList<String> col_data = new ArrayList<>();
                for (String cell : row) {
                    String value = cell;
                    col_data.add(value);
                    count++;
                }
                if(count == row.length){
                    csv_data.put(row_count, col_data);
                    row_count++;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return csv_data;
    }
}
