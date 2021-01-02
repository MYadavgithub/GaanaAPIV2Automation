package recomendation.v2;
import java.util.Map;
import common.Headers;
import config.Endpoints;
import java.util.ArrayList;
import java.util.HashMap;
import utils.CommonUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.testng.Assert;
import java.util.Map.Entry;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import test_data.MoodMixTd;
import org.testng.annotations.BeforeClass;

public class DeviceConsumedLanguage extends MadeForYou {
    
    ArrayList<String> urls = new ArrayList<>();
    Map<Integer, Response> responses = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(DeviceConsumedLanguage.class);

    @BeforeClass
    public void prepareEnv(){
        prepeareEnv();
    }

    @Test(priority = 1)
    @Feature("https://timesgroup.jira.com/browse/GAANA-41034")
    @Story("To Test Device Consumed API Response Time, Status Code and Response Body.")
    @Description("Genrate url and call api using get method to get complete response for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    public void callDeviceConsumedLanguage(){
        String device_id = prop.getProperty("deviceId").toString().trim();
        String url = BASEURL+Endpoints.deviceConsumedLanguage+device_id;
        urls.add(url);
        Response response = handler.createGetRequest(url);
        if (response != null) {
            responses.put(0, response);
            response = null;
            Map<String, String> headers = Headers.getHeaders(0);
            String new_device_id = CommonUtils.generateRandomDeviceId();
            headers.replace("deviceId", new_device_id);
            String url_2 = BASEURL+Endpoints.deviceConsumedLanguage+new_device_id;
            urls.add(url_2);
            response = handler.createGetRequestWithCustomHeaders(url_2, headers);
            responses.put(1, response);
        }

        if (responses.size() != 2 || responses == null) {
            log.error("Two api call was expected but there is some error Manual check required!");
            Assert.assertEquals(2, responses.size());
        }
    }

    @Test(priority = 2)
    @Feature("https://timesgroup.jira.com/browse/GAANA-41034")
    @Description("Validating language details data with registered and not registred device ids.")
    @Severity(SeverityLevel.NORMAL)
    public void validateDeviceConsumedLanguageResponse(){
        int counter = 0;
        boolean isResponseValidated = true;
        for(Entry<Integer, Response> response : responses.entrySet()){
            JSONObject responseObj = new JSONObject(response.getValue().asString());
            if(counter == 0){
                JSONArray languageDetails = responseObj.getJSONArray("languageDetails");
                if(languageDetails.length() > 0){
                    for(int i = 0; i<languageDetails.length(); i++){
                        JSONObject language = languageDetails.getJSONObject(i);
                        boolean isKeyValidated = validatekeys(MoodMixTd.exKeysDeviceConsumedLanguage(), helper.keys(language));
                        if(isKeyValidated){
                            int id = Integer.parseInt(language.optString("id").toString().trim());
                            String language_name = language.optString("language").toString().trim();
                            Assert.assertEquals(true, language_name.length() > 0, "Language name validation got failed!");
                            Assert.assertEquals(true, id > 0, "Language id can't be less than zero!");
                        }else{
                            isResponseValidated = false;
                            log.error("Language keys was not matching with expected values!");
                        }
                    }
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
            counter++;
        }
        Assert.assertEquals(true, isResponseValidated, "DeviceConsumedLanguage response body validation got failed!");
    }
}