package recomendation.album;
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
import io.restassured.response.Response;
import logic_controller.SimilarAlbumController;
import test_data.SimilarAlbumsTd;

public class SimilarAlbums extends BaseUrls {
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    SimilarAlbumController controller = new SimilarAlbumController();
    private static Logger log = LoggerFactory.getLogger(SimilarAlbums.class);
    // final static String JIRA_ID = "https://timesgroup.jira.com/browse/GAANA-43450";
    // final static String REPROTING_FEATURE = "Similar albums Api Validations";

    @BeforeClass
    public void prepareEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("reco_baseurl").toString().trim();
        MAX_CALL = SimilarAlbumsTd.SA_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = SimilarAlbumsTd.SA_INVOCATION)
    public void createSimilarAlbumCall(int album_id) {
        String url = controller.createUrl(BASEURL, album_id);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);

        if(API_CALL == (MAX_CALL)){
            Assert.assertEquals(RESPONSES.size() == URLS.size(), "Error! responses not saved.");
            log.info("All responses captured successfully for further validations.");
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object[][] DataProvider() {
        return new Object[][] { 
            {
                SimilarAlbumsTd.albumIds[API_CALL]
            }
        };
    }
}