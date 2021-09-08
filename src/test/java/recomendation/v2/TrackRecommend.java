package recomendation.v2;
import org.slf4j.*;
import org.testng.annotations.*;
import io.qameta.allure.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import common.Helper;
import io.restassured.response.Response;
import test_data.RecomendedTrackTd;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import org.testng.asserts.SoftAssert;

public class TrackRecommend {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger LOG = LoggerFactory.getLogger(TrackRecommend.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45447";
    final static String REPROTING_FEATURE = "TrackRecommend content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = RecomendedTrackTd.TRACK_IDS.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = RecomendedTrackTd.T_RECOMMEND_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in RecomendedTrackTd file, and get response.")
    @Severity(SeverityLevel.NORMAL)
    public void createTrackRecommendReq(String track_id){
        SoftAssert softAssert = new SoftAssert();
        int random_count = Helper.generateRandomNumber(1, 12);
        String url = BASEURL+Endpoints.TRACK_RECOMMEND+track_id+"/"+random_count;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        String track_ids [] = response.asString().split(",");
        if(track_ids.length != random_count){
            LOG.error(this.getClass()+" TrackRecommend failed for url : "+url);
        }

        softAssert.assertEquals(track_ids.length, random_count);
        softAssert.assertAll();
        if(API_CALL == MAX_CALL-1)
            LOG.info(this.getClass()+" TrackRecommend api validated succuessfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                RecomendedTrackTd.TRACK_IDS[API_CALL]
            }
        };
    }
}
