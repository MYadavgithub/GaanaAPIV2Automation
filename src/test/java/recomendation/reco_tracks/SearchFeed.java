package recomendation.reco_tracks;
import config.BaseUrls;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import logic_controller.SearchFeedController;
import test_data.SearchFeedTd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
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

public class SearchFeed extends BaseUrls{

    String BASEURL = "";
    int API_CALL = 0;
    int MAX_CALL = 0;
    SoftAssert softAssert = new SoftAssert();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    SearchFeedController controller = new SearchFeedController();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(SearchFeed.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41423";
    final static String REPROTING_FEATURE = "Search Feed API end to end validation.";
    
    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        MAX_CALL = SearchFeedTd.INVOCATION_COUNT;
    }

    @Test(priority = 1, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response Like, Status code,Response Time, Response Body Validation, Artworks, Expected Tab Lists, eof etc.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in SearchFeedTd file, and get response.")
    @Description("Do not change order or tabs in SearchFeedTd else handle that in API_CALL 4.")
    @Severity(SeverityLevel.BLOCKER)
    public void createSearchFeedGetCall(String tab_id, String tab_name) {
        Response response;
        String url = BASEURL+SearchFeedController.generateSearchFeedUrl(API_CALL,tab_id);
        URLS.add(url);
        if(API_CALL == 4 || tab_name.equals(SearchFeedTd.podcast)){
            Map<String, String> headers = Headers.getHeaders(0);
            headers.replace("gaanaAppVersion", prop.getProperty("podcast_app_version").toString().trim());
            response = request.createGetRequestWithCustomHeaders(url, headers);
        }else{
            response = request.createGetRequest(url);
        }

        Assert.assertEquals(response != null, true, "response value is null test can't be executed further!");
        RESPONSES.put(API_CALL, response);
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(priority = 2, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate all five mandatory tab present or not.")
    @Severity(SeverityLevel.BLOCKER)
    public void validateExRequiredTabs(String tab_id, String tab_name){
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            JSONArray tabs = res_object.getJSONArray("tabs");
            if(tabs != null && tabs.length() == (SearchFeedTd.tabs.length-5)){
                boolean result = controller.validateExTabs(tabs);
                Assert.assertEquals(result, true, "for api \n"+URLS.get(API_CALL)+"\nexpected tabs not validated.");
            }else{
                log.error("Tabs never acceptable as null value!");
                softAssert.assertEquals(tabs != null, true);
            }
        }else{
            log.error("For "+tab_name+ " api has given null as response body in test case validateExRequiredTabs() Url : \n"+URLS.get(API_CALL));
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(priority = 3, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate which tab is selected and value are correct or not!")
    @Severity(SeverityLevel.CRITICAL)
    public void validateSelectedTab(String tab_id, String tab_name){
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            JSONObject tabSelected = res_object.getJSONObject("tabSelected");
            if(tabSelected.length() == 3){
                boolean isTabValid = controller.validateTabSelected(tab_id, tabSelected);
                Assert.assertEquals(isTabValid, true, "for api \n"+URLS.get(API_CALL)+"\nexpected tabSelected not validated.");
            }
        }else{
            log.error("For "+tab_name+ " api has given null as response body in test case validateSelectedTab() Url : \n"+URLS.get(API_CALL));
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(priority = 4, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate eof present or not and value of eof should be 0")
    @Severity(SeverityLevel.MINOR)
    public void validateEOFPresent(String tab_id, String tab_name){
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            int eof = Integer.parseInt(res_object.optString("eof").toString().trim());
            Assert.assertEquals(eof, 0, "for api \n"+URLS.get(API_CALL)+"\nexpected eof value is not correct!");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(priority = 5, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate each object in response JSON object.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateResponseBody(String tab_id, String tab_name) {
        boolean isResponseValidated = false;
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            JSONArray responseBody = res_object.getJSONArray("response");
            switch (SearchFeedTd.tabs[API_CALL]) {
                case "default":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "-1":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "-2":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "-3":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "-4":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "100":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "103":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "104":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "-5":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;

                case "-6":
                    log.info("Going to validate : "+SearchFeedTd.tabs[API_CALL]);
                    isResponseValidated = controller.validateSearchFeedRecommendedResponse(tab_id, responseBody);
                break;
            
                default:
                    log.info("Response not able to validate due to no expected behaviour found !");
                break;
            }
            
            if(!isResponseValidated){
                API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
                Assert.assertEquals(isResponseValidated, true, "for api \n"+URLS.get(API_CALL)+"\nResponse body not validated.");
            }
        }else{
            log.error("For "+tab_name+ " api has given null as response body in test case validateResponseBody() Url : \n"+URLS.get(API_CALL));
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(priority = 6, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate Subtitle Accroding to logic defined by dev team")
    @Severity(SeverityLevel.NORMAL)
    public void validateSubTitleInResponseBody(String tab_id, String tab_name){
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            JSONArray response_array = res_object.getJSONArray("response");
            if(response_array.length() > 0){
                controller.validateSubTitle(tab_name, response_array);
            }else{
                log.error("Response array can't be null or empty");
                Assert.assertEquals(response_array.length() > 0, true);
            }

        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] url(){
        return new Object[][]
        {
            {
                SearchFeedTd.tabs[API_CALL], SearchFeedTd.tabsName(SearchFeedTd.tabs[API_CALL])
            }
        };
    }
}
