package recomendation.album;
import java.util.*;
import org.json.JSONObject;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import common.GlobalConfigHandler;
import config.v1.*;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.qameta.allure.*;
import io.restassured.response.Response;
import logic_controller.SimilarAlbumController;
import test_data.SimilarAlbumsTd;

public class SimilarAlbums {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_DATA_PRESENT = false;
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    SimilarAlbumController controller = new SimilarAlbumController();
    private static Logger log = LoggerFactory.getLogger(SimilarAlbums.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-43450";
    final static String REPROTING_FEATURE = "Similar albums Api Validations";

    @BeforeClass
    public void prepareEnv(){
        // GlobalConfigHandler.setLocalProps();
        // baseurl();
        // BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        BASEURL = GlobalConfigHandler.baseurl();
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
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        URLS.add(url);
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == MAX_CALL){
            Assert.assertEquals(RESPONSES.size() == URLS.size(), "Error! responses not saved.");
            log.info("SimilarAlbums : All responses captured successfully for further validations : "+(RESPONSES.size() == URLS.size()));
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
        if(API_CALL == MAX_CALL-1)
            log.info("SimilarAlbums : Response having required content validated successfully : "+IS_DATA_PRESENT);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : common details like album_id, seokey, title, language, favorite_count and status validated successfully : "+isBasicsValidated);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : track count value matched with trackids successfully : "+isTrackCountCorrect);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : Artworks validated successfully : "+isArtworksValid);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : Custom artworks validated successfully : "+isCustomArtworkValid);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : Primary Artist validated successfully : "+isPrimaryArtistValid);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : Artist validated successfully : "+isArtistValid);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : Gener validated successfully : "+isGenerValid);
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
            if(API_CALL == MAX_CALL-1)
                log.info("SimilarAlbums : Release Year And Premium Content validated successfully : "+isReleaseYearAndPremiumContentValid);
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