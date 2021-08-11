package recomendation.autoqueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import common.GlobalConfigHandler;
import common.RequestHandler;
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
import logic_controller.AutoQueueTrackController;
import test_data.AutoQueueTd;

public class RecommendedTracksAQ extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    AutoQueueTrackController aqTrackController = new AutoQueueTrackController();
    private static Logger log = LoggerFactory.getLogger(RecommendedTracksAQ.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-42284";
    final static String REPROTING_FEATURE = "RecommendedTracksAQ validation.";

    @BeforeTest
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        MAX_CALL = AutoQueueTd.INVOCATION;
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status code,Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in RecomendedTrackTd file, and get response.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    public void createRecommendedTrackIdsCall(String track_id){
        String url = BASEURL+Endpoints.RECOMMENDED_TRACKS_AQ+track_id;
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
    @Step("Validate response should not be null as well as track ids must be integer format.")
    @Severity(SeverityLevel.NORMAL)
    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    public void validateRecommendedTrackResponseData(String track_id){
        SoftAssert softAssert = new SoftAssert();
        String response = RESPONSES.get(API_CALL).asString();

        String[] tracks = response.split(",");
        for(String track : tracks){
            int trackId = Integer.parseInt(track.toString().trim());
            softAssert.assertEquals(trackId > 0, true, "Track ids must be numeric value!");
        }

        softAssert.assertAll();
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] url(){
        return new Object[][]
        {
            {
                AutoQueueTd.TRACK_IDS[API_CALL]
            }
        };
    }
}