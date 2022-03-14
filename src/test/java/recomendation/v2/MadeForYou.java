package recomendation.v2;
import config.BaseUrls;
import config.Endpoints;
import java.util.Map;
import common.GlobalConfigHandler;
import common.Headers;
import common.Helper;
import org.slf4j.Logger;
import org.testng.Assert;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import utils.CommonUtils;
import org.json.JSONObject;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.MadeForYouController;
import test_data.MixTd;

/**
 * @author umesh shukla
 */
public class MadeForYou extends BaseUrls {

    int counter = 0;
    String URL = "";
    String NEW_DEVICE_ID = "";
    Helper helper = new Helper();
    ArrayList<String> urls = new ArrayList<>();
    RequestHandler handler = new RequestHandler();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    MadeForYouController controller = new MadeForYouController();
    private static Logger log = LoggerFactory.getLogger(MadeForYou.class);
    final static String REPROTING_FEATURE = "Made For You api response validations";

    @BeforeClass
    public void prepeareEnv() {
        GlobalConfigHandler.setLocalProps();
        baseurl();
        String baseUrl = prop.getProperty("reco_baseurl").toString().trim();
        URL = baseUrl + Endpoints.madeForYou;
    }

    @Test(enabled = true, priority = 1, dataProvider = "urlProvider", invocationCount = MixTd.MADE_FOR_YOU_INVOCATION)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Story("Validate response time, status code, response body, track type, track-ids present or not, and artworks.")
    @Description("Once response got we will store results in to map for further validations")
    @Step("First call get registered device id and second call will hold new device id, save response for further validations.")
    @Severity(SeverityLevel.BLOCKER)
    public void createCallToMadeForYou(String url, int count) {
        Response response;
        if(counter <= 1){
            Map<String, String> headers = Headers.getHeaders(0, null);
            headers.replace("deviceId", MixTd.device_ids[counter]);
            response = handler.createGetRequestWithCustomHeaders(url, headers);
            RESPONSES.put(counter, response);
        }else if(counter > 1){
            Map<String, String> headers = Headers.getHeaders(0, null);
            headers.replace("deviceId", MixTd.device_ids[counter]);
            if(counter == 3){
                NEW_DEVICE_ID = CommonUtils.generateRandomDeviceId();
                headers.replace("deviceId", NEW_DEVICE_ID);
            }

            response = handler.createGetRequestWithCustomHeaders(url, headers);
            RESPONSES.put(counter, response);
        }

        if(counter == 3){
            if(RESPONSES.size() != 4 || RESPONSES == null) {
                log.error("Responses not captured successfully, please check manually!");
                Assert.assertEquals(2, RESPONSES.size());
            }
            counter = 0;
        }else{
            counter++;
        }
    }

    @Test(enabled = true, priority = 2, dataProvider = "urlProvider", invocationCount = MixTd.MADE_FOR_YOU_INVOCATION)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Description("Validate user type values")
    @Severity(SeverityLevel.NORMAL)
    public void validateUsertype(String url, int count){
        JSONObject responseObject = new JSONObject(RESPONSES.get(counter).asString());
        String user_type = responseObject.optString("userType").toString().trim();
        if(counter <= 1 && user_type.equalsIgnoreCase(MixTd.expectedUserType[0])){
            log.info("Device id : "+MixTd.device_ids[counter]+ " is registered user.");
        }else if(counter > 1 && user_type.equalsIgnoreCase(MixTd.expectedUserType[1])){
            if(counter == 3){
                log.info("Device id : "+NEW_DEVICE_ID+ " is new user.");
            }else{
                log.info("Device id : "+MixTd.device_ids[counter]+ " is new user.");
            }
        }else{
            log.error("User Type value unexpected in response body! Value got for user type is : "+user_type);
        }

        counter = controller.resetCounter(counter);
    }

    @Test(enabled = true, priority = 3, dataProvider = "urlProvider", invocationCount = MixTd.MADE_FOR_YOU_INVOCATION)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Description("Validating response on basis of registred and not registred devices.")
    @Severity(SeverityLevel.NORMAL)
    public void validateCommonDetails(String url, int count){
        int flag = 0;
        boolean isMadeForYouValidated = false;
        // SoftAssert softAssert = new SoftAssert();
        JSONObject responseObject = new JSONObject(RESPONSES.get(counter).asString());
        String user_type = responseObject.optString("userType").toString().trim();
        JSONArray response = responseObject.getJSONArray("vplMix");

        if(counter <= 3){
            isMadeForYouValidated = controller.genricVplValidation(flag, user_type, response, MixTd.device_ids[counter]);
        }else {
            isMadeForYouValidated = controller.genricVplValidation(flag, user_type, response,MixTd.device_ids[counter] );
        }
        //NEW_DEVICE_ID

        if(!isMadeForYouValidated){
            log.error("Response body validation broken for Made For You api!");
            Assert.assertEquals(true, isMadeForYouValidated);
        }else{
            counter = controller.resetCounter(counter);
        }

        /* Old code
        isMadeForYouValidated =  validateMadeForYouDetails(counter, MadeForYouArr);

        if(!isMadeForYouValidated){
            log.error("Response body validation broken for Made For You api!");
            Assert.assertEquals(true, isMadeForYouValidated);
        }else{
            counter++;
        }*/
    }

    @Test(enabled = true, priority = 4, dataProvider = "urlProvider", invocationCount = MixTd.MADE_FOR_YOU_INVOCATION)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Description("Validating trackIds and trackType for user type in response.")
    @Severity(SeverityLevel.BLOCKER)
    public void validateTrackIdsAndTrackType(String url, int count){
        int flag = 1;
        boolean isTrackIdsValid = false;
        JSONObject responseObject = new JSONObject(RESPONSES.get(counter).asString());
        String user_type = responseObject.optString("userType").toString().trim();
        JSONArray response = responseObject.getJSONArray("vplMix");

        if(counter <= 2){
            isTrackIdsValid = controller.genricVplValidation(flag, user_type, response, MixTd.device_ids[counter]);
        }else {
            isTrackIdsValid = controller.genricVplValidation(flag, user_type, response, NEW_DEVICE_ID);
        }

        if(!isTrackIdsValid){
            log.error("Track ids validation broken for Made For You api!");
            Assert.assertEquals(true, isTrackIdsValid);
        }else{
            counter = controller.resetCounter(counter);
        }
    }

    @Test(enabled = true, priority = 5, dataProvider = "urlProvider", invocationCount = MixTd.MADE_FOR_YOU_INVOCATION)
    @Link(name =  "Jira Task Id", value = "https://timesgroup.jira.com/browse/GAANA-41033")
    @Feature(REPROTING_FEATURE)
    @Description("Validating sourceId for Artist and Tag in response.")
    @Severity(SeverityLevel.BLOCKER)
    public void validateSourceId(String url, int count){
        int flag = 2;
        boolean isSourceIdValid = false;
        JSONObject responseObject = new JSONObject(RESPONSES.get(counter).asString());
        String user_type = responseObject.optString("userType").toString().trim();
        JSONArray response = responseObject.getJSONArray("vplMix");

        if(counter <= 2){
            isSourceIdValid = controller.genricVplValidation(flag, user_type, response, MixTd.device_ids[counter]);
        }else {
            isSourceIdValid = controller.genricVplValidation(flag, user_type, response, NEW_DEVICE_ID);
        }

        if(!isSourceIdValid){
            log.error("SourceId validation broken for Made For You api!");
            Assert.assertEquals(true, isSourceIdValid);
        }else{
            counter = controller.resetCounter(counter);
        }
    }

    /*@Step("Validating made for you response array on basis of response data : {1}")
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
    }*/

    @DataProvider(name = "urlProvider")
    public Object[][] DataProvider() {
        return new Object[][] {
            { URL , counter }
        };
    }
}