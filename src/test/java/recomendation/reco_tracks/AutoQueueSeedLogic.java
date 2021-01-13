package recomendation.reco_tracks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import common.Headers;
import config.BaseUrls;
import config.Constants;
import utils.JosnReader;
import config.Endpoints;
import org.slf4j.Logger;
import org.json.JSONArray;
import com.google.gson.Gson;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import common.GlobalConfigHandler;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import test_data.AutoQueueLogic;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import db_queries.AutoQueueNewLogicQuery;

// https://timesgroup.jira.com/browse/GAANA-41140

public class AutoQueueSeedLogic extends BaseUrls {

    Response response;
    int api_call = 0;
    String BASEURL = null;
    String TRACK_POST_URL = null;
    ArrayList<String> TRACK_IDS = null;
    Map<String, String> DEVICE_LISTS = null;
    org.json.JSONObject COMPLETE_RESPONSES = new org.json.JSONObject();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    AutoQueueLogic logicController = new AutoQueueLogic();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(AutoQueueSeedLogic.class);

    @BeforeClass
    public void prepareEnv() {
        System.setProperty("env", "prod");
        System.setProperty("type", "reco");
        System.setProperty("device_type", "android");
        baseurl();
        BASEURL = prop.getProperty("reco_baseurl").toString().trim();
        TRACK_POST_URL = BASEURL + Endpoints.recoTrackPost + Constants.SEED_ID;
    }

    @Test(priority = 1, dataProvider = "device_id", invocationCount = Constants.AUTO_LOGIC_INVOCATION_COUNT)
    public void createPostRequestOnTrackPost(String device_id) {
        Map<String, String> headers = Headers.getHeaders(0);
        headers.replace("deviceId", device_id);
        JSONObject post_data = JosnReader.ReadJSONFile("autoqueue.json");
        TRACK_IDS = getTrackIdBasedOnExperiment(post_data);
        log.info("msg==>"+TRACK_POST_URL);
        response = request.postRequest(TRACK_POST_URL, headers, post_data);
        RESPONSES.put(api_call, response);
        api_call = handler.invocationCounter(api_call, Constants.AUTO_LOGIC_INVOCATION_COUNT);
    }

    @Test(priority = 2, dataProvider = "device_id", invocationCount = Constants.AUTO_LOGIC_INVOCATION_COUNT)
    public void createRecommendedTrackCall(String device_id){
        // String baseurl = "http://rec.gaana.com";
        int count = 0;
        JSONArray complete_call_responses = new JSONArray();
        for(String track : TRACK_IDS){
            Response response = request.createGetRequest(BASEURL+ Endpoints.recoTracks+track);
            org.json.JSONObject response_object = new org.json.JSONObject(response.asString());
            JSONArray track_obj = response_object.getJSONArray("tracks");
            if(count == 0){
                complete_call_responses = track_obj;
            }else{
                for(int i = 0; i<track_obj.length(); i++){
                    complete_call_responses.put(track_obj.getJSONObject(i));
                }
            }
            count++;
        }
        COMPLETE_RESPONSES.put(device_id, complete_call_responses);
        api_call = handler.invocationCounter(api_call, Constants.AUTO_LOGIC_INVOCATION_COUNT);
    }

    @Test(priority = 3, dataProvider = "device_id", invocationCount = Constants.AUTO_LOGIC_INVOCATION_COUNT)
    public void filterZeroMatchingTagsAndEra(String device_id) {
        org.json.JSONObject realeaseYearEra = new org.json.JSONObject();
        AutoQueueNewLogicQuery dbQuery = new AutoQueueNewLogicQuery();
        String initial_query_params = TRACK_IDS.toString().replaceAll("[\\[\\]\\(\\)]", " ").trim();
        String recomendedListTrackIds = logicController.getAllTrackIds(device_id, COMPLETE_RESPONSES);
        JSONArray fiveSongsTagsReleaseYear = dbQuery.getReleaseYearAndTags(initial_query_params);
        JSONArray allTracksTagsReleaseYear = dbQuery.getReleaseYearAndTags(recomendedListTrackIds);

        if(fiveSongsTagsReleaseYear == null || allTracksTagsReleaseYear == null){
            realeaseYearEra =  logicController.getSavedData();
        }else{
            realeaseYearEra.put("five_seeds", fiveSongsTagsReleaseYear);
            realeaseYearEra.put("all_seeds", allTracksTagsReleaseYear);
        }

        ArrayList<String> filterOne_track_ids = logicController.filterZeroMatching(realeaseYearEra);
        JSONArray filterOneData = logicController.removeTracks(device_id, COMPLETE_RESPONSES, filterOne_track_ids);
        COMPLETE_RESPONSES.remove(device_id);
        COMPLETE_RESPONSES.put(device_id, filterOneData);
        api_call = handler.invocationCounter(api_call, Constants.AUTO_LOGIC_INVOCATION_COUNT);
    }

    @Test(priority = 4, dataProvider = "device_id", invocationCount = Constants.AUTO_LOGIC_INVOCATION_COUNT)
    public void newLogicApiResponse(String device_id){
        org.json.JSONObject newLogicResponse = new org.json.JSONObject(RESPONSES.get(api_call).asString());
        String ids_post_response = logicController.getAllTrackIds(null, newLogicResponse);
        String ids_after_filter_One_response = logicController.getAllTrackIds(device_id, COMPLETE_RESPONSES);

        System.out.println("\n\n "+ids_post_response.trim()+"\n\n"+ids_after_filter_One_response);
        api_call = handler.invocationCounter(api_call, Constants.AUTO_LOGIC_INVOCATION_COUNT);
    }

    private ArrayList<String> getTrackIdBasedOnExperiment(JSONObject post_data) {
        Gson gson = new Gson();
        String data = gson.toJson(post_data.get("last_played_data"));
        JSONArray posted_data = new JSONArray(data);
        String experiment_type = Constants.experiments()[api_call];
        
        if(api_call == 0 && experiment_type.contains("1") && posted_data.length() > 0){
            log.info(" Picking tracks for : Last 5 songs played (duration>15secs) in the last 20 songs (E4v3)");
            return logicController.getExperimentOneSongs(posted_data);
        }
        return null;
    }

    @DataProvider(name = "device_id")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                Constants.testDevicesAutoQueue().get(api_call)
            }
        };
    }
}