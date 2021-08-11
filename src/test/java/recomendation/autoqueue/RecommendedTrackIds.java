package recomendation.autoqueue;
import config.Endpoints;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.response.Response;
import logic_controller.AutoQueueTrackController;
import java.util.*;
import org.slf4j.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;
import common.GlobalConfigHandler;
import test_data.AutoQueueTd;

public class RecommendedTrackIds {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    AutoQueueTrackController aqTrackController = new AutoQueueTrackController();
    private static Logger log = LoggerFactory.getLogger(RecommendedTrackIds.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-42282";
    final static String REPROTING_FEATURE = "RecommendedTrackIds validation.";

    @BeforeTest
    public void prepEnv(){
        // GlobalConfigHandler.setLocalProps();
        // baseurl();
        // BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = AutoQueueTd.INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status code,Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in RecomendedTrackTd file, and get response.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.NORMAL)

    public void createRecommendedTrackIdsReq(String track_id){
        String url = BASEURL+Endpoints.RECOMMENDED_TRACK_IDS+track_id;
        URLS.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);
        // Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            log.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate response should not be null as well as track ids must be integer format.")
    @Severity(SeverityLevel.NORMAL)
    public void validateRecommendedTrackResponseData(String track_id){
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isEntityIdsValid = aqTrackController.validateEntityIds(1, url, response);
        Assert.assertEquals(isEntityIdsValid, true, "Error in validateRecommendedTrackResponseData for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isEntityIdsValid)
            log.info("SimilarTrackGeneric -> EntityIds validated successfully.");
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
