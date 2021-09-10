package recomendation.device_language;
import java.util.*;
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
public class AssociatedLanguage {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    DeviceLanguageController controller = new DeviceLanguageController();
    private static Logger LOG = LoggerFactory.getLogger(AssociatedLanguage.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45480";
    final static String REPROTING_FEATURE = "AssociatedLanguage content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = DeviceLangTd.LanguagesList().size();
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = DeviceLangTd.AL_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in DeviceTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createAssociatedLanguagesReq(String languges) {
        String url = BASEURL+Endpoints.ASSOCIATED_LANGUAGES+languges;
        URLS.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOG.info("All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = DeviceLangTd.AL_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body using plane java object class.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateSearchedLanguageVsResponseLanguageValue(String languges){
        try{
            Response response = RESPONSES.get(API_CALL);
            if(helper.responseJSONArray(response).length() > 0){
                List<AssociatedLanguages> prepareListFromReponse = controller.responseArrayToList(helper.responseJSONArray(response));
                AssociatedLanguagesPojo associatedLanguages = new AssociatedLanguagesPojo(prepareListFromReponse);
                List<AssociatedLanguages> lang = associatedLanguages.getAssociatedLanguages();
                if(lang.size() > 0){
                    for(AssociatedLanguages asLanguage : lang){
                        boolean isLangPresent = controller.validateSearchedLangPresentInResponse(languges, asLanguage);
                        if(!isLangPresent){
                            LOG.error(this.getClass()+" searched language not present in response data, Url was : "+URLS.get(API_CALL));
                            Assert.assertEquals(isLangPresent, true);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(API_CALL == MAX_CALL-1)
            LOG.info("Response Language compared with searched language successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = DeviceLangTd.AL_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body using plane java object class.")
    @Severity(SeverityLevel.CRITICAL)
    public void validateAssociatedLanguages(String languges){
        try{
            Response response = RESPONSES.get(API_CALL);
            if(helper.responseJSONArray(response).length() > 0){
                List<AssociatedLanguages> prepareListFromReponse = controller.responseArrayToList(helper.responseJSONArray(response));
                AssociatedLanguagesPojo associatedLanguages = new AssociatedLanguagesPojo(prepareListFromReponse);
                List<AssociatedLanguages> lang = associatedLanguages.getAssociatedLanguages();
                if(lang.size() > 0){
                    for(AssociatedLanguages asLanguage : lang){
                        List<AssociatedLanguageEntity> associated_languages = asLanguage.getLanguageEntity();
                        boolean isAssociatedLanguageValid = controller.validateAssociatedLanguage(asLanguage.getLanguage(), associated_languages);
                        if(!isAssociatedLanguageValid){
                            LOG.error(this.getClass()+" associated languages not valid in response data, Url was : "+URLS.get(API_CALL));
                            Assert.assertEquals(isAssociatedLanguageValid, true);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(API_CALL == MAX_CALL-1)
            LOG.info("AssociatedLanguages validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                DeviceLangTd.LanguagesList().get(API_CALL)
            }
        };
    }
}