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
import logic_controller.DeviceLanguageController;

/**
 * @author Umesh.Shukla
 */
public class DeviceLanguage {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    DeviceLanguageController controller = new DeviceLanguageController();
    private static Logger LOG = LoggerFactory.getLogger(DeviceLanguage.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45473";
    final static String REPROTING_FEATURE = "DeviceLanguage content validations.";

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
    public void createDeviceLanguageCall(String device_id) {
        device_id = utils.createUrlEncodedStr(device_id);
        String url = BASEURL+Endpoints.DEVICE_LANGUAGE+device_id;
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

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = DeviceLangTd.E_D_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body using plane java object class.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateTagaffinity30daysResponseData(String device_id){
        JSONObject response_object = helper.responseJSONObject(RESPONSES.get(API_CALL));
        try{
            JSONArray languageArray = response_object.getJSONArray("languageDetails");
            List<LanguageEntity> langEntity = controller.convertJSONArrayToList(languageArray);
            LanguageDetails languageDetails = new LanguageDetails(langEntity);

            int languge_size = languageDetails.getLanguageDetails().size();
            for(int i = 0; i<languge_size; i++){
                LanguageEntity language = languageDetails.getLanguageDetails().get(i);
                if(language.getId() <= 0){
                    LOG.error(this.getClass()+" languge id is not valid, please check for device_id : "+device_id+ " Url was "+URLS.get(API_CALL));
                }

                if(language.getLanguage().length() <= 0){
                    LOG.error(this.getClass()+" languge name is not valid, please check for device_id : "+device_id+ " Url was "+URLS.get(API_CALL));
                }

                if(language.getWeight() > 0 && language.getWeight() < 0){
                    LOG.error(this.getClass()+" languge weight is not valid, please check for device_id : "+device_id+ " Url was "+URLS.get(API_CALL));
                }
            }
        }catch(Exception e){
            // e.printStackTrace();
            Assert.assertEquals(response_object.get("languageDetails").toString().equals("null"), true);
            LOG.warn(this.getClass()+" languge details got null for device id "+device_id+ " Url was "+URLS.get(API_CALL));
        }
        if(API_CALL == MAX_CALL-1)
            LOG.info("DeviceLanguage api validated successfully.");
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