package stream.tencent.stream_info;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.Helper;
import io.restassured.response.Response;
import stream.tencent.pstream.DecryptStreamUrl;
import test_data.StreamInfoTD;

public class StreamInfoController {
    
    Helper helper = new Helper();
    DecryptStreamUrl decryptUrl = new DecryptStreamUrl();
    private static Logger log = LoggerFactory.getLogger(StreamInfo.class);
    
    public ArrayList<String> getStreamUrls(Response response){
        ArrayList<String> streamUrls = new ArrayList<>();
        JSONObject responseObj = new JSONObject(response.asString());
        JSONArray streamArray = responseObj.getJSONArray("streamingDetails");
        if(streamArray.length() > 0){
            Iterator<Object> streamItr = streamArray.iterator();
            while(streamItr.hasNext()){
                JSONObject streamUrlData = (JSONObject) streamItr.next();
                streamUrls.add(streamUrlData.optString("stream_url").toString().trim());
            }
        }
        return streamUrls;
    }

    public boolean validateStreamUrlPlayble(String baseurl, ArrayList<String> streamUrls, Map<String, String> headers) {
        boolean isLinkValid = false;
        if(streamUrls.size() > 0){
            for(String streamUrl : streamUrls){
                String playbleUrl = decryptUrl.decryptAndGetStreamUrl(baseurl, headers, streamUrl);
                log.info("Stream Url : \n"+playbleUrl);
                isLinkValid = playbleUrl.contains(StreamInfoTD.akamaiBaseUrl);
            }
        }
        return isLinkValid;
    }
}
