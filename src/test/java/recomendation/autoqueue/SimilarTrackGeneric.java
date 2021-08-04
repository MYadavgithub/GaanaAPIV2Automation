package recomendation.autoqueue;
import config.BaseUrls;
import config.Endpoints;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import test_data.AutoQueueTd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import logic_controller.AutoQueueTrackController;

public class SimilarTrackGeneric extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    AutoQueueTrackController aqTrackController = new AutoQueueTrackController();
    private static Logger log = LoggerFactory.getLogger(SimilarTrackGeneric.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-44820";
    final static String REPROTING_FEATURE = "Similar Track Generic api response validation.";

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
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