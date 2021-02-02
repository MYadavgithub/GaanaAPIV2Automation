package recomendation.artist;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.BaseUrls;
import io.restassured.response.Response;
import logic_controller.ArtistController;
import test_data.ArtistTd;
import utils.CommonUtils;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.RequestHandler;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
 * @author Umesh Shukla
 */

public class SimilarArtists extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    boolean IS_COUNT_VALID = false;
    CommonUtils utils = new CommonUtils();
    ArrayList<String> URLS = new ArrayList<>();
    RequestHandler request = new RequestHandler();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    ArtistController controller = new ArtistController();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(SimilarArtists.class);

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("prec_baseurl").toString().trim();
        MAX_CALL = ArtistTd.artistIds.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = ArtistTd.INVOCATION)
    public void createGetRequestSimilarArtist(int artist_id){
        String url = controller.prepareUrl(BASEURL, artist_id);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == (MAX_CALL-1)){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = ArtistTd.INVOCATION)
    public void validateArtistCountInResponses(int artist_id){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray artist = response.getJSONArray("artist");
        int count = Integer.parseInt(response.optString("count").toString().trim());
        Assert.assertEquals(artist.length(), count, "Error! Api count value not matching with received Artits Objects!");
        IS_COUNT_VALID = true;
        if(API_CALL == (MAX_CALL-1) && IS_COUNT_VALID){
            log.info("All responses api object count and self artist object count validated successfully.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = ArtistTd.INVOCATION)
    public void validateArtistsData(int artist_id){
        JSONObject response = utils.converResponseToJSONObject(RESPONSES.get(API_CALL));
        JSONArray artist = response.getJSONArray("artist");
        boolean isArtistDataValid = controller.validateArtistData( URLS.get(API_CALL), artist);
        Assert.assertEquals(isArtistDataValid, true, "Error! Artist Data validation failed!");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                ArtistTd.artistIds[API_CALL]
            }
        };
    }
}
