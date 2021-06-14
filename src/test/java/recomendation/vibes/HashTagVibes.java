package recomendation.vibes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import config.BaseUrls;
import utils.CommonUtils;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.HashTagVibesController;
import test_data.TrendingTrackTd;

/**
 * @author Umesh Shukla
 * @version 8.22.0 Support Disabled
 * @deprecated https://timesgroup.jira.com/browse/GAANA-43257
 */

public class HashTagVibes extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_COUNT_VALID = false;
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    RequestHandler request = new RequestHandler();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    HashTagVibesController controller = new HashTagVibesController();
    private static Logger log = LoggerFactory.getLogger(HashTagVibes.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41923";
    final static String REPROTING_FEATURE = "HashTag Vibe Api Validations";
    
    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        MAX_CALL = TrendingTrackTd.HV_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Response, Status code, Response Time, Response Body Validation, Artworks.")
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body for five hasgtags, response will captured and saved in map for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    public void createGetRequestSimilarArtist(String hashtag_name){
        String url = controller.prepareUrl(BASEURL, hashtag_name);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == (MAX_CALL-1)){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity info for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityInfo(String hashtag_name){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isEntityInfoValid = controller.validateEntityInfo(hashtag_name, entities);
        Assert.assertEquals(isEntityInfoValid, true, "Error! EntityInfo validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity shorttracks for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityMapShortTrack(String hashtag_name){
        String array_key = "short_track";
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isShortTrackValid = controller.validateEntityMapData(hashtag_name, entities, array_key);
        Assert.assertEquals(isShortTrackValid, true, "Error! short_track validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity Hastags for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityMapHastags(String hashtag_name){
        String array_key = "hashtags";
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isHastagsValid = controller.validateEntityMapData(hashtag_name, entities, array_key);
        Assert.assertEquals(isHastagsValid, true, "Error! hashtags validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 5, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity Artist for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityMapArtist(String hashtag_name){
        String array_key = "artist";
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isArtistValid = controller.validateEntityMapData(hashtag_name, entities, array_key);
        Assert.assertEquals(isArtistValid, true, "Error! artist validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 6, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity Albums for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityMapAlbums(String hashtag_name){
        String array_key = "album";
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isAlbumsValid = controller.validateEntityMapData(hashtag_name, entities, array_key);
        Assert.assertEquals(isAlbumsValid, true, "Error! album validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 7, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity Tracks for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityMapTracks(String hashtag_name){
        String array_key = "track";
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isTrackValid = controller.validateEntityMapData(hashtag_name, entities, array_key);
        Assert.assertEquals(isTrackValid, true, "Error! track validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 8, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating EntityMapInfo for hashtag name {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityMapInfo(String hashtag_name){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isEntityMapInfo = controller.validateEntityMapData(hashtag_name, entities, null);
        Assert.assertEquals(isEntityMapInfo, true, "Error! EntityMapInfo validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                TrendingTrackTd.HASHTAGS[API_CALL]
            }
        };
    }
}