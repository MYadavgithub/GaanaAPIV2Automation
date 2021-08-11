package recomendation.album;
import java.util.*;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import common.*;
import config.*;
import io.qameta.allure.*;
import io.restassured.response.Response;
import logic_controller.SAEntityInfoController;
import test_data.SimilarAlbumsTd;

public class SimilarAlbumsEntityInfo extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    SAEntityInfoController saController = new SAEntityInfoController();
    private static Logger log = LoggerFactory.getLogger(SimilarAlbumsEntityInfo.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-44897";
    final static String REPROTING_FEATURE = "SimilarAlbumsEntityInfo detailed validation.";

    @BeforeClass
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        MAX_CALL = SimilarAlbumsTd.SAEI_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SAEI_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status Code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all album ids which present in SimilarAlbumsTd file.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.NORMAL)
    public void createSimilarAlbumEntityReq(int album_id){
        String url = BASEURL+Endpoints.SIMILAR_ALBUMS_ENTITY_INFO+album_id;
        URLS.add(url);
        Response response = request.createGetRequest(url);
        System.out.println(response.asString());
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1)
            log.info("SimilarAlbumsEntityInfo api response captured successfully for further validations.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SAEI_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate whether response having contents or not along with status & entities count.")
    @Severity(SeverityLevel.CRITICAL)
    public void checkResponseHavingData(int album_id){
        String validator_key = SimilarAlbumsTd.VALIDATOR_KEYS[0];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isKeyValueValid = saController.validateResponses(validator_key, url, response);
        Assert.assertEquals(isKeyValueValid, true, "Error in validateEachTrackKeyValue for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isKeyValueValid)
            log.info("Count, status and response body entities count validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SAEI_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate Entities Key.")
    @Severity(SeverityLevel.BLOCKER)
    public void validateEntityKeys(int album_id){
        String validator_key = SimilarAlbumsTd.VALIDATOR_KEYS[1];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean exEntityKeysValid = saController.validateResponses(validator_key, url, response);
        Assert.assertEquals(exEntityKeysValid, true, "Error in validateEntityKeys for Url : "+url);
        if(API_CALL == MAX_CALL-1 && exEntityKeysValid)
            log.info("Expected entity keys validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SAEI_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate Entities Key.")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityKeysValue(int album_id){
        String validator_key = SimilarAlbumsTd.VALIDATOR_KEYS[2];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean exEntityKeyValuesValid = saController.validateResponses(validator_key, url, response);
        Assert.assertEquals(exEntityKeyValuesValid, true, "Error in validateEntityKeysValue for Url : "+url);
        if(API_CALL == MAX_CALL-1 && exEntityKeyValuesValid)
            log.info("Expected entity keys values validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 5, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SAEI_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate Entities All Type Artworks")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityArtworks(int album_id){
        String validator_key = SimilarAlbumsTd.VALIDATOR_KEYS[3];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean exEntityKeyValuesValid = saController.validateResponses(validator_key, url, response);
        Assert.assertEquals(exEntityKeyValuesValid, true, "Error in validateEntityArtworks for Url : "+url);
        if(API_CALL == MAX_CALL-1 && exEntityKeyValuesValid)
            log.info("Expected artwork values validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 6, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SAEI_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate Entities All Artists Data.")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityPrimaryArtists(int album_id){
        String validator_key = SimilarAlbumsTd.VALIDATOR_KEYS[4];
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean exEntityArtistsValid = saController.validateResponses(validator_key, url, response);
        Assert.assertEquals(exEntityArtistsValid, true, "Error in validateEntityPrimaryArtists for Url : "+url);
        if(API_CALL == MAX_CALL-1 && exEntityArtistsValid)
            log.info("Expected entity artists values validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                SimilarAlbumsTd.ALBUM_IDS[API_CALL]
            }
        };
    }
}
