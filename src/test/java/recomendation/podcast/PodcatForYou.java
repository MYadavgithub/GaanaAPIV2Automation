package recomendation.podcast;
import config.BaseUrls;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.PodcastConroller;
import test_data.PodcastTd;
import utils.CommonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import common.GlobalConfigHandler;
import common.Headers;
import common.RequestHandler;

/**
 * @author Umesh Shukla
 */
public class PodcatForYou extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    CommonUtils utils = new CommonUtils();
    ArrayList<String> urls = new ArrayList<>();
    RequestHandler handler = new RequestHandler();
    PodcastConroller controller = new PodcastConroller();
    GlobalConfigHandler ghandler = new GlobalConfigHandler();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(PodcatForYou.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-42185";
    final static String REPROTING_FEATURE = "Podcast for You api content validations.";

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        MAX_CALL = PodcastTd.deviceIds.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = PodcastTd.PODCAST_FY_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation, Artworks.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in PodcastTd file, and get response.")
    @Severity(SeverityLevel.NORMAL)
    public void createPodcastForYouCall(String device_id){
        device_id = utils.createUrlEncodedStr(device_id);
        String baseurl = prop.getProperty("reco_baseurl");
        String url = controller.prepareUrlPFY(baseurl, device_id);
        urls.add(url);        
        Map<String, String> headers = Headers.getHeaders(0);
        headers.replace("deviceId", device_id);
        Response response = handler.createGetRequestWithCustomHeaders(url, headers);
        RESPONSES.put(API_CALL, response);
        API_CALL = ghandler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = PodcastTd.PODCAST_FY_INVOCATION)
    @Feature(REPROTING_FEATURE)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Step("Validating Response data for device id {0}")
    @Severity(SeverityLevel.NORMAL)
    public void validateResponsePodcastForYou(String device_id){
        SoftAssert softAssert = new SoftAssert();
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        
        int status = Integer.parseInt(response.optString("status").toString().trim());
        softAssert.assertEquals(status, 1, "Status should be 1 but got "+status);

        int user_token_status = Integer.parseInt(response.optString("user-token-status").toString().trim());
        softAssert.assertEquals(user_token_status, 1, "user_token_status should be 1 but got "+user_token_status);

        int count = Integer.parseInt(response.getString("count").toString().trim());
        softAssert.assertEquals(count, 14, "count should be 14 but got "+count);

        boolean showIdValid = false;
        JSONArray shows = response.getJSONArray("shows");
        
        for(int i = 0; i<shows.length(); i++){
            int show_id = Integer.parseInt(shows.optString(i).toString().trim());
            if(show_id > 0){
                showIdValid = true;
            }else{
                showIdValid = false;
                log.error("Show id can't be zero or less than zero, url was : "+urls.get(API_CALL));
                break;
            }
        }

        softAssert.assertEquals(showIdValid, true, "Show ids not validated!");
        softAssert.assertAll();
        API_CALL = ghandler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                PodcastTd.deviceIds[API_CALL]
            }
        };
    }
}