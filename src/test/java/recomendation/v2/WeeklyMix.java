package recomendation.v2;
import java.util.Map;
import org.slf4j.*;
import org.testng.annotations.*;
import io.qameta.allure.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import common.Helper;
import io.restassured.response.Response;
import test_data.MixTd;
import utils.CommonUtils;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import org.testng.asserts.SoftAssert;

/**
 * @author Umesh.Shukla
 */
public class WeeklyMix {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger LOG = LoggerFactory.getLogger(WeeklyMix.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45485";
    final static String REPROTING_FEATURE = "WeeklyMix api content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = MixTd.DM_DEVICES.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = MixTd.DM_INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in MixTd file, and get response.")
    @Severity(SeverityLevel.NORMAL)
    public void createWeeklyMixReqAndValidateResponse(String device_id){
        SoftAssert softAssert = new SoftAssert();
        String device_md5 = CommonUtils.md5(device_id);
        String url = BASEURL+Endpoints.WEEKLY_MIX+device_md5;
        Map<String, String> headers = RequestHelper.getHeader(0);
        headers.replace("deviceId", device_id);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        String track_ids [] = response.asString().split(",");
        if(track_ids.length > 0 && !track_ids[0].equals("")){
            for(String track_id : track_ids){
                try{
                    int _track_id = Integer.parseInt(track_id);
                    softAssert.assertEquals(_track_id > 0, true);
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }
            }
        }else{
            softAssert.assertEquals(track_ids[0].equals(""), true);
            LOG.warn(this.getClass()+" for device_id : "+device_id+" and Url : "+url+" no data found...");
        }

        softAssert.assertAll();
        if(API_CALL == MAX_CALL-1)
            LOG.info(this.getClass()+" WeeklyMix api validated succuessfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                MixTd.DM_DEVICES[API_CALL]
            }
        };
    }
}
