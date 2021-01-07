package utils;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JosnReader {

    public static JSONObject ReadJSONFile(String filename) {
        String file_path = "./src/test/resources/data/jsonfiles/"+filename;

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(file_path));
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}