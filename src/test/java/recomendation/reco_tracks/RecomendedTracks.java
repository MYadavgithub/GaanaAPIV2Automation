package recomendation.reco_tracks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import config.BaseUrls;
import config.Endpoints;
import utils.CsvReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.Helper;
import common.RequestHandler;
import io.restassured.response.Response;
import test_data.RecomendedTrackTd;

public class RecomendedTracks extends BaseUrls {

    String URL = "";
    int loop_count = 0;
    int api_hit_count = 0;
    String API_NAME = "Recomended Track";
    int RECOMENDED_TRACK_COUNT = 30;
    final static int INVOCATION = 5;
    ArrayList<String> url_list = null;
    Helper helper = new Helper();
    RequestHandler req = new RequestHandler();
    Map<Integer, JSONObject> responses = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(RecomendedTracks.class);

    public static String prepareUrl(String baseurl, String track_id) {
        String end_points = Endpoints.recoTracks + track_id;
        return baseurl + end_points;
    }

    private void resetCounts(){
        if(api_hit_count == url_list.size()){
            api_hit_count = 0;
        }
    }

    @BeforeClass
    public void generateAllRecoUrls() {
        url_list = new ArrayList<>();
        String baseurl = BaseUrls.baseurl();
        ArrayList<String> input_values = CsvReader.readCsv(prop.getProperty("tracks_td"));
        for (String val : input_values) {
            String url = prepareUrl(baseurl, val);
            url_list.add(url);
        }

        loop_count = url_list.size();
        if (url_list == null)
            log.error(API_NAME + " urls creation broken.");
        // INVOCATION = loop_count;
    }

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] { 
            { 
                url_list.get(api_hit_count) 
            }
        };
    }

    @Test(priority = 1, dataProvider = "urlProvider", invocationCount = INVOCATION)
    public void callRecoTrack(String url) {
        Response response = req.createGetRequestCall(prop, url);
        if (response != null) {
            JSONObject response_object = new JSONObject(response.asString());
            responses.put(api_hit_count, response_object);
        }else{
            Assert.assertEquals(response != null, true, "Not able to get response for url : \n "+url);
        }

        api_hit_count++;
        if(loop_count == api_hit_count){
            resetCounts();
            log.info("All api hit response saved in map successfully.");
        }
    }

    @Test(priority = 2, dependsOnMethods = {"callRecoTrack"}, invocationCount = INVOCATION)
    private void validateCommonResponseData() {
        String response_count = responses.get(api_hit_count).optString("count").toString().trim();
        boolean response_status = responses.get(api_hit_count).getBoolean("status");
        boolean isJsonObjectCountValidated = Integer.parseInt(response_count) <= RECOMENDED_TRACK_COUNT;
        boolean isStatusOfResponseValidated = response_status;
        if (!isJsonObjectCountValidated && !isStatusOfResponseValidated) {
            log.error(API_NAME + " count found : " +response_count);
            Assert.assertEquals(isStatusOfResponseValidated, true, "Status and count key validation failed!");
        } else {
            log.info("URL validated for common response data Url was : " +url_list.get(api_hit_count));
            api_hit_count++;
        }

        if(loop_count == api_hit_count){
            resetCounts();
            log.info("All api responses status and response object count validated successfully.");
        }
    }

    /**
     * To validate track list object we need to pass track list.
     * @param track_list
     * @return
     */
    @Test(priority = 3, dependsOnMethods = {"validateCommonResponseData"}, invocationCount = INVOCATION)
    public void validateTrackListKeysParams() {
        boolean isTrackListExpectedColsValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if(track_list.length() > 0) {
            List<Object> keys = helper.keys(track_list.getJSONObject(0));
            boolean isExpectedKeysPresent = helper.compareList(keys, RecomendedTrackTd.exTrackObjectKeys());
            if(!isExpectedKeysPresent) {
                isTrackListExpectedColsValidated = false;
                log.error("Required keys are missing in track list response, manual check required! api was \n "+url_list.get(api_hit_count));
                // Assert.assertEquals(isExpectedKeysPresent, true); // removed hard assertion
            }else{
                isTrackListExpectedColsValidated = true;
            }

        } else {
            log.error("Tracks array empty for request url : " +url_list.get(api_hit_count));
        }
        api_hit_count++;
        if(loop_count == api_hit_count){
            resetCounts();
            log.error("Expected keys are present in all track list objects : "+isTrackListExpectedColsValidated);
        }
    }

    @Test(priority = 4, dependsOnMethods = {"validateCommonResponseData"}, invocationCount = INVOCATION)
    public void validateEachTrackKeyValueData(){
        boolean isKeyValueOfTrackValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if(!track_list.isEmpty()){
            boolean track_common_data_validated = false;
            for(int i = 0; i <track_list.length(); i++) {
                List<Object> keys = helper.keys(track_list.getJSONObject(i));
                JSONObject _track = track_list.getJSONObject(i);
                track_common_data_validated = validateTrack(keys, _track);
            }

            if(track_common_data_validated){
                isKeyValueOfTrackValidated = true;
            }else{
                log.error("While validating key value pairs in each track validation got failed for api \n"+url_list.get(api_hit_count));
                Assert.assertEquals(isKeyValueOfTrackValidated, true);
            }
        }else{
            log.error("While validating track key value params we got empty track data api was : \n"+url_list.get(api_hit_count));
            Assert.assertEquals(!track_list.isEmpty(), true);
        }

        api_hit_count++;
        if(loop_count == api_hit_count){
            resetCounts();
            log.info("Expected key value data validated for each tracks Successfully : "+isKeyValueOfTrackValidated);
        }
    }

    @Test(priority = 5, dependsOnMethods = {"validateEachTrackKeyValueData"}, invocationCount = INVOCATION)
    public void validateEachTrackArtistData(){
        boolean isArtistValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if(!track_list.isEmpty()){
            for(int i = 0; i <track_list.length(); i++) {
                JSONObject _track = track_list.getJSONObject(i);
                isArtistValidated = validateSubArraysInTrackObject("Artist", _track.optJSONArray("artist"), RecomendedTrackTd.exArtistObjectKeys());
                if(isArtistValidated){
                    isArtistValidated = true;
                }else{
                    isArtistValidated = false;
                    log.error("Artist validation failed, api was : \n"+url_list.get(api_hit_count)+"\n track data was : "+_track);
                    Assert.assertEquals(isArtistValidated, true);
                }
            }
        }

        api_hit_count++;
        if(loop_count == api_hit_count){
            resetCounts();
            log.info("In each track artist expected data validated successfully : "+isArtistValidated);
        }
    }

    @Test(priority = 6, dependsOnMethods = {"callRecoTrack"}, invocationCount = INVOCATION)
    public void validateIsPremiumKeyPresent(){
        boolean isPrimiumValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if(!track_list.isEmpty()){
            for(int i = 0; i <track_list.length(); i++) {
                JSONObject track = track_list.getJSONObject(i);
                try{
                    int is_premium = Integer.parseInt(track.optString("is_premium").toString().trim());
                    if(is_premium == 0 || is_premium == 1){
                        isPrimiumValidated = true;
                    }else{
                        isPrimiumValidated = false;
                    }
                }catch(Exception e){
                    int device_type = GlobalConfigHandler.getDeviceType();
                    if(device_type == 0){
                        log.error("is_primium key only you will get in above android version gaanaAndroid-8.9.0");
                    }else if(device_type == 1){
                        log.error("is_primium key only you will get in above android version gaanaIphone-8.21.0");
                    }
                }
            }
        }

        api_hit_count++;
        if(loop_count == api_hit_count && isPrimiumValidated){
            resetCounts();
            log.info("is_primium key we are getting in api responnse : "+isPrimiumValidated);
        }
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
    private boolean validateSubArraysInTrackObject(String name, JSONArray data, List<String> ex_keys) {
        if(data.length() > 0){
            List<Object> keys = helper.keys(data.getJSONObject(0));
            boolean isExpectedKeysPresent = helper.compareList(keys, ex_keys);
            if(isExpectedKeysPresent){
                Iterator<Object> itr = data.iterator();
                while(itr.hasNext()){
                    JSONObject data_obj = (JSONObject) itr.next();
                    return helper.validateEachJsonObjectWithAllowedEmptyData(keys, data_obj);
                }
            }else{
                Assert.assertEquals(isExpectedKeysPresent, true, "Expected keys not present in "+name+" object!");
            }
        }
        return false;
    }
}
