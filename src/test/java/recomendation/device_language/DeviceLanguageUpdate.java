package recomendation.device_language;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.json.*;
import org.slf4j.*;
import pojo.*;
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
import logic_controller.DeviceLanguageController;

/**
 * @author Umesh.Shukla
 */
public class DeviceLanguageUpdate {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    DeviceLanguageController controller = new DeviceLanguageController();
    private static Logger LOG = LoggerFactory.getLogger(DeviceLanguageUpdate.class);
    final static String JIRA_ID = "";
    final static String REPROTING_FEATURE = "DeviceLanguageUpdate api content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = DeviceLangTd.languagesUpdateList().size();
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = DeviceLangTd.DLU_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in DeviceTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createDeviceLanguageUpdateReq(String languges) {
        Map<String, String> headers = RequestHelper.getHeader(0);
        String device_id = headers.get("deviceId");
        String url = BASEURL+Endpoints.DEVICE_LANGUAGE_UPDATE+device_id+"&language="+languges;
        URLS.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        String _deviceId = response.asString();
        
        if(!StringUtils.equals(_deviceId, device_id)){
            LOG.error(this.getClass()+ " Update request device id not matched with response device id...");
            Assert.assertEquals(_deviceId.equals(device_id), true);
        }

        String updated_language_check_url = BASEURL+Endpoints.DEVICE_LANGUAGE+device_id;
        response = request.executeRequestAndGetResponse(updated_language_check_url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOG.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = DeviceLangTd.DLU_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Detailed validation for DeviceLanguageUpdate response data.")
    @Feature(REPROTING_FEATURE)
    @Severity(SeverityLevel.BLOCKER)
    public void validateDeviceLanguageUpdate(String languges) {
        JSONObject response_object = helper.responseJSONObject(RESPONSES.get(API_CALL));
        try{
            JSONArray languageArray = response_object.getJSONArray("languageDetails");
            List<LanguageEntity> langEntity = controller.convertJSONArrayToList(languageArray);
            LanguageDetails languageDetails = new LanguageDetails(langEntity);

            int languge_size = languageDetails.getLanguageDetails().size();
            Assert.assertEquals(languge_size > 0, true);

            boolean isResponseValid = controller.validateUpdatedLanguageWithAssociatedLanguage(languges, languageDetails);
            
            if(!isResponseValid){
                LOG.error(this.getClass()+ " DeviceLanguage api response is not valid Url was : "+URLS.get(API_CALL));
                Assert.assertEquals(isResponseValid, true);
            }
        }catch(Exception e){
            // e.printStackTrace();
            Assert.assertEquals(response_object.get("languageDetails").toString().equals("null"), true);
            LOG.warn(this.getClass()+" language update request response got null for language :"+languges+ " Url was "+URLS.get(API_CALL));
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                DeviceLangTd.languagesUpdateList().get(API_CALL)
            }
        };
    }
}