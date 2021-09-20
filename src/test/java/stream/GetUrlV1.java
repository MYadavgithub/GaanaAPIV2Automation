package stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import common.GlobalConfigHandler;
import common.Helper;
import config.Endpoints;
import config.enums.DeviceType;
import config.v1.GetProp;
import config.v1.RequestHelper;
import test_data.StreamTd;

/**
 * @author [umesh.shukla]
 * @email [umesh.shukla@gaana.com]
 * @create date 2021-09-20 12:07:36
 * @modify date 2021-09-20 12:07:36
 * @desc [GetUrlV1 streaming validations]
 */

public class GetUrlV1 {
    
    int API_CALL = 0;
    int MAX_API_CALL = 0;
    int MAX_TRACKS = 3;
    String BASEURL;
    GetProp prop = null;
    int TRACK_IDS [] = {};
    String CONSUMER;
    String [] device_types = {};
    GlobalConfigHandler handler = new GlobalConfigHandler();
    Map<String, JSONArray> generatedHashcodes = new HashMap<>();
    Map<String, String[]> streamUrls = new HashMap<>();
    ArrayList<String> DecryptedStreamUrls = new ArrayList<>();
    private static Logger LOGGER = LoggerFactory.getLogger(GetUrlV1.class);

    @BeforeClass
    public void setEnv(ITestContext context){
        BASEURL = GlobalConfigHandler.baseurl();  
        prop = new GetProp();
        device_types = prop.getDeviceType();
        MAX_API_CALL = StreamTd.INVOCATION;
        TRACK_IDS = StreamHelper.getTrackIdsFromSearchFeed(prop, MAX_API_CALL);
    }

    @Test(enabled = true, priority = 1, dataProvider = "dp", invocationCount = StreamTd.INVOCATION)
    public void prepareHashCodes(String device_type) {
        boolean isPodcasterApp = DeviceType.valueDeviceType(device_type).equals(DeviceType.PODCASTER_APP);
        boolean isHomePodApp = DeviceType.valueDeviceType(device_type).equals(DeviceType.HOMEPOD_APP);
        if(!isPodcasterApp && !isHomePodApp){
            System.setProperty("device_type", device_type);
            Map<String, String> headers = RequestHelper.getHeaders(0, DeviceType.valueDeviceType(device_type));
            JSONArray hashcodes = new JSONArray();
            for(int i = 0; i<=MAX_TRACKS; i++){
                JSONObject hashcode = new JSONObject();
                int random_number = Helper.generateRandomNumber(0, MAX_API_CALL);
                int track_id = TRACK_IDS[random_number];
                String token = Token.hashcodeId(BASEURL, headers, track_id);
                hashcode.put("track_id", track_id);
                hashcode.put("token", token);
                hashcodes.put(hashcode);
                if(i == (MAX_TRACKS-1) && hashcodes.length() > 0)
                    generatedHashcodes.put(DeviceType.valueDeviceType(device_type).toString(), hashcodes);
            }

            if(API_CALL == (StreamTd.INVOCATION-1)){
                if(generatedHashcodes.isEmpty()){
                    LOGGER.error(this.getClass() +"Hashcodes not generated properly...!");
                }
                Assert.assertEquals(!generatedHashcodes.isEmpty(), true);
                // LOGGER.error(generatedHashcodes.toString());
            }
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_API_CALL);
    }

    @Test(enabled = true, priority = 2, dataProvider = "dp", invocationCount = StreamTd.INVOCATION)
    public void validateGetUrlV1AndGetStreamUrl(String device_type) {
        boolean isPodcasterApp = DeviceType.valueDeviceType(device_type).equals(DeviceType.PODCASTER_APP);
        boolean isHomePodApp = DeviceType.valueDeviceType(device_type).equals(DeviceType.HOMEPOD_APP);
        if(!isPodcasterApp && !isHomePodApp){
            System.setProperty("device_type", device_type);
            Map<String, String> headers = RequestHelper.getHeaders(0, DeviceType.valueDeviceType(device_type));
            JSONArray hashcode_set = StreamHelper.getHascodeAndTrackIds(device_type, generatedHashcodes);

            if(hashcode_set.length() != (MAX_TRACKS+1)){
                LOGGER.error("Maximum tracks must be equal to the length of saved hashcodes...!");
                Assert.assertEquals(hashcode_set.length() != (MAX_TRACKS+1), true);
            }

            String [] stream_urls = {};
            for(int i = 0; i<=MAX_TRACKS; i++){
                if(i == 0)
                    stream_urls = new String[hashcode_set.length()];
                String track_id = hashcode_set.getJSONObject(i).optString("track_id").toString().trim();
                String hashcode = hashcode_set.getJSONObject(i).optString("token").toString().trim();
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(BASEURL+Endpoints.GET_URL_V1+"track_id=");
                urlBuilder.append(track_id+"&hashcode="+hashcode);
                urlBuilder.append("&quality=auto&delivery_type=stream");
                String url = urlBuilder.toString();
                String stream_url = StreamHelper.validateGetUrlV1Responses(url, headers);
                stream_urls[i] = stream_url;
                if(i == MAX_TRACKS && stream_urls.length > 0)
                    streamUrls.put(DeviceType.valueDeviceType(device_type).toString(), stream_urls);
            }

            if(API_CALL == (StreamTd.INVOCATION-1)){
                if(streamUrls.isEmpty()){
                    LOGGER.error(this.getClass() +"Stream Urls not generated properly...!");
                }
                Assert.assertEquals(!streamUrls.isEmpty(), true);
                // LOGGER.error(streamUrls.entrySet().toString());
            }
        }
        API_CALL = handler.invocationCounter(API_CALL, MAX_API_CALL);
    }

    @Test(enabled = true, priority = 3, dataProvider = "dp", invocationCount = StreamTd.INVOCATION)
    public void validateStreamUrlPostDecryt(String device_type) {
        boolean isPodcasterApp = DeviceType.valueDeviceType(device_type).equals(DeviceType.PODCASTER_APP);
        boolean isHomePodApp = DeviceType.valueDeviceType(device_type).equals(DeviceType.HOMEPOD_APP);
        if(!isPodcasterApp && !isHomePodApp){
            System.setProperty("device_type", device_type);
            Map<String, String> headers = RequestHelper.getHeaders(0, DeviceType.valueDeviceType(device_type));
            String savedStreamUrls [] = StreamHelper.getStreamUrl(device_type, streamUrls);
            if(savedStreamUrls.length != (MAX_TRACKS+1)){
                LOGGER.error("Saved hashcodes must be equal to the length of Maximum Tracks...!");
                Assert.assertEquals(savedStreamUrls.length != (MAX_TRACKS+1), true);
            }

            for(int i = 0; i<=MAX_TRACKS; i++){
                if(!savedStreamUrls[i].contains(BASEURL)){
                    String url;
                    if(DeviceType.valueDeviceType(device_type).equals(DeviceType.GAANA_WEBSITE_APP)){
                        url = BASEURL+Endpoints.WEB_STREAM_DECRYPT+savedStreamUrls[i];
                        LOGGER.error("BADA CUTIYA : "+url);
                    }else{
                        url = BASEURL+Endpoints.APP_STREAM_DECRYPT+savedStreamUrls[i];
                    }

                    String decryptedStreamUrl = StreamHelper.createDecryptAppCall(url, headers);
                    if(decryptedStreamUrl.length() > 0)
                        DecryptedStreamUrls.add(decryptedStreamUrl);
                }else{
                    LOGGER.warn(this.getClass()+" no stream url found for decrypt,  url was : "+savedStreamUrls[i]);
                }
            }
        }
        LOGGER.warn(DecryptedStreamUrls.toString());
        API_CALL = handler.invocationCounter(API_CALL, MAX_API_CALL);
    }


    @DataProvider(name = "dp")
    public Object [][] trackIds(){
        return new Object[][]
        {
            {
                device_types[API_CALL]
            }
        };
    }
}
