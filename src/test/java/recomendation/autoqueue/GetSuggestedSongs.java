package recomendation.autoqueue;
import java.util.*;
import org.json.*;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import common.GlobalConfigHandler;
import common.Helper;
import config.Endpoints;
import config.enums.DeviceType;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.qameta.allure.*;
import io.restassured.response.Response;
import logic_controller.AutoQueueController;
import test_data.AutoQueueTd;

/**
 * @author [umesh.shukla]
 * @email [umesh.shukla@gaana.com]
 * @create date 2021-10-21 15:55:14
 * @modify date 2021-10-21 15:55:14
 * @desc [GetSuggestedSongs api validation]
 */
public class GetSuggestedSongs {
    
    int API_CALL;
    int MAX_CALL;
    String BASEURL;
    Helper helper = new Helper();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    AutoQueueController aqController = new AutoQueueController();
    private static Logger LOGGER = LoggerFactory.getLogger(GetSuggestedSongs.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-46065";
    final static String REPROTING_FEATURE = "GetSuggestedSongs api response validation.";
    
    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = AutoQueueTd.SS_INVOCATION;
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status Code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all track ids which present in AutoqueueTd file.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.NORMAL)
    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void createGetSuggestedCall(int type, String track_ids){
        String url;
        Map<String, String> headers;
        Response response = null;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        if(API_CALL == 0){
            url = BASEURL+Endpoints.GET_SUGGESTED_SONGS+type;
            headers = RequestHelper.getHeaders(0, DeviceType.ANDROID_APP);
            headers.replace("deviceId", AutoQueueTd.DEVICE_ID);
            response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        }else{
            url = BASEURL+Endpoints.GET_SUGGESTED_SONGS+type+"&trackIds="+track_ids;
            response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        }
        URLS.add(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOGGER.info(this.getClass()+" All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate in api response like count and status.")
    @Step("Validating validateCountStatusUserToken.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateCountStatusUserToken(int type, String track_ids){
        boolean isCountStatusUserTokenValid = aqController.validateCountStatusUserToken(URLS.get(API_CALL), RESPONSES.get(API_CALL));
        Assert.assertEquals(isCountStatusUserTokenValid, true, "Error in CountStatusUserTokenValidation for Url : "+URLS.get(API_CALL));
        if(API_CALL == MAX_CALL-1 && isCountStatusUserTokenValid)
            LOGGER.info(this.getClass()+" Count Status and user token validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("validate count on basis of type send in ap params.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateCountOnType(int type, String track_ids){
        boolean isTrackCountValid = false;
        Response responseObj = RESPONSES.get(API_CALL);
        JSONObject response = helper.responseJSONObject(responseObj);
        int count = Integer.parseInt(response.optString("count").toString().trim());
        JSONArray tracks = response.getJSONArray("tracks");
        int ex_track_count = AutoQueueTd.trackCount(String.valueOf(type));

        if(count <= ex_track_count && count == tracks.length()){
            isTrackCountValid = true;
            // LOGGER.info(this.getClass()+"Track count validated successfully for url : \n"+URLS.get(API_CALL));
        }else{
            isTrackCountValid  = false;
            LOGGER.error(this.getClass()+"Track count not validated successfully for url : \n"+URLS.get(API_CALL));
            Assert.assertEquals(count, ex_track_count);
        }

        if(API_CALL == MAX_CALL-1 && isTrackCountValid)
            LOGGER.info(this.getClass()+" Track count validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate all keys expected in tracks response object.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateTracksKeys(int type, String track_ids){
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isKeysValid = aqController.validateTracksKeys(url, response, AutoQueueTd.EX_TRACKS_KEYS);
        Assert.assertEquals(isKeysValid, true, "Error in validateTracksKeys for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isKeysValid)
            LOGGER.info(this.getClass()+" Tracks keys validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("To validate each track expected keys performing this test.")
    @Severity(SeverityLevel.NORMAL)
    @Test(enabled = true, priority = 5, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateEachTrackKeyValue(int type, String track_ids){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[0];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isKeyValueValid = aqController.validateTrackDetails(validator_key, url, response, AutoQueueTd.RTP_REMOVE_FROM_VALUE_VALIDATION);
        Assert.assertEquals(isKeyValueValid, true, "Error in validateEachTrackKeyValue for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isKeyValueValid)
            LOGGER.info(this.getClass()+" Tracks key value validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("To validate each track's all type of artworks")
    @Severity(SeverityLevel.NORMAL)
    @Test(enabled = true, priority = 6, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateEachArtworkForTracks(int type, String track_ids){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[1];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtworksValid = aqController.validateTrackDetails(validator_key, url, response, AutoQueueTd.ARTWORK_TYPES);
        Assert.assertEquals(isArtworksValid, true, "Error in validateEachArtworkForTracks for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtworksValid)
            LOGGER.info("Tracks all type artworks value validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate track having premium content or not.")
    @Severity(SeverityLevel.NORMAL)
    @Test(enabled = true, priority = 7, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateIsPremiumKeyPresent(int type, String track_ids) {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[2];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isPremiumKeyValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isPremiumKeyValid, true, "Error in validateIsPremiumKeyPresent for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isPremiumKeyValid)
            LOGGER.info("Tracks premium key value validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate release date should be in form of 10 < track > 10.")
    @Severity(SeverityLevel.NORMAL)
    @Test(enabled = true, priority = 8, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateTrackReleaseDates(int type, String track_ids){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[3];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isTrackReleaseDateValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isTrackReleaseDateValid, true, "Error in validateTrackReleaseDates for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isTrackReleaseDateValid)
            LOGGER.info(this.getClass()+" Tracks release date validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate language and language id should be same in over all response tracks.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(enabled = true, priority = 9, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateLanguageAndLanguageId(int type, String track_ids){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[4];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isLangAndLangIdValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isLangAndLangIdValid, true, "Error in validateLanguageAndLanguageId for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isLangAndLangIdValid)
            LOGGER.info(this.getClass()+" Tracks language and language id validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate geners should be same in over all response tracks.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(enabled = true, priority = 10, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateGener(int type, String track_ids){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[5];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isGenerValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isGenerValid, true, "Error in validateGener for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isGenerValid)
            LOGGER.info(this.getClass()+" Gener validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate artist data in each tracks.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(enabled = true, priority = 11, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateEachTrackArtistData(int type, String track_ids) {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[6];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtistValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isArtistValid, true, "Error in validateEachTrackArtistData for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtistValid)
            LOGGER.info(this.getClass()+" Artist validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate stream url for each track.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(enabled = true, priority = 12, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateEachTrackStreamUrl(int type, String track_ids) {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[7];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtistValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isArtistValid, true, "Error in validateEachTrackStreamUrl for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtistValid)
            LOGGER.info(this.getClass()+" Each track stream url validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate track format for each track.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(enabled = true, priority = 13, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateEachTrackFormat(int type, String track_ids) {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[8];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtistValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isArtistValid, true, "Error in validateEachTrackFormat for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtistValid)
            LOGGER.info(this.getClass()+" Each track format validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider(){
        return new Object [][]
        {
            {
                AutoQueueTd.TYPE[API_CALL], AutoQueueTd.TRACKS
            }
        };
    }
}
