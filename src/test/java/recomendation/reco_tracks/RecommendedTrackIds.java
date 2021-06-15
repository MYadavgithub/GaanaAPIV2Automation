package recomendation.reco_tracks;
import config.BaseUrls;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.RecoTrackController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import common.GlobalConfigHandler;
import common.RequestHandler;
import test_data.RecomendedTrackTd;

public class RecommendedTrackIds extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    RecoTrackController controller = new RecoTrackController();
    private static Logger log = LoggerFactory.getLogger(RecomendedTracks.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-42282";
    final static String REPROTING_FEATURE = "RecommendedTrackIds validation.";

    @BeforeTest
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        BASEURL = baseurl();
        MAX_CALL = RecomendedTrackTd.track_ids.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = RecomendedTrackTd.T_IDS_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status code,Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in RecomendedTrackTd file, and get response.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.NORMAL)

    public void createRecommendedTrackIdsCall(String track_id){
        String url = controller.prepRecoTrackIdsUrl(BASEURL, track_id);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            log.info("ALl response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = RecomendedTrackTd.T_IDS_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate response should not be null as well as track ids must be integer format.")
    @Severity(SeverityLevel.NORMAL)
    public void validateRecommendedTrackResponseData(String track_id){
        SoftAssert softAssert = new SoftAssert();
        Response response = RESPONSES.get(API_CALL);
        JSONArray response_arr =  new JSONArray(response.asString());
        softAssert.assertEquals(response_arr.length() > 0, true, "Track ids not found for given request! Url was : "+URLS.get(API_CALL));

        if(response_arr.length() > 0){
            Iterator<Object> tracks = response_arr.iterator();
            while(tracks.hasNext()){
                Object track = tracks.next();
                int trackId = Integer.parseInt(track.toString().trim());
                softAssert.assertEquals(trackId > 0, true, "Track ids must be numeric value!");
            }
        }

        softAssert.assertAll();
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] url(){
        return new Object[][]
        {
            {
                RecomendedTrackTd.track_ids[API_CALL]
            }
        };
    }
}
