package recomendation.artist;
import java.util.*;
import org.json.*;
import org.slf4j.*;
import io.qameta.allure.*;
import io.restassured.response.Response;
import logic_controller.ArtistController;
import test_data.ArtistTd;
import utils.CommonUtils;
import org.testng.annotations.*;
import common.GlobalConfigHandler;
import org.testng.Assert;
import config.v2.RequestHandlerV1;
import config.v2.RequestHelper;
import config.v2.RequestHelper.ApiRequestTypes;
import config.v2.RequestHelper.ContentTypes;

/**
 * @author Umesh Shukla
 */

public class SimilarArtists {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_COUNT_VALID = false;
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    ArtistController controller = new ArtistController();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(SimilarArtists.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41673";
    final static String REPROTING_FEATURE = "SimilarArtists Api Validations";

    @BeforeClass
    public void prepareEnv(){
        // GlobalConfigHandler.setLocalProps();
        // baseurl();
        // BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = ArtistTd.artistIds.length-1;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = ArtistTd.INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation, Artworks.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in ArtistTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createGetRequestSimilarArtist(int artist_id){
        String url = controller.prepareUrl(0, BASEURL, artist_id);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        URLS.add(url);
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == (MAX_CALL-1)){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = ArtistTd.INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Object counts for artist id {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateArtistCountInResponses(int artist_id){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray artist = response.getJSONArray("artist");
        int count = Integer.parseInt(response.optString("count").toString().trim());
        Assert.assertEquals(artist.length(), count, "Error! Api count value not matching with received Artits Objects!");
        IS_COUNT_VALID = true;
        if(API_CALL == (MAX_CALL-1) && IS_COUNT_VALID){
            log.info("All responses api object count and self artist object count validated successfully.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = ArtistTd.INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Artits Data for artist id {0}")
    @Severity(SeverityLevel.CRITICAL)
    public void validateArtistsData(int artist_id){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray artist = response.getJSONArray("artist");
        boolean isArtistDataValid = controller.validateArtistData( URLS.get(API_CALL), artist);
        Assert.assertEquals(isArtistDataValid, true, "Error! Artist Data validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                ArtistTd.artistIds[API_CALL]
            }
        };
    }
}
