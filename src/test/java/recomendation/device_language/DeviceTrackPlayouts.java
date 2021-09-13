package recomendation.device_language;
import java.util.*;
import org.slf4j.*;
import pojo.*;
import org.json.*;
import org.testng.Assert;
import org.testng.annotations.*;
import io.qameta.allure.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import common.Helper;
import test_data.DeviceLangTd;
import utils.CommonUtils;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;
import logic_controller.DeviceLanguageController;

/**
 * @author Umesh.Shukla
 */
public class DeviceTrackPlayouts {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    DeviceLanguageController controller = new DeviceLanguageController();
    private static Logger LOG = LoggerFactory.getLogger(DeviceTrackPlayouts.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45479";
    final static String REPROTING_FEATURE = "DeviceTrackPlayouts content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = DeviceLangTd.DTP_DEVICE_IDS.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = DeviceLangTd.DTP_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in DeviceTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createDeviceTrackPlayoutsReq(String device_id) {
        device_id = utils.createUrlEncodedStr(device_id);
        String url = BASEURL+Endpoints.DEVICE_TRACK_PLAYOUTS+device_id;
        URLS.add(url);
        Map<String, String> headers = RequestHelper.getHeader(0);
        headers.replace("deviceId", device_id);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOG.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = DeviceLangTd.DTP_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body using plane java object class.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateDeviceTrackPlayoutsResponseData(String device_id){
        JSONArray response = helper.responseJSONArray(RESPONSES.get(API_CALL));
        if(response.length() > 0){
            List<DeviceTrackPlayout> devicePlayoutList = controller.convertToList(response);
            DeviceTrackPlayoutsPojo deviceTrackPlayouts = new DeviceTrackPlayoutsPojo(devicePlayoutList);

            SoftAssert softAssert = new SoftAssert();
            for(DeviceTrackPlayout deviceTrackPlayout : deviceTrackPlayouts.getDeviceTrackPlayouts()){
                softAssert.assertEquals(deviceTrackPlayout.getTrackId() > 0, true, 
                    "track id invalid for device_id "+device_id+" Url : "+URLS.get(API_CALL));
                softAssert.assertEquals(deviceTrackPlayout.getPlayout() >= 1, true, 
                    "playout invalid for device_id "+device_id+" Url : "+URLS.get(API_CALL));
                softAssert.assertEquals(deviceTrackPlayout.getDeviceDuration() >= 0, true, 
                    "DeviceDuration invalid for device_id "+device_id+" Url : "+URLS.get(API_CALL));
                softAssert.assertEquals(deviceTrackPlayout.getSongDuration() >= 0, true, 
                    "SongDuration invalid for device_id "+device_id+" Url : "+URLS.get(API_CALL));
            }
            softAssert.assertAll();
        }else{
            LOG.warn(this.getClass()+" DeviceTrackPlayouts response is empty for device_id "+device_id+ " URL was : "+URLS.get(API_CALL));
        }

        if(API_CALL == MAX_CALL-1)
            LOG.info("DeviceTrackPlayouts api validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                DeviceLangTd.DTP_DEVICE_IDS[API_CALL]
            }
        };
    }
}