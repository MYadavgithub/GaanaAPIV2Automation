package recomendation.album;
import java.util.*;
import org.slf4j.*;
import pojo.*;
import org.testng.Assert;
import org.testng.annotations.*;
import io.qameta.allure.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import common.Helper;
import test_data.SimilarAlbumsTd;
import utils.CommonUtils;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.response.Response;

/**
 * @author Umesh.Shukla
 */
public class SimilarTopShow {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger LOG = LoggerFactory.getLogger(SimilarTopShow.class);
    final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-45478";
    final static String REPROTING_FEATURE = "SimilarTopShow content validations.";

    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = SimilarAlbumsTd.STS_SHOW_IDS.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = SimilarAlbumsTd.STS_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Story("Need to validate over-all API response, Status code, Response Time, Response Body Validation.")
    @Feature(REPROTING_FEATURE)
    @Step("Prepare Urls for all requests which listed in SimilarAlbumsTd file, and get response.")
    @Severity(SeverityLevel.BLOCKER)
    public void createSimilarTopShowReq(int show_id) {
        String url = BASEURL+Endpoints.SIMILAR_TOP_SHOW+show_id;
        URLS.add(url);
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOG.info(this.getClass()+" All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = SimilarAlbumsTd.STS_INVOCATION)
    @Link(name =  "Jira Task Id", value = JIRA_ID)
    @Feature(REPROTING_FEATURE)
    @Step("Validating response body using plane java object class.")
    @Severity(SeverityLevel.CRITICAL)
    public void ValidateResponseBody(int show_id){
        Response response = RESPONSES.get(API_CALL);
        try{
            SimilarTopShowPojo similarTopShow  = response.getBody().as(SimilarTopShowPojo.class);

            if(similarTopShow.getUserTokenStatus() == 1 && similarTopShow.getStatus() == 1){
               
                if(similarTopShow.getShows() == null){
                    Assert.assertEquals(Integer.parseInt(similarTopShow.getCount()), 0);
                    LOG.warn(this.getClass()+" Shows list are null for show_id "+show_id+ " URL was : "+URLS.get(API_CALL));
                }else{
                    boolean countAndShowCountValid = (Integer.parseInt(similarTopShow.getCount()) == similarTopShow.getShows().length);
                    Assert.assertEquals(countAndShowCountValid, true, this.getClass()+" Count and show count not matching...");

                    for(int show : similarTopShow.getShows()){
                        if(show <= 0){
                            LOG.error(this.getClass()+" invalid show found for "+show_id+ " URL was : "+URLS.get(API_CALL));
                            Assert.assertEquals(show <= 0, false);
                        }
                    }
                }
            }else{
                Assert.assertEquals(similarTopShow.getUserTokenStatus(), 1, "user-token-status invalid...");
                Assert.assertEquals(similarTopShow.getStatus(), 1, "status invalid...");
            }

        }catch(Exception e){
            e.printStackTrace();
            LOG.error(this.getClass()+" Can not generate plain java object from response object...", response.asString());
        }
        if(API_CALL == MAX_CALL-1)
            LOG.info("SimilarTopShow api validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                SimilarAlbumsTd.STS_SHOW_IDS[API_CALL]
            }
        };
    }
}