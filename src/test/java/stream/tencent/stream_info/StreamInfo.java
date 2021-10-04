package stream.tencent.stream_info;
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
import common.Headers;
import common.Helper;
import common.RequestHandler;
import config.BaseUrls;
import config.Endpoints;
import io.restassured.response.Response;
import test_data.StreamInfoTD;
import utils.GenerateHashCode;

public class StreamInfo extends BaseUrls {

    int API_CALL = 0;
    int MAX_CALL = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    RequestHandler reqHandler = new RequestHandler();
    StreamInfoController controller = new StreamInfoController();
    Map<Integer, ArrayList<String>> RESPONSES = new HashMap<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger log = LoggerFactory.getLogger(StreamInfo.class);
    
    @BeforeClass
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        BASEURL = baseurl().toString().trim();
        MAX_CALL = StreamInfoTD.consumers.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = StreamInfoTD.SI_INVOCATION)
    public void createStreamInfoApicall(String track_ids, int device_type){
        Map<String, String> headers = Headers.getHeaders(0, String.valueOf(device_type));
        GenerateHashCode hash = new GenerateHashCode();
        String hashcode = hash.getHashSum(BASEURL, track_ids, headers);
        String url = BASEURL+Endpoints.streamInfoEndpoint(track_ids, hashcode);
        Response response = reqHandler.createGetRequestWithCustomHeaders(url, headers);
        RESPONSES.put(API_CALL, controller.getStreamUrls(response));
        if(RESPONSES.size() == MAX_CALL){
            log.info("All playble StreamUrls saved successfully in runtime memory for further use.");
            Assert.assertEquals(RESPONSES.size() == MAX_CALL, true, "Stream info responses not saved successfully!");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = StreamInfoTD.SI_INVOCATION)
    public void decryptUrls(String track_ids, int device_type){
        ArrayList<String> streamUrls = RESPONSES.get(API_CALL);
        Map<String, String> headers = Headers.getHeaders(0, String.valueOf(device_type));
        boolean isLinkvalid = controller.validateStreamUrlPlayble(BASEURL, streamUrls, headers);
        if(isLinkvalid){
            log.info("For device type : "+GlobalConfigHandler.getAppName(device_type)+ " akamai link working.");
        }else{
            Assert.assertEquals(isLinkvalid, true, "Akamai stream url validation not working!");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] trackDeviceType(){
        return new Object[][]
        {
            {
                StreamInfoTD.trackIds, StreamInfoTD.consumers[API_CALL]
            }
        };
    }
}