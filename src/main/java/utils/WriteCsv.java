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

    public void writeCsv(String filename, Map<Integer, JSONObject> data) {
        String file_path = Constants.WRITE_TD_CSV_FROM;
        deleteExisting(file_path, filename);
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

    private static void deleteExisting(String filepath, String filename) {
        FileActions.fileOperation(0, filepath, filename);
    }

    /**
     * @param file_name
     * @param head
     * @param values
     * @param isOverride send true for writing line by line else false
     */
    public static void writeCsvWithHeader(String file_name, String[] head, Map<Integer, String[]> values, boolean isOverride) {
        String path = "."+Constants.CUSTOM_REPORT_FOLDER+"/Runtime/";
        if(head != null){
            deleteExisting(path, file_name);
            FileActions.checkFolderExists("."+Constants.CUSTOM_REPORT_FOLDER+"/Runtime/");
        }

        CSVWriter writer;
        try {
            String file = path+file_name;;
            writer = new CSVWriter(new FileWriter(file, isOverride));
            writer.writeNext(head);

            for(Entry<Integer, String[]> val : values.entrySet()){
                String[] write = val.getValue();
                writer.writeNext(write);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}