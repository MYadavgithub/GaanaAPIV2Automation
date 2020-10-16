package recomendation.reco_tracks;
import common.Helper;
import common.RequestHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import config.BaseUrls;
import config.Constants;
import config.Endpoints;
import utils.CommonUtils;
import utils.CsvReader;
import utils.WriteCsv;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.FileActions;
import common.GlobalConfigHandler;
import io.restassured.response.Response;
import test_data.RecomendedTrackTd;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecomendedTracks extends BaseUrls {

    String URL = "";
    int loop_count = 0;
    int api_hit_count = 0;
    String API_NAME = "Recomended_Track";
    int RECOMENDED_TRACK_COUNT = 30;
    boolean isPrevFilePresent = false;
    ArrayList<String> url_list = null;
    ArrayList<String> previous_data = null;
    Helper helper = new Helper();
    WriteCsv wcsv = new WriteCsv();
    CommonUtils util = new CommonUtils();
    RequestHandler req = new RequestHandler();
    Map<Integer, JSONObject> responses = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(RecomendedTracks.class);

    public static String prepareUrl(String baseurl, String track_id) {
        String end_points = Endpoints.recoTracks + track_id;
        return baseurl + end_points;
    }

    private void resetCounts() {
        if (api_hit_count == url_list.size()) {
            api_hit_count = 0;
        }
    }

    @BeforeClass
    public void generateAllRecoUrls() {
        String file_name_prev_data = API_NAME + ".csv";
        String file_path = "./src/test/resources/savedResponse/";
        isPrevFilePresent = FileActions.fileOperation(1, file_path, file_name_prev_data);
        if(isPrevFilePresent)
            previous_data = CsvReader.readCsv(file_path+file_name_prev_data);

        url_list = new ArrayList<>();
        String baseurl = BaseUrls.baseurl();
        String input_file = System.getProperty("user.dir") + "/src/test/resources/data/"+ prop.getProperty("tracks_td");
        ArrayList<String> input_values = CsvReader.readCsv(input_file);
        for (String val : input_values) {
            String url = prepareUrl(baseurl, val);
            url_list.add(url);
        }

        loop_count = url_list.size();
        if (url_list == null)
            log.error(API_NAME + " urls creation broken.");
    }

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                url_list.get(api_hit_count) 
            }
        };
    }

    @Test(priority = 1, dataProvider = "urlProvider", invocationCount = Constants.REC_INVOCATION_COUNT)
    public void callRecoTrack(String url) {
        Response response = req.createGetRequestCall(prop, url);
        if (response != null) {
            JSONObject response_object = new JSONObject(response.asString());
            responses.put(api_hit_count, response_object);
        } else {
            Assert.assertEquals(response != null, true, "Not able to get response for url : \n " + url);
        }

        api_hit_count++;
        if (loop_count == api_hit_count) {
            wcsv.writeCsv(API_NAME + ".csv", responses);
            resetCounts();
            log.info("All api hit response saved in map successfully.");
        }
    }

    @Test(priority = 2, dependsOnMethods = { "callRecoTrack" }, invocationCount = Constants.REC_INVOCATION_COUNT)
    private void validateCommonResponseData() {
        String response_count = responses.get(api_hit_count).optString("count").toString().trim();
        boolean response_status = responses.get(api_hit_count).getBoolean("status");
        boolean isJsonObjectCountValidated = Integer.parseInt(response_count) <= RECOMENDED_TRACK_COUNT;
        boolean isStatusOfResponseValidated = response_status;

        if (!isJsonObjectCountValidated && !isStatusOfResponseValidated) {
            log.error(API_NAME + " count found : " + response_count);
            Assert.assertEquals(isStatusOfResponseValidated, true, "Status and count key validation failed!");
        } else {
            log.info("URL validated for common response data Url was : " + url_list.get(api_hit_count));
            api_hit_count++;
        }

        if (loop_count == api_hit_count) {
            resetCounts();
            log.info("All api responses status and response object count validated successfully.");
        }
    }

    /**
     * To validate track list object we need to pass track list.
     * @param track_list
     * @return
     */
    @Test(priority = 3, dependsOnMethods = {"validateCommonResponseData" }, invocationCount = Constants.REC_INVOCATION_COUNT)
    public void validateTrackListKeysParams() {
        boolean isTrackListExpectedColsValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if (track_list.length() > 0) {
            List<Object> keys = helper.keys(track_list.getJSONObject(0));
            boolean isExpectedKeysPresent = helper.compareList(keys, RecomendedTrackTd.exTrackObjectKeys());
            if (!isExpectedKeysPresent) {
                isTrackListExpectedColsValidated = false;
                log.error("Required keys are missing in track list response, manual check required! api was \n "+url_list.get(api_hit_count));
                // Assert.assertEquals(isExpectedKeysPresent, true); // removed hard assertion
            } else {
                isTrackListExpectedColsValidated = true;
            }

        } else {
            log.error("Tracks array empty for request url : " + url_list.get(api_hit_count));
        }
        api_hit_count++;
        if (loop_count == api_hit_count) {
            resetCounts();
            log.error("Expected keys are present in all track list objects : " + isTrackListExpectedColsValidated);
        }
    }

    @Test(priority = 4, dependsOnMethods = {"validateCommonResponseData" }, invocationCount = Constants.REC_INVOCATION_COUNT)
    public void validateEachTrackKeyValueData() {
        boolean isKeyValueOfTrackValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if (!track_list.isEmpty()) {
            boolean track_common_data_validated = false;
            for (int i = 0; i < track_list.length(); i++) {
                List<Object> keys = helper.keys(track_list.getJSONObject(i));
                JSONObject _track = track_list.getJSONObject(i);
                track_common_data_validated = validateTrack(keys, _track);
            }

            if (track_common_data_validated) {
                isKeyValueOfTrackValidated = true;
            } else {
                log.error("While validating key value pairs in each track validation got failed for api \n"+ url_list.get(api_hit_count));
                Assert.assertEquals(isKeyValueOfTrackValidated, true);
            }
        } else {
            log.error("While validating track key value params we got empty track data api was : \n"+ url_list.get(api_hit_count));
            Assert.assertEquals(!track_list.isEmpty(), true);
        }

        api_hit_count++;
        if (loop_count == api_hit_count) {
            resetCounts();
            log.info("Expected key value data validated for each tracks Successfully : " + isKeyValueOfTrackValidated);
        }
    }

    @Test(priority = 5, dependsOnMethods = {"validateEachTrackKeyValueData" }, invocationCount = Constants.REC_INVOCATION_COUNT)
    public void validateEachTrackArtistData() {
        boolean isArtistValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if (!track_list.isEmpty()) {
            for (int i = 0; i < track_list.length(); i++) {
                JSONObject _track = track_list.getJSONObject(i);
                isArtistValidated = validateSubArraysInTrackObject("Artist", _track.optJSONArray("artist"),
                        RecomendedTrackTd.exArtistObjectKeys());
                if (isArtistValidated) {
                    isArtistValidated = true;
                } else {
                    isArtistValidated = false;
                    log.error("Artist validation failed, api was : \n" + url_list.get(api_hit_count)+ "\n track data was : " + _track);
                    Assert.assertEquals(isArtistValidated, true);
                }
            }
        }

        api_hit_count++;
        if (loop_count == api_hit_count) {
            resetCounts();
            log.info("In each track artist expected data validated successfully : " + isArtistValidated);
        }
    }

    @Test(priority = 6, dependsOnMethods = { "callRecoTrack" }, invocationCount = Constants.REC_INVOCATION_COUNT)
    public void validateIsPremiumKeyPresent() {
        boolean isPrimiumValidated = false;
        JSONArray track_list = responses.get(api_hit_count).getJSONArray("tracks");
        if (!track_list.isEmpty()) {
            for (int i = 0; i < track_list.length(); i++) {
                JSONObject track = track_list.getJSONObject(i);
                try {
                    int is_premium = Integer.parseInt(track.optString("is_premium").toString().trim());
                    if (is_premium == 0 || is_premium == 1) {
                        isPrimiumValidated = true;
                    } else {
                        isPrimiumValidated = false;
                    }
                } catch (Exception e) {
                    int device_type = GlobalConfigHandler.getDeviceType();
                    if (device_type == 0) {
                        log.error("is_primium key only you will get in above android version gaanaAndroid-8.9.0");
                    } else if (device_type == 1) {
                        log.error("is_primium key only you will get in above android version gaanaIphone-8.21.0");
                    }
                }
            }
        }

        api_hit_count++;
        if (loop_count == api_hit_count && isPrimiumValidated) {
            resetCounts();
            log.info("is_primium key we are getting in api responnse : " + isPrimiumValidated);
        }
    }

    /**
     * Compare Track Results from previous to new executed record.
     */
    @Test(priority = 7, dependsOnMethods = { "callRecoTrack" }, invocationCount = Constants.REC_INVOCATION_COUNT)
    public void comparePreviosRunTracksWithNewRun() {
        boolean result = false;
        String url = url_list.get(api_hit_count);
        if(previous_data == null && isPrevFilePresent == false){
            String file_name_prev_data = API_NAME + ".csv";
            String file_path = "./src/test/resources/savedResponse/";
            previous_data = CsvReader.readCsv(file_path+file_name_prev_data);
        }

        Assert.assertEquals(previous_data.size() == loop_count, true, "Previous data for new response comparision not available!");

        JSONArray tracks = responses.get(api_hit_count).getJSONArray("tracks");
        JSONArray previous_tracks = getPrevDataTracksData(previous_data.get(api_hit_count));

        if(tracks.length() == previous_tracks.length()){
            result = helper.validateResDataWithOldData(url, previous_tracks, tracks);
            assertEquals(result, true);
        }else{
            log.error("Previous Data count is not matched with current response count for more info please check response of : \n"+url);
            Assert.assertEquals(tracks.length() == previous_tracks.length(), true);
        }
        api_hit_count++;
        if(loop_count == api_hit_count){
            resetCounts();
            log.info("Old repoponse and new response matching as expected : "+result);
        }
    }

    /**
     * This method will convert previous data into jsonarray from string
     * @param prev_data
     * @return
     */
    private JSONArray getPrevDataTracksData(String prev_data) {
        String rechecked_str = util.removeQoutesForCsvData(prev_data);
        JSONObject Obj = new JSONObject(rechecked_str);
        JSONArray tracks_list = Obj.getJSONArray("tracks");
        Assert.assertEquals(tracks_list != null, true, "Previous data was not found in saved file please re-execute or manually check data!");
        return tracks_list;
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
