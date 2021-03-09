package recomendation.reco_tracks;
import config.BaseUrls;
import io.restassured.response.Response;
import logic_controller.RecoTrackController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import common.GlobalConfigHandler;
import common.RequestHandler;
import test_data.RecomendedTrackTd;

public class RecommendedTrackIds extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    RequestHandler request = new RequestHandler();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    RecoTrackController controller = new RecoTrackController();
    private static Logger log = LoggerFactory.getLogger(RecomendedTracks.class);

    @BeforeTest
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        BASEURL = baseurl();
        MAX_CALL = RecomendedTrackTd.track_ids.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = RecomendedTrackTd.T_IDS_INVOCATION)
    public void createRecommendedTrackIdsCall(String track_id){
        String url = controller.prepRecoTrackIdsUrl(BASEURL, track_id);
        URLS.add(url);
        Response response = request.createGetRequest(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            log.info("ALl response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = RecomendedTrackTd.T_IDS_INVOCATION)
    public void validateRecommendedTrackResponseData(String track_id){
        SoftAssert softAssert = new SoftAssert();
        Response response = RESPONSES.get(API_CALL);
        JSONArray response_arr =  new JSONArray(response.asString());
        softAssert.assertEquals(response_arr.length() > 0, true, "Track ids not found for given request! Url was : "+URLS.get(API_CALL));

        if(response_arr.length() > 0){
            Iterator<Object> tracks = response_arr.iterator();
            while(tracks.hasNext()){
                Object track = tracks.next();
                int trackId = Integer.parseInt(track.toString().trim());
                softAssert.assertEquals(trackId > 0, true, "Track ids must be numeric value!");
            }
        }

        softAssert.assertAll();
    }

    @DataProvider(name = "dp")
    public Object [][] url(){
        return new Object[][]
        {
            {
                RecomendedTrackTd.track_ids[API_CALL]
            }
        };
    }
}
