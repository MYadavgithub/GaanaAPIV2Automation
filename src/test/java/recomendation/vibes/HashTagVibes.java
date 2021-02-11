package recomendation.vibes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import config.BaseUrls;
import utils.CommonUtils;
import io.restassured.response.Response;
import logic_controller.HashTagVibesController;
import test_data.TrendingTrackTd;

/**
 * @author Umesh Shukla
 */

public class HashTagVibes extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_COUNT_VALID = false;
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    RequestHandler request = new RequestHandler();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    HashTagVibesController controller = new HashTagVibesController();
    private static Logger log = LoggerFactory.getLogger(HashTagVibes.class);
    
    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        MAX_CALL = TrendingTrackTd.HV_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = TrendingTrackTd.HV_INVOCATION)
    public void createGetRequestSimilarArtist(String hashtag_name){
        String url = controller.prepareUrl(BASEURL, hashtag_name);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == (MAX_CALL-1)){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                TrendingTrackTd.HASHTAGS[API_CALL]
            }
        };
    }

}
