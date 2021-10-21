package recomendation.autoqueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import common.GlobalConfigHandler;
import config.Endpoints;
import config.enums.DeviceType;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.response.Response;
import logic_controller.AutoQueueController;
import test_data.AutoQueueTd;

public class GetSuggestedSongs {
    
    int API_CALL;
    int MAX_CALL;
    String BASEURL;
    ArrayList<String> URLS = new ArrayList<>();
    Map<Integer, Response> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    AutoQueueController aqController = new AutoQueueController();
    private static Logger LOGGER = LoggerFactory.getLogger(GetSuggestedSongs.class);
    
    @BeforeClass
    public void prepareEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        MAX_CALL = AutoQueueTd.SS_INVOCATION;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void createGetSuggestedCall(int type, String track_ids){
        String url;
        Map<String, String> headers;
        Response response = null;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        // if(API_CALL == 0){
        //     url = BASEURL+Endpoints.GET_SUGGESTED_SONGS+type;
        //     headers = RequestHelper.getHeaders(0, DeviceType.ANDROID_APP);
        //     headers.replace("deviceId", AutoQueueTd.DEVICE_ID);
        //     response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        // }else{
            url = BASEURL+Endpoints.GET_SUGGESTED_SONGS+type+"&trackIds="+track_ids;
            response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        // }
        URLS.add(url);
        RESPONSES.put(API_CALL, response);
        if(API_CALL == MAX_CALL-1){
            Assert.assertEquals(RESPONSES.size(), MAX_CALL, "Response not captured properly for further validations!");
            LOGGER.info(this.getClass()+" All response captured for further validations.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = AutoQueueTd.SS_INVOCATION)
    public void validateCountStatusUserToken(int type, String track_ids){
        boolean isCountStatusUserTokenValid = aqController.validateCountStatusUserToken(URLS.get(API_CALL), RESPONSES.get(API_CALL));
        Assert.assertEquals(isCountStatusUserTokenValid, true, "Error in CountStatusUserTokenValidation for Url : "+URLS.get(API_CALL));
        if(API_CALL == MAX_CALL-1 && isCountStatusUserTokenValid)
            LOGGER.info(this.getClass()+" Count Status and user token validated successfully.");
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }



    @DataProvider(name = "dp")
    public Object[][] DataProvider(){
        return new Object [][]
        {
            {
                AutoQueueTd.TYPE[API_CALL], AutoQueueTd.TRACKS
            }
        };
    }
}
