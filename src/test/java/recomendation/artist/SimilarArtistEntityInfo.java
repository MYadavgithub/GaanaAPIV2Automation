package recomendation.artist;
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
import org.testng.asserts.SoftAssert;
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
import logic_controller.ArtistController;
import test_data.ArtistTd;

public class SimilarArtistEntityInfo extends BaseUrls{

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_COUNT_VALID = false;
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    RequestHandler request = new RequestHandler();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    ArtistController controller = new ArtistController();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(SimilarArtists.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41682";
    final static String REPROTING_FEATURE = "SimilarArtistsInfo Api Validations";

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        MAX_CALL = ArtistTd.ARTIST_INFO_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = ArtistTd.ARTIST_INFO_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation, Artworks.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in ArtistTd file, and get response, for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    public void createGetRequestSimilarArtistInfo(int artist_id){
        String url = controller.prepareUrl(1, BASEURL, artist_id);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == (MAX_CALL-1)){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = ArtistTd.ARTIST_INFO_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Entity counts and Entity description for artist id {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateEntityCountAndDescription(int artist_id){
        SoftAssert softAssert = new SoftAssert();
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        int count = Integer.parseInt(response.optString("count").toString().trim());
        int status = Integer.parseInt(response.optString("status").toString().trim());
        String entityDescription = response.optString("entityDescription").toString().trim();
        int count_of_entity = response.getJSONArray("entities").length();

        softAssert.assertEquals(count_of_entity, count, "Entity object count validation failed!");
        softAssert.assertEquals(status >= 0, true, "Status value validation failed!");
        softAssert.assertEquals((entityDescription.isEmpty() || entityDescription.length() > 0), true, "Entity description validation failed!");
        softAssert.assertAll();
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = ArtistTd.ARTIST_INFO_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Common Entity for artist id {0}")
    @Severity(SeverityLevel.CRITICAL)
    public void validateCommonEntityDetails(int artist_id){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isCommonDetailsValid = controller.validateCommonEntityDetails(URLS.get(API_CALL), entities);
        Assert.assertEquals(isCommonDetailsValid, true, "Error! Artist Common Data validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = ArtistTd.ARTIST_INFO_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating EntityInfo for artist id {0}")
    @Severity(SeverityLevel.CRITICAL)
    public void validateEntityInfoDetails(int artist_id){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray entities = response.getJSONArray("entities");
        boolean isEntityInfoValid = controller.validateEntityInfo(URLS.get(API_CALL), entities);
        Assert.assertEquals(isEntityInfoValid, true, "Error! Artist EntityInfo Data validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                ArtistTd.artistIds[ArtistTd.artistIds.length-1]
            }
        };
    }
}