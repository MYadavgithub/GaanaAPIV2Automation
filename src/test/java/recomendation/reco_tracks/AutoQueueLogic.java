package recomendation.reco_tracks;
import config.BaseUrls;
import config.Endpoints;
import io.restassured.response.Response;
import utils.JosnReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import  org.json.*;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.Headers;
import common.RequestHandler;

// https://timesgroup.jira.com/browse/GAANA-41140

public class AutoQueueLogic extends BaseUrls {

    static int SEED_ID = 11;
    int call_count = 0;
    Response response;
    static final int AUTO_LOGIC_INVOCATION_COUNT = 4;
    String TRACK_POST_URL = null;

    RequestHandler request = new RequestHandler();
    private static Logger log = LoggerFactory.getLogger(AutoQueueLogic.class);

    @BeforeClass
    public void prepareEnv() {
        System.setProperty("env", "local");
        System.setProperty("type", "reco");
        System.setProperty("device_type", "android");
        baseurl();
        String baseurl = prop.getProperty("reco_baseurl").toString().trim();
        TRACK_POST_URL = baseurl + Endpoints.recoTrackPost + SEED_ID;
    }

    @Test(priority = 1, dataProvider = "device_id", invocationCount = AUTO_LOGIC_INVOCATION_COUNT)
    public void createPostRequestOnTrackPost(String device_id) {
        Map<String, String> headers = Headers.getHeaders(0);
        headers.replace("deviceId", device_id);
        JSONObject post_data = JosnReader.ReadJSONFile("autoqueue.json");
        getTrackIdBasedOnExperiment(post_data);
        response = request.postRequest(TRACK_POST_URL, headers, post_data);
        GlobalConfigHandler handler = new GlobalConfigHandler();
        call_count = handler.invocationCounter(call_count, AUTO_LOGIC_INVOCATION_COUNT);
    }

    private void getTrackIdBasedOnExperiment(JSONObject post_data) {
        Gson gson = new Gson();
        String data = gson.toJson(post_data.get("last_played_data"));
        JSONArray posted_data = new JSONArray(data);
        System.out.println(posted_data.length());
        if(call_count == 0){
            Map<Double, String> track_ids = new HashMap<>();
            log.info(" Picking tracks for : Last 5 songs played (duration>15secs) in the last 20 songs (E4v3)");
            for(int i = 0; i < posted_data.length(); i++){
                org.json.JSONObject obj = posted_data.getJSONObject(i);
                double play_time = Double.parseDouble(obj.optString("played_duration").toString().trim());
                String track_id = obj.getString("track_id").trim();
                System.out.println(play_time + " "+ track_id);
                track_ids.put(play_time, track_id);
            }

            if(track_ids.size() > 0){
                System.out.println(track_ids.keySet());
            }
        }
        
    }

    private List<String> experimentWiseDeviceList() {
        // last index is default logic device id
        String arr [] = {"GM1901_d8f4420a8ba7849e75", "GM1901_d8f4420a8ba7849e76", "GM1901_d8f4420a8ba7849e96", "GM1901_d8f4420a8ba7849e80"};
        return Arrays.asList(arr);
    }

    @DataProvider(name = "device_id")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                experimentWiseDeviceList().get(call_count)
            }
        };
    }
}