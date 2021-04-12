package pstream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import config.BaseUrls;
import test_data.PstreamTd;
import utils.GenerateHashCode;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.Helper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

public class GetUrlV1 extends BaseUrls{
    
    int API_CALL = 0;
    int MAX_CALL = 0;
    int device_type_counter = 0;
    String BASEURL = "";
    Helper helper = new Helper();
    ArrayList<String> HASHCODES = new ArrayList<>();
    ArrayList<String> PLAYBLE_STREAMURLS = new ArrayList<>();
    Map<Integer, JSONObject> STREAMURLS = new HashMap<>();
    PstreamController controller = new PstreamController();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    GenerateHashCode hash = new GenerateHashCode();
    DecryptStreamUrl decrypt = new DecryptStreamUrl();
    private static Logger log = LoggerFactory.getLogger(GetUrlV1.class);

    @BeforeClass
    public void prepEnv(){
        GlobalConfigHandler.setLocalProps();
        baseurl();
        BASEURL = prop.getProperty("pstream-baseurl").toString().trim();
        MAX_CALL = PstreamTd.track_ids.length;
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = PstreamTd.PST_INVOCATION)
    public void getHashcodes(String track_id, String device_type){
        Map<String, String> headers = controller.generateHeaders(device_type);
        String hashcode = hash.createHash(BASEURL, track_id, headers);
        HASHCODES.add(hashcode);
        if(HASHCODES.size() == MAX_CALL){
            log.info("All hascode saved successfully in runtime memory for further use.");
            Assert.assertEquals(HASHCODES.size() == MAX_CALL, true, "Hashcode generation failed!");
        }

        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = PstreamTd.PST_INVOCATION)
    public void getStreamDataUrl(String track_id, String device_type) {
        Map<String, String> headers = controller.generateHeaders(device_type);
        JSONObject resultObj = decrypt.getStreamUrlData(BASEURL, headers, track_id, HASHCODES.get(API_CALL));
        STREAMURLS.put(API_CALL, resultObj);
        if(STREAMURLS.size() == MAX_CALL){
            log.info("All StreamUrls saved successfully in runtime memory for further use.");
            Assert.assertEquals(STREAMURLS.size() == MAX_CALL, true, "Hashcode generation failed!");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = PstreamTd.PST_INVOCATION)
    public void getStreamUrl(String track_id, String device_type) {
        Map<String, String> headers = controller.generateHeaders(device_type);
        String data = STREAMURLS.get(API_CALL).getString("data").toString().trim();
        String streamUrl = decrypt.decryptAndGetStreamUrl(BASEURL, headers, data);
        PLAYBLE_STREAMURLS.add(streamUrl);
        if(PLAYBLE_STREAMURLS.size() == MAX_CALL){
            log.info("All playble StreamUrls saved successfully in runtime memory for further use.");
            Assert.assertEquals(PLAYBLE_STREAMURLS.size() == MAX_CALL, true, "Hashcode generation failed!");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @Test(enabled = true, priority = 4, dataProvider = "dp", invocationCount = PstreamTd.PST_INVOCATION)
    public void validateStreamUrl(String track_id, String device_type) {
        if(API_CALL == 0){
            boolean isStreamUrlsValid = helper.validateActiveLinks(PLAYBLE_STREAMURLS);
            if(isStreamUrlsValid){
                log.info("All playble StreamUrls working fine.");
            }else{
                Assert.assertEquals(isStreamUrlsValid, true, "Playble streamUrl having issue!");
            }
        }

        int content_source = Integer.parseInt(STREAMURLS.get(API_CALL).getString("content_source").toString().trim());
        if(PLAYBLE_STREAMURLS.get(API_CALL).contains("stream-cdn.gaana.com") && content_source == 12){
            log.info(track_id+" track stream url is for stream CDN.");
        }else{
            log.info(track_id+" track stream url is for akamai.");
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_CALL);
    }

    @DataProvider(name = "dp")
    public Object [][] device_id_type(){

        return new Object[][]
        {
            {
                PstreamTd.track_ids[API_CALL], PstreamTd.device_type[API_CALL]
            }
        };
    }
}
