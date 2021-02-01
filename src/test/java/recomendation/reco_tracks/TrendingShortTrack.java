package recomendation.reco_tracks;
import config.BaseUrls;
import config.Endpoints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import utils.CommonUtils;
import org.testng.Assert;
import org.json.JSONArray;
import org.json.JSONObject;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import io.restassured.response.Response;
import logic_controller.TrendingTrackController;
import test_data.TrendingTrackTd;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
 * @author Umesh Shukla
 */

public class TrendingShortTrack extends BaseUrls {

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    ArrayList<String> Types = new ArrayList<>(); 
    Map<Integer, Response> RESPONSES = new HashMap<>();
    TrendingTrackController controller = new TrendingTrackController();
    // private static Logger log = LoggerFactory.getLogger(TrendingShortTrack.class);

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        Types.add(TrendingTrackTd.defaultCall);
        MAX_CALL = TrendingTrackTd.invocation;
        BASEURL = baseurl();
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = TrendingTrackTd.invocation)
    public void createGetTrendingShortTrackCall(String type_key){
        Response response;
        String url = BASEURL+Endpoints.trendingShortTrack;
        if(type_key.equals(TrendingTrackTd.defaultCall)){
            response = request.createGetRequest(url);
            Types = controller.getSpecificSearchKeywords(Types, response);
        }else{
            url = BASEURL+Endpoints.trendingShortTrack+type_key;
            response = request.createGetRequest(url);
        }
        RESPONSES.put(API_CALL, response);
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = TrendingTrackTd.invocation)
    public void validateData(String type_key){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray data = response.getJSONArray("data");
        boolean isDataValidated = controller.validateData(2, type_key, data);
        Assert.assertEquals(isDataValidated, true, "Data validations failed!");
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = TrendingTrackTd.invocation)
    public void validateBannerData(String type_key){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray bannerData = response.getJSONArray("bannerData");
        boolean isBannerValidated = controller.validateData(0, type_key, bannerData);
        Assert.assertEquals(isBannerValidated, true, "Banner data validations failed!");
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = TrendingTrackTd.invocation)
    public void validatePlayListData(String type_key){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray playListData = response.getJSONArray("playlistData");
        boolean isBannerValidated = controller.validateData(1, type_key, playListData);
        Assert.assertEquals(isBannerValidated, true, "PlayListData data validations failed!");
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
