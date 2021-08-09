package recomendation.album;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.SimilarAlbumController;
import test_data.SimilarAlbumsTd;

public class SimilarAlbums extends BaseUrls {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_DATA_PRESENT = false;
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    SimilarAlbumController controller = new SimilarAlbumController();
    private static Logger log = LoggerFactory.getLogger(SimilarAlbums.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-43450";
    final static String REPROTING_FEATURE = "Similar albums Api Validations";

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("reco_baseurl").toString().trim();
        MAX_CALL = SimilarAlbumsTd.SA_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status code,Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in SimilarAlbumsTd file, and get response.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.NORMAL)
    public void createSimilarAlbumCall(int album_id) {
        String url = controller.createUrl(BASEURL, album_id);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == (MAX_CALL)){
            Assert.assertEquals(RESPONSES.size() == URLS.size(), "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate whether response having contents or not.")
    @Severity(SeverityLevel.CRITICAL)
    public void checkResponseHavingData(int album_id){
        JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
        boolean isDataPresent = controller.validateResponseHavingData(response);
        Assert.assertTrue(isDataPresent, "No data available in api response for album_id : "+album_id);
        IS_DATA_PRESENT = isDataPresent;
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate album_id, seokey, title, language, favorite_count and status values for response objects.")
    @Severity(SeverityLevel.NORMAL)
    public void validateCommonDetailsInResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isBasicsValidated = controller.validateBasics(response);
            Assert.assertTrue(isBasicsValidated, "Common details validatations failed for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate track_count value must match with trackids value in response objects.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateTrackCountAndTrackidsInResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isTrackCountCorrect = controller.validatetrackIds(response);
            Assert.assertTrue(isTrackCountCorrect, "Tracks count not matched with track ids for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 5, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate basic artworks in response objects.")
    @Severity(SeverityLevel.NORMAL)
    public void validateArtworksInResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isArtworksValid = controller.validateArtworks(response);
            Assert.assertTrue(isArtworksValid, "Artworks not validated for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 6, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate custom artworks in response objects.")
    @Severity(SeverityLevel.NORMAL)
    public void validateCustomArtworksInResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isCustomArtworkValid = controller.validateCustomArtworks(response);
            Assert.assertTrue(isCustomArtworkValid, "Custom artworks not validated for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 7, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate primary artist details in response objects.")
    @Severity(SeverityLevel.NORMAL)
    public void validatePrimaryArtistInResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isPrimaryArtistValid = controller.validatePrimaryArtist(response);
            Assert.assertTrue(isPrimaryArtistValid, "Primary Artist not validated for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 8, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate artist details in response objects.")
    @Severity(SeverityLevel.NORMAL)
    public void validateArtistInResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isArtistValid = controller.validateArtist(response);
            Assert.assertTrue(isArtistValid, "Artist not validated for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 9, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate gener details in response objects.")
    @Severity(SeverityLevel.NORMAL)
    public void validateGenerResponse(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isGenerValid = controller.validateGener(response);
            Assert.assertTrue(isGenerValid, "Gener not validated for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @Test(enabled = true, priority = 10, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate release year and premium content details in response objects.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateReleaseYearAndPremiumContent(int album_id){
        if(checkDataAvl()){
            JSONObject response = new JSONObject(RESPONSES.get(API_CALL).asString());
            boolean isReleaseYearAndPremiumContentValid = controller.validateReleaseYearAndPremiumContent(response);
            Assert.assertTrue(isReleaseYearAndPremiumContentValid, "Release Year And Premium Content not validated for album_id : "+album_id);
            API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
        }
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                SimilarAlbumsTd.ALBUM_IDS[API_CALL]
            }
        };
    }

    private boolean checkDataAvl(){
        if(!IS_DATA_PRESENT){
            log.error("Response Data not available for detailed validations!");
            return false;
        }
        return true;
    }
}