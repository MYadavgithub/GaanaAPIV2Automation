package recomendation.reco_tracks;
import java.util.Map;
import utils.WriteCsv;
import config.BaseUrls;
import utils.CsvReader;
import config.Constants;
import config.Endpoints;
import org.slf4j.Logger;
import java.util.HashMap;
import org.testng.Assert;
import java.util.Iterator;
import common.FileActions;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.annotations.Optional;
import io.restassured.response.Response;
import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeClass;

public class Vibes extends BaseUrls {

    String id = "1";
    String baseurl = "";
    int DEVICE_ID_COUNT = 1;
    static int RES_OBJ = 10;
    String API_NAME = "Vibes";
    final static int CASE_COUNT = 5;
    RequestHandler rq = new RequestHandler();
    ArrayList<String> device_ids = null;
    Map<Integer, Response> responses = new HashMap<>();
    Map<Integer, String[]> all_entitys_ids = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(Vibes.class);

    @BeforeClass
    public void prepareRequestData() {
        // System.setProperty("env", "local");
        // System.setProperty("type", "Reco");
        // System.setProperty("device_type", "android");
        baseurl = "http://172.26.60.171:7080";
        // baseurl = "http://172.26.60.171:7079";
        device_ids = CsvReader.readCsv("./src/test/resources/data/deviceid.csv");
    }

    @Test(priority = 1, invocationCount = CASE_COUNT)
    @Parameters("id")
    public void validateStatusAndDataType(@Optional("1") String id) {
        baseurl();
        String url = baseurl + Endpoints.vibes;
        prop.setProperty("deviceId", device_ids.get(DEVICE_ID_COUNT));
        Response response = rq.createGetRequest(prop, url);
        Assert.assertEquals(response != null, true, "Response time or code is not valid!");
        responses.put(DEVICE_ID_COUNT, response);
        deviceIdCounter();
    }

    @Test(priority = 2, invocationCount = CASE_COUNT)
    @Parameters ({"id"})
    public void getEntityIds(@Optional("1") String id) {
        if(DEVICE_ID_COUNT == 1) {
            Assert.assertEquals(!responses.isEmpty(), true, "Response is empty can't process further!");
            Assert.assertEquals(responses.size() == CASE_COUNT, true,"Responses list is not equal to number of cases.");
        }

        JSONObject response_object = new JSONObject(responses.get(DEVICE_ID_COUNT).asString());
        int res_obj_count = Integer.parseInt(response_object.getString("count").trim());
        Assert.assertEquals((res_obj_count == RES_OBJ), true, "Response Object must be 10!");

        String[] unique_ids = new String[RES_OBJ];
        JSONArray entities = response_object.getJSONArray("entities");
        Iterator<Object> itr = entities.iterator();
        
        int count = 0;
        while(itr.hasNext()){
            JSONObject entity = (JSONObject) itr.next();
            String entity_id = entity.getString("entity_id").trim();
            String entity_type = entity.optString("entity_type").trim().toString();
            String created_id = entity_id+"_"+entity_type;
            unique_ids[count] = created_id;
            count++;
        }

        all_entitys_ids.put(DEVICE_ID_COUNT, unique_ids);
        deviceIdCounter();
    }

    @Test(priority = 3)
    @Parameters ({"id"})
    public void processCsvWrite(@Optional("1") String id) {
        if(!all_entitys_ids.isEmpty() && id.equals("0")){
            String file_name = API_NAME+".csv";
            String head[] = { "Entities" };
            WriteCsv.writeCsvWithHeader(file_name, head, all_entitys_ids);
        }else if(id.equals("1")){
            Map<Integer, ArrayList<String>> previous_entities = readPrevData();
            Assert.assertEquals((previous_entities.size() == all_entitys_ids.size()), true, "Previous and new response size should be same!");
            boolean result = validatedata(previous_entities, all_entitys_ids);
            Assert.assertEquals(result, true, "Previous and new response matching!");
        }
    }

    private boolean validatedata(Map<Integer, ArrayList<String>> previous_entities, Map<Integer, String[]> new_entities) {
        boolean result = false;
        for(int i = 1; i<=previous_entities.size(); i++){
            ArrayList<String> pre_response_entities = previous_entities.get(i);
            String[] new_response_entities = new_entities.get(i);
            log.info(pre_response_entities.toString());
            for(String val : new_response_entities){
                if(!pre_response_entities.contains(val)){
                    log.info(val);
                    result = true;
                }else{
                    result = false;
                    log.error(val+" is present in previous response as well as new response for device id : "+device_ids.get(i));
                    break;
                }
            }
        }
        return result;
    }

    private Map<Integer, ArrayList<String>> readPrevData() {
        String path = "."+Constants.CUSTOM_REPORT_FOLDER+"/Runtime/";
        String file_name = API_NAME+".csv";
        boolean isFilePresent = FileActions.fileOperation(1, path, file_name);
        if(isFilePresent)
            return CsvReader.readCsvLineWise(path + file_name);
        return null;
    }

    private void deviceIdCounter() {
        if(DEVICE_ID_COUNT == CASE_COUNT){
            DEVICE_ID_COUNT = 1;
        }else{
            DEVICE_ID_COUNT++;
        }
    }
}