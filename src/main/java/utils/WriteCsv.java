package utils;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import com.opencsv.CSVWriter;
import org.json.JSONObject;
import common.FileActions;
import config.Constants;

public class WriteCsv {
    String folder_name = "savedResponse";
    FileActions fa = new FileActions();
    CsvReader cr = new CsvReader();
    String file_path = Constants.WRITE_TD_CSV_FROM;

    public void writeCsv(String filename, Map<Integer, JSONObject> data) {
        deleteExixting(filename);
        CSVWriter writer;
        try {
            String file = file_path + filename;
            FileActions.checkFolderExists(file_path);
            writer = new CSVWriter(new FileWriter(file));

            for(Entry<Integer, JSONObject> entry : data.entrySet()) {
                // StringBuilder builder = new StringBuilder();
                String key = String.valueOf(entry.getKey());
                JSONObject value = entry.getValue();
                if(key != null && value != null){
                    /*builder.append(key);
                    builder.append(',');
                    builder.append(value);
                    builder.append(System.getProperty("line.separator"));
                    String[] record = builder.toString().split(","); */
                    String[] record = {key, value.toString()};
                    writer.writeNext(record);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteExixting(String filename) {
        FileActions.fileOperation(0, file_path, filename);
    }
}
