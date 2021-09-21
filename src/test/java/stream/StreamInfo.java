package stream;
import java.util.*;
import java.util.Map.Entry;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
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
 * @desc [StreamInfo streaming validations]
 */

public class StreamInfo {

    GetProp prop = null;
    public String BASEURL;
    String TRACK_IDS;
    Map<String, String> TRACK_WITH_HASHCODE = new HashMap<>();
    Map<String, String[]> STREAM_DETAILS = null;
    ArrayList<String> DecryptedStreamUrls = new ArrayList<>();
    GlobalConfigHandler handler = new GlobalConfigHandler();
    private static Logger LOGGER = LoggerFactory.getLogger(StreamInfo.class);

    @BeforeClass
    public void setEnv(){
        BASEURL = GlobalConfigHandler.baseurl();
        prop = new GetProp();
        int [] tracks = StreamHelper.getTrackIdsFromSearchFeed(prop, StreamTd.TRACK_COUNT);
        TRACK_IDS = StreamHelper.convertIntArrayToString(tracks);
    }

    @Parameters("consumer")
    @Test(enabled = true, priority = 1)
    public void generateHashCodes(@Optional("GaanaAndroidApp") String device_type) {
        DeviceType deviceType = DeviceType.valueDeviceType(device_type);
        Map<String, String> headers = RequestHelper.getHeaders(0, deviceType);
        TRACK_WITH_HASHCODE.put(TRACK_IDS, Token.hashcodeIds(BASEURL, headers, TRACK_IDS));

        if(TRACK_WITH_HASHCODE.isEmpty()){
            LOGGER.error(this.getClass() +"Hashcodes not generated properly...!");
        }
        Assert.assertEquals(!TRACK_WITH_HASHCODE.isEmpty(), true);
        LOGGER.info("HashCode and track_ids saved successfully.");
    }

    @Parameters("consumer")
    @Test(enabled = true, priority = 2)
    public void generateStreamUrlFromStreamInfo(@Optional("GaanaAndroidApp") String device_type) {
        DeviceType deviceType = DeviceType.valueDeviceType(device_type);
        Map<String, String> headers = RequestHelper.getHeaders(0, deviceType);
        String url = BASEURL+Endpoints.streamInfoEndpoint(TRACK_IDS, TRACK_WITH_HASHCODE.get(TRACK_IDS));
        STREAM_DETAILS = StreamHelper.getStreamInfoResponse(url, headers);

        if(STREAM_DETAILS.isEmpty()){
            LOGGER.error(this.getClass() +"Hashcodes not generated properly...!");
        }
        Assert.assertEquals(!STREAM_DETAILS.isEmpty(), true);
        
        if(STREAM_DETAILS.keySet().size() != (StreamTd.TRACK_COUNT-1)){
            LOGGER.error(this.getClass() +"Total requested Tracks not matching with response of stream urls...!");
        }
        Assert.assertEquals(STREAM_DETAILS.keySet().size() == (StreamTd.TRACK_COUNT-1), true);
        LOGGER.info("Stream url generated from stream info successfully.");
    }

    @Parameters("consumer")
    @Test(enabled = true, priority = 3)
    public void decrytAndSaveUrl(@Optional("GaanaAndroidApp") String device_type) {
        String url;
        DeviceType deviceType = DeviceType.valueDeviceType(device_type);
        Map<String, String> headers = RequestHelper.getHeaders(0, deviceType);

        for(Entry<String, String[]> track_stream_details : STREAM_DETAILS.entrySet()){
            String track_id = track_stream_details.getKey();
            String streamUrl = track_stream_details.getValue()[0];

            if(deviceType.equals(DeviceType.GAANA_WEBSITE_APP)){
                url = prop.getPortalXurl().trim()+Endpoints.WEB_STREAM_DECRYPT+streamUrl;
            }else{
                url = BASEURL+Endpoints.APP_STREAM_DECRYPT+streamUrl;
            }

            String decryptedStreamUrl = StreamHelper.createDecryptAppCall(url, headers);
            if(decryptedStreamUrl.length() > 0){
                boolean isAppAkamai = decryptedStreamUrl.startsWith(StreamTd.APP_AKAMAI_PREFIX);
                boolean isAppTencent = decryptedStreamUrl.startsWith(StreamTd.APP_TENCENT_PREFIX);
                boolean isWebStream = decryptedStreamUrl.startsWith(StreamTd.WEB_PREFIX);
                boolean isAppAkamaiMx = decryptedStreamUrl.startsWith(StreamTd.APP_AKAMAI_MX_PREFIX);
                if(isAppAkamai || isAppTencent || isWebStream || isAppAkamaiMx){
                    DecryptedStreamUrls.add(decryptedStreamUrl);
                }else{
                    LOGGER.error(this.getClass()+" stream url prefix validations failed... Url was : "+decryptedStreamUrl);
                    Assert.assertEquals(false, true, "Error...!");
                }
            }else{
                LOGGER.error("Unable to decrypt stream url for track_id : "+track_id+"\nUrl was : "
                    +url+"\nheaders are : "+headers);
            }
        }
        if(DecryptedStreamUrls.size() > 0)
            LOGGER.info("Stream Url decrypted and saved for status code validation successfully.");
    }

    @Parameters("consumer")
    @Test(enabled = true, priority = 4)
    public void validateStreamUrlPostDecryptStatusCode(@Optional("GaanaAndroidApp") String device_type) {
        if(DecryptedStreamUrls.size() > 0){
            boolean isAllStreamUrlsValid = Helper.validateGetUrlStatusCode(DecryptedStreamUrls);
            Assert.assertEquals(isAllStreamUrlsValid, true, "Error... while validating status code!");
            LOGGER.info("Decrypted stream url status code validated successfully.");
        }else{
            LOGGER.warn(this.getClass()+" no stream url found for status code validations,  Object size was : "+DecryptedStreamUrls.size());
        }
    }
}