package recomendation.v2;
import java.util.*;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import io.qameta.allure.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import common.Helper;
import io.restassured.response.Response;
import pojo.Tagaffinity30daysPojo;
import test_data.PodcastTd;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;

public class Tagaffinity30days {

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger LOG = LoggerFactory.getLogger(Tagaffinity30days.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45436";
    final static String REPROTING_FEATURE = "Tagaffinity30days content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = PodcastTd.TAG_AFFINITY_30DAYS_DEVICES.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = PodcastTd.TAG_AFFINITY_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in PodcastTd file, and get response.")
    @Severity(SeverityLevel.NORMAL)
    public void createRecoTrackReq(String device_md5){
        String url = BASEURL+Endpoints.TAG_AFFINITY_30DAYS+device_md5;
        URLS.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOG.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = PodcastTd.TAG_AFFINITY_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body using plane java object class.")
    @Severity(SeverityLevel.NORMAL)
    public void validateRecommendedTrackResponseData(String device_md5){
        SoftAssert softAssert = new SoftAssert();
        JSONObject response_object = helper.responseJSONObject(RESPONSES.get(API_CALL));
        Tagaffinity30daysPojo tagaffinity30days= new Gson().fromJson(response_object.toString(), Tagaffinity30daysPojo.class);

        if(tagaffinity30days.getId() == null){
            boolean tagAffinityNotFound = tagaffinity30days.getTags() == null;
            if(tagAffinityNotFound){
                LOG.error(this.getClass()+" For URL : "+URLS.get(API_CALL)+ " Tagaffinity not found!");
                softAssert.assertEquals(tagAffinityNotFound, false);
            }
        }else if(tagaffinity30days.getId().toString().trim().equals(device_md5)){
            boolean tagAffinityNotFound = tagaffinity30days.getTags().size() > 0;
            if(tagAffinityNotFound){
                LOG.info(this.getClass()+" For URL : "+URLS.get(API_CALL)+ " Tagaffinity working as expected.");
            }
            softAssert.assertEquals(tagAffinityNotFound, true);
        }
        softAssert.assertAll();
        if(API_CALL == MAX_CALL-1)
            LOG.info("Tagaffinity30days api validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }
    
    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                PodcastTd.TAG_AFFINITY_30DAYS_DEVICES[API_CALL]
            }
        };
    }
}
