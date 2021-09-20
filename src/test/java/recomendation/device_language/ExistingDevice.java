package recomendation.device_language;
import java.util.Map;
import org.slf4j.*;
import org.testng.annotations.*;
import io.qameta.allure.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import common.Helper;
import io.restassured.response.Response;
import test_data.DeviceLangTd;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import org.testng.asserts.SoftAssert;
import org.apache.tika.exception.UnsupportedFormatException;

/**
 * @author Umesh.Shukla
 */
public class ExistingDevice {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger LOG = LoggerFactory.getLogger(ExistingDevice.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45474";
    final static String REPROTING_FEATURE = "ExistingDevice content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = DeviceLangTd.DEVICE_IDS.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = DeviceLangTd.E_D_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in DeviceTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createExistingDeviceCall(String device_id) throws UnsupportedFormatException{
        SoftAssert softAssert = new SoftAssert();
        String url = BASEURL+Endpoints.IS_EXISTING_DEVICE;
        Map<String, String> headers = RequestHelper.getHeader(0);
        headers.replace("deviceId", device_id);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        boolean response_flag = Boolean.valueOf(response.asString());
        if(!response_flag){
            softAssert.assertFalse(response_flag);
            LOG.error(this.getClass()+" ExistingDevice failed for url : "+url);
        }else if(response_flag){
            softAssert.assertTrue(response_flag);
            LOG.info(this.getClass()+" ExistingDevice valid for url : "+url);
        }else{
            response.prettyPrint();
            LOG.info(this.getClass()+" ExistingDevice unexpected response for url : "+url);
            throw new UnsupportedFormatException("Response unexpected!");
        }

        softAssert.assertAll();
        if(API_CALL == MAX_CALL-1)
            LOG.info(this.getClass()+" TrackRecommend api validated succuessfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                DeviceLangTd.DEVICE_IDS[API_CALL]
            }
        };
    }
}
