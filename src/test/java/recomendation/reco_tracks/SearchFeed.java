package recomendation.reco_tracks;
import io.qameta.allure.*;
import io.restassured.response.Response;
import logic_controller.SearchFeedController;
import test_data.SearchFeedTd;
import utils.CommonUtils;
import java.util.*;
import org.json.*;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import common.GlobalConfigHandler;
import config.v1.GetProp;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;

/**
 * @author Umesh Shukla
 */

public class SearchFeed {

    String BASEURL = "";
    int API_CALL = 0;
    int MAX_CALL = 0;
    GetProp prop = null;
    SoftAssert softAssert = new SoftAssert();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    SearchFeedController controller = new SearchFeedController();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(SearchFeed.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-41423";
    final static String REPROTING_FEATURE = "Search Feed API end to end validation.";
    
    @BeforeClass
    public void prepareEnv(){
        // GlobalConfigHandler.setLocalProps();
        // baseurl();
        // BASEURL = GlobalConfigHandler.getRecoExecUrl(prop);
        BASEURL = GlobalConfigHandler.baseurl();
        prop = new GetProp();
        MAX_CALL = SearchFeedTd.INVOCATION_COUNT;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
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
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();

        /**
         * previously version check are added now removed so its deprecated.
        if(tab_id.equals(SearchFeedTd.podcast)){
            Map<String, String> headers = RequestHelper.getHeader(0);
            if(GlobalConfigHandler.getDeviceType() == 0){
                headers.replace("gaanaAppVersion", prop.getPodcastAppVersion());
            }
            response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
            // response = rh.createGetRequestWithCustomHeaders(url, headers);
            response.prettyPrint();
        }else{
            response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        }*/

        if(GlobalConfigHandler.getDeviceType() == 1 && tab_id == SearchFeedTd.podcast){
            Map<String, String> headers = RequestHelper.getHeader(0);
            headers.replace("gaanaAppVersion", prop.getIosPodcastAppVersion());
            response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        }else{
            response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        }

        Assert.assertEquals(response != null, true, "response value is null test can't be executed further!");
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            log.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate all five mandatory tab present or not.")
    @Severity(SeverityLevel.BLOCKER)
    public void validateExRequiredTabs(String tab_id, String tab_name){
        boolean result = false;
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            JSONArray tabs = res_object.getJSONArray("tabs");

            int expected_tab_len = 0;
            if(GlobalConfigHandler.getDeviceType() == 1){
                expected_tab_len = SearchFeedTd.tabs.length-2; // in case of ios previos reponse working
            }else{
                expected_tab_len = SearchFeedTd.tabs.length-3; // in case of android recommended rab removed from tabs list
            }

            if(tabs != null && tabs.length() == expected_tab_len){
                result = controller.validateExTabs(tabs);
                Assert.assertEquals(result, true, "for api \n"+URLS.get(API_CALL)+"\nexpected tabs not validated.");
            }else{
                log.error("Tabs never acceptable as null value : Tab Name "+tab_name);
                softAssert.assertEquals((tabs != null) && result, true);
            }
        }else{
            log.error("For "+tab_name+ " api has given null as response body in test case validateExRequiredTabs() Url : \n"+URLS.get(API_CALL));
        }
        if(API_CALL == MAX_CALL-1)
            log.info("validateExRequiredTabs validated successfully : "+result);
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate which tab is selected and value are correct or not!")
    @Severity(SeverityLevel.CRITICAL)
    public void validateSelectedTab(String tab_id, String tab_name){
        boolean isTabValid = false;
        Response response = RESPONSES.get(API_CALL);
        if(!response.asString().equals("null")){
            JSONObject res_object = new JSONObject(response.asString());
            JSONObject tabSelected = res_object.getJSONObject("tabSelected");
            if(tabSelected.length() == 3){
                isTabValid = controller.validateTabSelected(tab_id, tabSelected);
                Assert.assertEquals(isTabValid, true, "for api \n"+URLS.get(API_CALL)+"\nexpected tabSelected not validated.");
            }
        }else{
            log.error("For "+tab_name+ " api has given null as response body in test case validateSelectedTab() Url : \n"+URLS.get(API_CALL));
        }
        if(API_CALL == MAX_CALL-1)
            log.info("validateSelectedTab validated successfully : "+isTabValid);
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
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
            if(API_CALL == MAX_CALL-1)
                log.info("validateEOFPresent validated successfully.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 5, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
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

                case "1389":
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
                if(API_CALL == MAX_CALL-1)
                    log.info("validateResponseBody validated successfully.");
            }
        }else{
            log.error("For "+tab_name+ " api has given null as response body in test case validateResponseBody() Url : \n"+URLS.get(API_CALL));
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 6, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
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
                if(tab_id.equals("default")){
                    tab_name = SearchFeedTd.tabsName(SearchFeedTd.podcast);
                }
                controller.validateSubTitle(tab_name, response_array);
                if(API_CALL == MAX_CALL-1)
                    log.info("validateSubTitleInResponseBody validated successfully.");
            }else{
                log.error("Response array can't be null or empty");
                Assert.assertEquals(response_array.length() > 0, true);
            }
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 7, dataProvider = "dp", invocationCount = SearchFeedTd.INVOCATION_COUNT)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validate Sections Data along with artworks and entitie nodes.")
    @Severity(SeverityLevel.CRITICAL)
    public void ValidateSectionData(String tab_id, String tab_name){
        Response _response = RESPONSES.get(API_CALL);
        JSONObject response = null;
        JSONArray sections = null;
        SoftAssert softAssert = new SoftAssert();
        CommonUtils util = new CommonUtils();
        if(_response != null){
            response = util.converResponseToJSONObject(_response);
        }

        sections = response.getJSONArray("sections");

        if(sections == null)
            softAssert.assertEquals(sections != null, true, "Sections data can't be null, please check response of : \n+"+URLS.get(API_CALL));

        softAssert.assertEquals(sections.length() == 1, true, "Sections length should be equal to 1, check response of : \n+"+URLS.get(API_CALL));

        boolean isBasicsValid = controller.validateSectionBasics(sections.getJSONObject(0));
        softAssert.assertEquals(isBasicsValid, true, "Sections basic validation failed, check response of : \n+"+URLS.get(API_CALL));

        JSONArray entities = sections.getJSONObject(0).getJSONArray("entities");
        if(entities.length() <= 0)
            softAssert.assertEquals(entities.length() <= 0, true, "Sections entities validation can't be processed, check response of : \n+"+URLS.get(API_CALL));

        boolean entityValid = controller.validateSectionsEntity(entities);
        softAssert.assertEquals(entityValid, true, "Sections Entity validation failed, check response of : \n+"+URLS.get(API_CALL));
        softAssert.assertAll();
        if(API_CALL == MAX_CALL-1)
            log.info("Sections tab validated successfully.");
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
