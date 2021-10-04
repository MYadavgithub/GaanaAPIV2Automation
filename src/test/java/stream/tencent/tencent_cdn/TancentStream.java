package stream.tencent.tencent_cdn;
import common.Helper;
import config.BaseUrls;
import config.Constants;
import org.slf4j.Logger;
import org.testng.Assert;
import java.util.ArrayList;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import utils.CsvReader;
import org.testng.annotations.Parameters;

public class TancentStream extends BaseUrls {
    String decryption_type = "";
    String consumer = "";
    static String DECRYPTION_TYPE = "tencent";
    ArrayList<String> trackids = null;
    ArrayList<String> allStreamUrls = null;
    ArrayList<String> decryptedUrls = new ArrayList<>();
    private static Logger log = LoggerFactory.getLogger(TancentStream.class);

    public void prepareTest(String consumer, String decryption_type){
        // System.setProperty("env", "local");
        // System.setProperty("type", "reco");
        // System.setProperty("device_type", "android");
        baseurl();
        System.setProperty("device_type", consumer);
        GetTrackIds getTrackids = new GetTrackIds();
        if(decryption_type.equals(DECRYPTION_TYPE)){
            trackids = getTrackids.getAllTracks();
            if(trackids.size() <= 0){
                ArrayList<String> tracks = CsvReader.readCsv("./src/test/resources/data/Tencent_CDN_Tracks.csv");
                tracks.remove(0);
                trackids = tracks;
            }
        }else{
            ArrayList<String> tracks = CsvReader.readCsv("./src/test/resources/data/Track_Akamai_Test.csv");
            tracks.remove(0);
            trackids = tracks;
        }
        StreamInfo streamInfo = new StreamInfo();
        allStreamUrls = streamInfo.getAllStreamUrl(trackids);
    }
    
    private String prepareUrl(String stream_url){
        String endpoint = "";
        String baseurl = prop.getProperty("portalx_ip_url").toString().trim();
        if(System.getProperty("device_type").equalsIgnoreCase(Constants.GaanaWebsiteApp)){
            endpoint = "/aes/web/decrypt?val="+stream_url;
        }else{
            endpoint = "/aes/app/decrypt?val="+stream_url;
        }
        return baseurl+endpoint;
    }

    @Parameters({"consumer", "decryption_type"})
    @Test(priority = 1)
    public void validateStreamUrls(String consumer_ty, String decryption_ty){
        consumer = consumer_ty;
        decryption_type = decryption_ty;
        prepareTest(consumer, decryption_type);
        RequestHandler rq = new RequestHandler();

        if(trackids.size() == allStreamUrls.size() && trackids.size() > 0){
            for(String stream_url : allStreamUrls){
                String url = prepareUrl(stream_url);
                Response response = rq.createGetRequest(url);
                String decrypted_url = response.asString();
                if(decryption_type.equals(DECRYPTION_TYPE)){
                    log.info("Tencent CDN URL : "+decrypted_url);
                    boolean isTencentUrl = decrypted_url.contains("https://stream-cdn.gaana.com");
                    if(isTencentUrl){
                        decryptedUrls.add(decrypted_url);
                    }else{
                        log.error("Not got tencent url for URL : \n"+stream_url);
                        Assert.assertEquals(isTencentUrl, true);
                    }
                }else{
                    boolean isAkamaiUrl = false;
                    log.info("Akamai URL : "+decrypted_url);
                    if(consumer.equals(Constants.GaanaWapApp) || consumer.equals(Constants.GaanaWebsiteApp)){
                        isAkamaiUrl = decrypted_url.contains("https://vodhlsweb-vh.akamaihd.net");
                    }else{
                        isAkamaiUrl = decrypted_url.contains("https://vodhls-vh.akamaihd.net");
                    }

                    if(isAkamaiUrl){
                        decryptedUrls.add(decrypted_url);
                    }else{
                        log.error("Not got Akamai url for URL : \n"+stream_url);
                        Assert.assertEquals(isAkamaiUrl, true);
                    }
                }
            }
        }else{
            log.error("Not able to complete this test suite due to no track_ids exits as test data!");
            Assert.assertEquals(trackids.size() > 0, true);
        }
    }

    @Test(priority = 2)
    public void validateStreamUrlsActive(){
        boolean isDecryptedUrlValidated = false;
        Helper helper = new Helper();
        if(decryption_type.equals(DECRYPTION_TYPE)){
            isDecryptedUrlValidated = helper.validateActiveLinks(decryptedUrls);
        }else{
            isDecryptedUrlValidated = Helper.validateGetUrlStatusCode(decryptedUrls);
        }
        Assert.assertEquals(isDecryptedUrlValidated, true, "Some links are not working in tencent url!");
    }
}
