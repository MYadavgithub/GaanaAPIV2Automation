package recomendation.v2;
import config.BaseUrls;
import config.Endpoints;
import java.util.List;
import java.util.Map;
import common.Headers;
import common.Helper;
import org.slf4j.Logger;
import org.testng.Assert;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import utils.CommonUtils;
import org.json.JSONObject;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import test_data.MoodMixTd;

public class MadeForYou extends BaseUrls {

    int counter = 0;
    String URL = "";
    String BASEURL = "";
    String NEW_DEVICE_ID = "";
    Helper helper = new Helper();
    ArrayList<String> urls = new ArrayList<>();
    RequestHandler handler = new RequestHandler();
    Map<Integer, Response> responses = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(MadeForYou.class);
    final static String REPROTING_FEATURE = "Made For You api response validations";

    @BeforeClass
    public void prepeareEnv() {
        System.setProperty("env", "prod");
        System.setProperty("type", "reco");
        System.setProperty("device_type", "android");
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        URL = BASEURL + Endpoints.madeForYou;
        NEW_DEVICE_ID = CommonUtils.generateRandomDeviceId();
    }

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] {
            { URL, counter }
        };
    }

    @Test(priority = 1, dataProvider = "urlProvider", invocationCount = MoodMixTd.DEVICE_CONSUMED_INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Story("Validate response time, status code, response body, track type, track-ids present or not, and artworks.")
    @Description("Once response got we will store results in to map for further validations")
    @Step("First call get registered device id and second call will hold new device id, save response for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    public void createCallToMadeForYou(String url, int count) {
        Response response;
        if(counter == 0){
            response = handler.createGetRequest(url);
            responses.put(counter, response);
            counter++;
        }else if(counter == 1){
            Map<String, String> headers = Headers.getHeaders(0);
            headers.replace("deviceId", NEW_DEVICE_ID);
            response = handler.createGetRequestWithCustomHeaders(url, headers);
                if (response != null)
                    responses.put(counter, response);

            if (responses.size() != 2 || responses == null) {
                log.error("Two api call was expected but there is some error Manual check required!");
                Assert.assertEquals(2, responses.size());
            }
            counter = 0;
        }
    }

    @Test(priority = 2, dataProvider = "urlProvider", invocationCount = MoodMixTd.DEVICE_CONSUMED_INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Description("Validating response on basis of registred and not registred devices.")
    @Severity(SeverityLevel.NORMAL)
    public void validateMadeForYouResponseBody(String url, int count){
        boolean isMadeForYouValidated = false;
        SoftAssert softAssert = new SoftAssert();
        List<String> expectedUsers = MoodMixTd.expectedUserType();
        JSONObject responseObject = new JSONObject(responses.get(counter).asString());
        String user_type = responseObject.optString("userType").toString().trim();
        if(!expectedUsers.contains(user_type)){
            log.error("User Type value unexpected in response body! Value got for user type is : "+user_type);
            softAssert.assertEquals(true, expectedUsers.contains(user_type));
        }
        JSONArray MadeForYouArr = responseObject.getJSONArray("vplMix");
        isMadeForYouValidated =  validateMadeForYouDetails(counter, MadeForYouArr);

        if(!isMadeForYouValidated){
            log.error("Response body validation broken for Made For You api!");
            Assert.assertEquals(true, isMadeForYouValidated);
        }else{
            counter++;
        }
    }
    
    @Step("Validating made for you response array on basis of response data : {1}")
    private boolean validateMadeForYouDetails(int counter, JSONArray madeForYouArr) {
        boolean isDatavalidated = false;
        ArrayList<String> artworks = new ArrayList<>();
        if(madeForYouArr.length() > 0){
            Iterator<Object> itr = madeForYouArr.iterator();
            while(itr.hasNext()){
                JSONObject madeForYou = (JSONObject) itr.next();
                List<Object> keys = helper.keys(madeForYou);
                List<String> expectedKeys = MoodMixTd.expectedMadeForYouKeys();
                boolean isKeysValidated = validatekeys(expectedKeys, keys);
                Assert.assertEquals(true, isKeysValidated);
                
                artworks.add(madeForYou.optString("backgroundArtworkUrl").toString().trim());
                String mixType = madeForYou.optString("mixType").toString().trim();
                String title = madeForYou.optString("title").toString().trim();
                String textColorCode = madeForYou.optString("textColorCode").toString().trim();
                int track_type = Integer.parseInt(madeForYou.getString("trackType").toString().trim());
                int artworkTemplateId = Integer.parseInt(madeForYou.optString("artworkTemplateId").toString().trim());

                if(mixType.length() <= 0 && title.length() <= 0 && textColorCode.length() <= 0 && track_type < 0 && artworkTemplateId < 0){
                    log.error("In response Mixtype, title, textColorCode, trackType or artworkTemplateId is present with worng value, manual check required!");
                    Assert.assertEquals(true, false);
                }

                boolean trackIdValidated = true;
                JSONArray madeForYouTracks = madeForYou.getJSONArray("trackIds");
                for(int i = 0; i<madeForYouTracks.length(); i++){
                    if(Double.parseDouble(madeForYouTracks.optString(i).toString().trim()) <= 0){
                        trackIdValidated = false;
                    }
                }
                Assert.assertEquals(true, trackIdValidated);
                isDatavalidated = true;
            }
        
            if(artworks.size() > 0 && isDatavalidated){
                isDatavalidated = helper.validateActiveLinks(artworks);
            }
        }
        return isDatavalidated;
    }

    @Step("Validating received keys along-with expected keys respectivey data are : {0} {1}")
    public static boolean validatekeys(List<String> expectedKeys, List<Object> keys) {
        boolean isKeyValidated = false;
        if(expectedKeys.size() == keys.size()){
            for(Object key : keys) {
                if(!expectedKeys.contains(key.toString().trim())){
                    isKeyValidated = false;
                    log.error("Key value is unexpected : "+key);
                    break;
                }else{
                    isKeyValidated = true;
                }
            }
        }
        return isKeyValidated;
    }
}