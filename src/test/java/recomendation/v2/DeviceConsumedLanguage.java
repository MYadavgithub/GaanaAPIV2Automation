package recomendation.v2;
import java.util.Map;
import common.Headers;
import common.Helper;
import common.RequestHandler;
import config.BaseUrls;
import config.Endpoints;
import java.util.ArrayList;
import java.util.HashMap;
import utils.CommonUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.testng.Assert;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import test_data.MoodMixTd;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

public class DeviceConsumedLanguage extends BaseUrls {
    
    int counter = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    ArrayList<String> urls = new ArrayList<>();
    RequestHandler handler = new RequestHandler();
    Map<String, String> customHeaders = new HashMap<>();
    Map<Integer, Response> responses = new HashMap<>();
    final static String REPROTING_FEATURE = "Device Consumed Language api response validations";
    private static Logger log = LoggerFactory.getLogger(DeviceConsumedLanguage.class);

    @BeforeClass
    public void prepareEnv(){
        // System.setProperty("env", "prod");
        // System.setProperty("type", "reco");
        // System.setProperty("device_type", "android");
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        createUrls();
    }

    @Test(priority = 1, dataProvider = "urlProvider", invocationCount = MoodMixTd.DEVICE_CONSUMED_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = "https://timesgroup.jira.com/browse/GAANA-41034")
    @Feature(REPROTING_FEATURE)
    @Story("Validate response time, status code, response body mainly lanuguage and language_id")
    @Description("Genrate url and call api using get method to get complete response for further validations.")
    @Step("First call get registered device id and second call will hold new device id, save response for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    public void callDeviceConsumedLanguage(String url){
        Response response;
        if(counter == 0){
            response = handler.createGetRequest(url);
            responses.put(counter, response);
            counter++;
        }else if(counter == 1){
            response = handler.createGetRequestWithCustomHeaders(url, customHeaders);
            responses.put(counter, response);
            if (responses.size() != 2 || responses == null) {
                log.error("Two api call was expected but there is some error Manual check required!");
                Assert.assertEquals(2, responses.size());
            }
            counter = 0;
        }
    }

    @Test(priority = 2, dataProvider = "urlProvider", invocationCount = MoodMixTd.DEVICE_CONSUMED_INVOCATION_COUNT)
    @Link(name = "Jira Id", url = "https://timesgroup.jira.com/browse/GAANA-41034")
    @Feature(REPROTING_FEATURE)
    @Description("Validating language details data with registered and not registred device ids.")
    @Severity(SeverityLevel.NORMAL)
    public void validateDeviceConsumedLanguageResponse(String url){
        boolean isResponseValidated = true;
        JSONObject responseObj =  new JSONObject(responses.get(counter).asString());
        if(counter == 0){
            JSONArray languageDetails = responseObj.getJSONArray("languageDetails");
            if(languageDetails.length() > 0){
                for(int i = 0; i<languageDetails.length(); i++){
                    JSONObject language = languageDetails.getJSONObject(i);
                    boolean isKeyValidated = MadeForYou.validatekeys(MoodMixTd.exKeysDeviceConsumedLanguage(), helper.keys(language));
                    if(isKeyValidated){
                        int id = Integer.parseInt(language.optString("id").toString().trim());
                        String language_name = language.optString("language").toString().trim();
                        Assert.assertEquals(true, language_name.length() > 0, "Language name validation got failed!");
                        Assert.assertEquals(true, id > 0, "Language id can't be less than zero!");
                    }else{
                        isResponseValidated = false;
                        log.error("Language keys was not matching with expected values! : "+language);
                    }
                }
                counter++;
            }else{
                isResponseValidated = false;
                log.error("For registered user language api response is not valid please manually check for url : "+urls.get(counter));
                Assert.assertTrue(languageDetails.length() > 0);
            }
        }else if(counter == 1){
            if(responseObj.optString("languageDetails").toString().length() != 0){
                isResponseValidated = false;
            }
        }else{
            isResponseValidated = false;
        }
        Assert.assertEquals(true, isResponseValidated, "DeviceConsumedLanguage response body validation got failed!");
    }

    private void createUrls(){
        for(int i = 0; i<MoodMixTd.DEVICE_CONSUMED_INVOCATION_COUNT; i++){
            if(i == 0){
                String device_id = prop.getProperty("deviceId").toString().trim();
                String url = BASEURL+Endpoints.deviceConsumedLanguage+device_id;
                urls.add(url);
            }else {
                customHeaders = Headers.getHeaders(0);
                String new_device_id = CommonUtils.generateRandomDeviceId();
                customHeaders.replace("deviceId", new_device_id);
                String url = BASEURL+Endpoints.deviceConsumedLanguage+new_device_id;
                urls.add(url);
            }
        }
    }

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] {
            {
                urls.get(counter)
            }
        };
    }
}