package recomendation.reco_tracks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import config.BaseUrls;
import config.Endpoints;
import utils.CsvReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.Helper;
import common.RequestHandler;
import io.restassured.response.Response;
import test_data.RecomendedTrackTd;
import com.github.cliftonlabs.json_simple.JsonObject;

public class RecomendedTracks extends BaseUrls {

    String URL = "";
    int api_hit_count = 0;
    String API_NAME = "Recomended Track";
    int RECOMENDED_TRACK_COUNT = 30;
    ArrayList<String> url_list = null;
    JsonObject common_data = new JsonObject();
    Helper helper = new Helper();
    RequestHandler req = new RequestHandler();
    private static Logger log = LoggerFactory.getLogger(RecomendedTracks.class);

    public static String prepareUrl(String baseurl, String track_id) {
        String end_points = Endpoints.recoTracks + track_id;
        return baseurl + end_points;
    }

    @BeforeClass
    public void generateAllRecoUrls(ITestContext context) {
        url_list = new ArrayList<>();
        String baseurl = BaseUrls.baseurl();
        ArrayList<String> input_values = CsvReader.readCsv(prop.getProperty("tracks_td"));
        for (String val : input_values) {
            String url = prepareUrl(baseurl, val);
            url_list.add(url);
        }

        int loop_count = url_list.size();
        if (url_list == null)
            log.error(API_NAME + " urls creation broken.");

        provideApiHitCount(context, loop_count); // to get loop count based on url hit
    }

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] { 
            { 
                url_list.get(api_hit_count) 
            }
        };
    }

    @Test(priority = 1, dataProvider = "urlProvider")
    public void callRecoTrack(String url) {
        URL = url;
        Response response = req.createGetRequestCall(prop, url);
        if (response != null) {
            JSONObject response_object = new JSONObject(response.asString());
            String response_count = response_object.optString("count").toString();
            boolean response_status = response_object.getBoolean("status");
            common_data.put("count", response_count);
            common_data.put("status", response_status);
            validateCommonResponseData();
            JSONArray track_list = response_object.getJSONArray("tracks");
            boolean result = validateTrackList(track_list);
            if(result){
                log.info("Validated for : "+URL);
            }
        }
        api_hit_count++;
    }

    // @Test(priority = 2, dependsOnMethods = {"callRecoTrack"}, alwaysRun = true)
    private void validateCommonResponseData() {
        boolean isJsonObjectCountValidated = Integer.parseInt(common_data.get("count").toString().trim()) <= RECOMENDED_TRACK_COUNT;
        boolean isStatusOfResponseValidated = common_data.get("status").toString().equals("true");
        if (!isJsonObjectCountValidated && !isStatusOfResponseValidated) {
            log.error(API_NAME + " count found : " + common_data.get("count").toString());
            Assert.assertEquals(isStatusOfResponseValidated, true, "Status and count key validation failed!");
        } else {
            log.info("URL validated for common response data Url was : " + URL);
        }
    }

    /**
     * To validate track list object we need to pass track list.
     * @param track_list
     * @return
     */
    private boolean validateTrackList(JSONArray track_list) {
        if(track_list.length() > 0) {
            List<Object> keys = helper.keys(track_list.getJSONObject(0));
            boolean isExpectedKeysPresent = helper.compareList(keys, RecomendedTrackTd.exTrackObjectKeys());
            if(!isExpectedKeysPresent) {
                log.error("Required keys are missing in track list response, manual check required!");
                Assert.assertEquals(isExpectedKeysPresent, true);
            }

            for(int i = 0; i < track_list.length(); i++) {
                JSONObject _track = track_list.getJSONObject(i);
                boolean track_common_data_validated = validateTrack(keys, _track);
                boolean isArtistValidated = false;
                if(track_common_data_validated){
                    isArtistValidated = validateSubArraysInTrackObject("Artist", _track.optJSONArray("artist"), RecomendedTrackTd.exArtistObjectKeys());
                }else{
                    Assert.assertEquals(track_common_data_validated, true, "key value data validation failed for tracks in url : "+URL);
                }
                if(isArtistValidated){
                    return true;
                }
            }
        } else {
            log.error("Tracks array empty for request url : " + URL);
        }
        return false;
    }

    /**
     * To validate each track key values pairs
     * @param keys
     * @param track
     * @return
     */
    private boolean validateTrack(List<Object> keys, JSONObject track) {
        if(!track.isEmpty() && keys.size() > 0) {
            return helper.validateEachObject(keys, track, RecomendedTrackTd.removeObjectValidationKeys());
        }else{
            log.error("Track List Object can't be null, please manually validate data for : "+ URL);
            Assert.assertEquals(track.isEmpty(), false);
        }
        return false;
    }

    /**
     * To validate data sub array in each track.
     * @param name
     * @param data
     * @param ex_keys
     * @return
     */
    private boolean validateSubArraysInTrackObject(String name, JSONArray data, List<String> ex_keys) {;
        if(data.length() > 0){
            List<Object> keys = helper.keys(data.getJSONObject(0));
            boolean isExpectedKeysPresent = helper.compareList(keys, ex_keys);
            if(isExpectedKeysPresent){
                Iterator<Object> itr = data.iterator();
                while(itr.hasNext()){
                    JSONObject data_obj = (JSONObject) itr.next();
                    return helper.validateEachObject(keys, data_obj, null);
                }
            }else{
                Assert.assertEquals(isExpectedKeysPresent, true, "Expected keys not present in "+name+" object!");
            }
        }
        return false;
    }

    /**
     * to get invocation count on basis of passed test data from csv.
     * @param context
     * @param count
     */
    public void provideApiHitCount(ITestContext context, int count) {
        ITestNGMethod currentTestNGMethod = null;
        for(ITestNGMethod testNGMethod : context.getAllTestMethods()) {
            if(testNGMethod.getInstance() == this) {
                currentTestNGMethod = testNGMethod;
                break;
            }
        }
        currentTestNGMethod.setInvocationCount(count);
    }
}
