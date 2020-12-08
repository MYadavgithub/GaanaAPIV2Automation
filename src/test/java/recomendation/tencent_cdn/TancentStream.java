package recomendation.tencent_cdn;
import common.Helper;
import config.BaseUrls;
import org.slf4j.Logger;
import org.testng.Assert;
import java.util.ArrayList;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;

public class TancentStream extends BaseUrls {

    ArrayList<String> trackids = null;
    ArrayList<String> allStreamUrls = null;
    ArrayList<String> tencentUrls = new ArrayList<>();
    private static Logger log = LoggerFactory.getLogger(GetTrackIds.class);

    @BeforeTest
    public void prepareTest(){
        // System.setProperty("env", "local");
        // System.setProperty("type", "reco");
        // System.setProperty("device_type", "android");
        baseurl();
        GetTrackIds getTrackids = new GetTrackIds();
        trackids = getTrackids.getAllTracks();
        StreamInfo streamInfo = new StreamInfo();
        allStreamUrls = streamInfo.getAllStreamUrl(trackids);
    }
    
    private String prepareUrl(String stream_url){
        String baseurl = prop.getProperty("portalx_ip_url").toString().trim();
        String endpoint = "/aes/app/decrypt?val="+stream_url;
        return baseurl+endpoint;
    }

    @Test(priority = 1)
    public void validateStreamUrls(){ 
        RequestHandler rq = new RequestHandler();
        if(trackids.size() == allStreamUrls.size()){
            for(String stream_url : allStreamUrls){
                String url = prepareUrl(stream_url);
                Response response = rq.createGetRequest(prop, url);
                String tencent_url = response.asString();
                log.info("Tencent CDN URL : "+tencent_url);
                boolean isTencentUrl = tencent_url.contains("https://stream-cdn.gaana.com");
                if(isTencentUrl){
                    tencentUrls.add(tencent_url);
                }else{
                    log.error("Not got tencent url for URL : \n"+stream_url);
                    Assert.assertEquals(isTencentUrl, true);
                }
            }
        }
    }

    @Test(priority = 2)
    public void validateTencentUrlsActive(){
        Helper helper = new Helper();
        boolean isTencentUrlValid = helper.validateActiveLinks(tencentUrls);
        Assert.assertEquals(isTencentUrlValid, true, "Some links are not working in tencent url!");
    }
}
