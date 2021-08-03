package recomendation.autoqueue;
import config.BaseUrls;
import config.Endpoints;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.AutoQueueController;
import test_data.AutoQueueTd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author umesh-shukla
 */

public class RecommendedSongs extends BaseUrls{

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    GlobalConfigHandler handler = new GlobalConfigHandler();
    RequestHandler request = new RequestHandler();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    AutoQueueController aqController = new AutoQueueController();
    private static Logger log = LoggerFactory.getLogger(RecommendedSongs.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-44799";
    final static String REPROTING_FEATURE = "Recommended Track Post api response validation.";

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        MAX_CALL = AutoQueueTd.TRACK_IDS.length;
    }

    @Test(priority = 1, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status Code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all track ids which present in AutoqueueTd file.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.NORMAL)
    public void createRecoTrackReq(String track_id){
        String url = BASEURL+Endpoints.RECOMMENDED_SONGS+track_id;
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            log.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate in api response like count and status.")
    @Step("Validating validateCountStatusUserToken.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 2, invocationCount = AutoQueueTd.INVOCATION)
    public void validateCountStatusUserToken(){
        boolean isCountStatusUserTokenValid = aqController.validateCountStatusUserToken(URLS.get(API_CALL), RESPONSES.get(API_CALL));
        Assert.assertEquals(isCountStatusUserTokenValid, true, "Error in CountStatusUserTokenValidation for Url : "+URLS.get(API_CALL));
        if(API_CALL == MAX_CALL-1 && isCountStatusUserTokenValid)
            log.info("Count Status and user token validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate all keys expected in tracks response object.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 3, invocationCount = AutoQueueTd.INVOCATION)
    public void validateTracksKeys(){
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isKeysValid = aqController.validateTracksKeys(url, response, AutoQueueTd.EX_TRACKS_KEYS);
        Assert.assertEquals(isKeysValid, true, "Error in validateTracksKeys for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isKeysValid)
            log.info("Tracks keys validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("To validate each track expected keys performing this test.")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 4, invocationCount = AutoQueueTd.INVOCATION)
    public void validateEachTrackKeyValue(){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[0];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isKeyValueValid = aqController.validateTrackDetails(validator_key, url, response, AutoQueueTd.RTP_REMOVE_FROM_VALUE_VALIDATION);
        Assert.assertEquals(isKeyValueValid, true, "Error in validateEachTrackKeyValue for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isKeyValueValid)
            log.info("Tracks key value validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("To validate each track's all type of artworks")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 5, invocationCount = AutoQueueTd.INVOCATION)
    public void validateEachArtworkForTracks(){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[1];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtworksValid = aqController.validateTrackDetails(validator_key, url, response, AutoQueueTd.ARTWORK_TYPES);
        Assert.assertEquals(isArtworksValid, true, "Error in validateEachArtworkForTracks for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtworksValid)
            log.info("Tracks all type artworks value validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate track having premium content or not.")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 6, invocationCount = AutoQueueTd.INVOCATION)
    public void validateIsPremiumKeyPresent() {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[2];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isPremiumKeyValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isPremiumKeyValid, true, "Error in validateIsPremiumKeyPresent for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isPremiumKeyValid)
            log.info("Tracks premium key value validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate release date should be in form of 10 < track > 10.")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 7, invocationCount = AutoQueueTd.INVOCATION)
    public void validateTrackReleaseDates(){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[3];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isTrackReleaseDateValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isTrackReleaseDateValid, true, "Error in validateTrackReleaseDates for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isTrackReleaseDateValid)
            log.info("Tracks release date validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate language and language id should be same in over all response tracks.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 8, invocationCount = AutoQueueTd.INVOCATION)
    public void validateLanguageAndLanguageId(){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[4];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isLangAndLangIdValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isLangAndLangIdValid, true, "Error in validateLanguageAndLanguageId for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isLangAndLangIdValid)
            log.info("Tracks language and language id validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate geners should be same in over all response tracks.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 9, invocationCount = AutoQueueTd.INVOCATION)
    public void validateGener(){
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[5];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isGenerValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isGenerValid, true, "Error in validateGener for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isGenerValid)
            log.info("Gener validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate artist data in each tracks.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 10, invocationCount = AutoQueueTd.INVOCATION)
    public void validateEachTrackArtistData() {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[6];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtistValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isArtistValid, true, "Error in validateEachTrackArtistData for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtistValid)
            log.info("Artist validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate stream url for each track.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 11, invocationCount = AutoQueueTd.INVOCATION)
    public void validateEachTrackStreamUrl() {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[7];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtistValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isArtistValid, true, "Error in validateEachTrackStreamUrl for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtistValid)
            log.info("Each track stream url validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Description("Validate track format for each track.")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 12, invocationCount = AutoQueueTd.INVOCATION)
    public void validateEachTrackFormat() {
        String validator_key = AutoQueueTd.VALIDATOR_KEYS[8];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isArtistValid = aqController.validateTrackDetails(validator_key, url, response, null);
        Assert.assertEquals(isArtistValid, true, "Error in validateEachTrackFormat for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isArtistValid)
            log.info("Each track format validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                AutoQueueTd.TRACK_IDS[API_CALL]
            }
        };
    }
}