package recomendation.reco_tracks;
import config.Endpoints;
import java.util.*;
import utils.CommonUtils;
import org.testng.Assert;
import org.json.*;
import common.GlobalConfigHandler;
import io.qameta.allure.*;
import io.restassured.response.Response;
import logic_controller.TrendingTrackController;
import test_data.TrendingTrackTd;
import org.testng.annotations.*;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;

/**
 * @author Umesh Shukla
 * @version 8.22.0 Support Disabled
 * @deprecated https://timesgroup.jira.com/browse/GAANA-43257
 */

public class TrendingShortTrack {

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    ArrayList<String> Types = new ArrayList<>(); 
    Map<Integer, Response> RESPONSES = new HashMap<>();
    TrendingTrackController controller = new TrendingTrackController();
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41651";
    final static String REPROTING_FEATURE = "Trending Short Track Api Validations";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        // GlobalConfigHandler.setLocalProps();
        Types.add(TrendingTrackTd.defaultCall);
        MAX_CALL = TrendingTrackTd.INVOCATION;
        // BASEURL = baseurl();
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = TrendingTrackTd.INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code,Response Time, Response Body Validation, Artworks.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in SearchFeedTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createGetTrendingShortTrackCall(String type_key){
        Response response;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        String url = BASEURL+Endpoints.trendingShortTrack;
        if(type_key.equals(TrendingTrackTd.defaultCall)){
            // response = request.createGetRequest(url);
            response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
            Types = controller.getSpecificSearchKeywords(Types, response);
        }else{
            url = BASEURL+Endpoints.trendingShortTrack+type_key;
            // response = request.createGetRequest(url);
            response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        }
        RESPONSES.put(API_CALL, response);
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = TrendingTrackTd.INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Data Array for key {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateData(String type_key){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray data = response.getJSONArray("data");
        boolean isDataValidated = controller.validateData(2, type_key, data);
        Assert.assertEquals(isDataValidated, true, "Data validations failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = TrendingTrackTd.INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Banner Data Array for key {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateBannerData(String type_key){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray bannerData = response.getJSONArray("bannerData");
        boolean isBannerValidated = controller.validateData(0, type_key, bannerData);
        Assert.assertEquals(isBannerValidated, true, "Banner data validations failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = TrendingTrackTd.INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Playlist Data Array for key {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validatePlayListData(String type_key){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray playListData = response.getJSONArray("playlistData");
        boolean isBannerValidated = controller.validateData(1, type_key, playListData);
        Assert.assertEquals(isBannerValidated, true, "PlayListData data validations failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] url(){
        return new Object[][]
        {
            {
                Types.get(API_CALL)
            }
        };
    }
}
