package recomendation.autoqueue;
import config.Endpoints;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import test_data.AutoQueueTd;
import java.util.*;
import org.slf4j.*;
import org.testng.Assert;
import common.GlobalConfigHandler;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.*;
import logic_controller.AutoQueueTrackController;

public class SimilarTrackGeneric {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    AutoQueueTrackController aqTrackController = new AutoQueueTrackController();
    private static Logger log = LoggerFactory.getLogger(SimilarTrackGeneric.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-44820";
    final static String REPROTING_FEATURE = "Similar Track Generic api response validation.";

    @BeforeClass
    public void prepareEnv(){
        // GlobalConfigHandler.setLocalProps();
        // baseurl();
        // BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = AutoQueueTd.INVOCATION;
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status Code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all track ids which present in AutoqueueTd file.")
    @Description("Save all the response in runtime memory for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 1, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    public void createSimilarTrackGenericReq(String track_id){
        String url = BASEURL+Endpoints.SIMILAR_TRACK_GENERIC+track_id;
        URLS.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        // Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            log.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate status and entity type for all responses.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 2, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    public void validateStatusEntityType(String track_id){
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isStatusEntityType = aqTrackController.validateStatusEntityType(url, response, AutoQueueTd.EX_ENTITY_TYPE);
        Assert.assertEquals(isStatusEntityType, true, "Error in validateStatusEntityType for Url : "+url);
        if(API_CALL == MAX_CALL-1 && isStatusEntityType)
            log.info("SimilarTrackGeneric -> status and entity type validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate entity ids array in responses.")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 3, dataProvider = "dp", invocationCount = AutoQueueTd.INVOCATION)
    public void validateEntityIds(String track_id){
        String url = URLS.get(API_CALL);
        Response response = RESPONSES.get(API_CALL);
        boolean isEntityIdsValid = aqTrackController.validateEntityIds(0, url, response);
        Assert.assertEquals(isEntityIdsValid, true, "Error in validateEntityIdsValid for Url : "+url);
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