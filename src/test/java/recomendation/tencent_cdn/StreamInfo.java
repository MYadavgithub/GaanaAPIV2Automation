package recomendation.tencent_cdn;
import config.BaseUrls;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.testng.Assert;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import io.restassured.response.Response;
import org.apache.tools.ant.util.StringUtils;

/**
 * @author Umesh Shukla
 */

public class StreamInfo extends BaseUrls {

    ArrayList<String> trackids = null;
    RequestHandler handler = new RequestHandler();
    private static Logger log = LoggerFactory.getLogger(GetTrackIds.class);

    private String prepareUrl(ArrayList<String> trackids) {
        // System.setProperty("env", "local");
        // System.setProperty("type", "reco");
        // System.setProperty("device_type", "android");
        baseurl();
        GetTrackIds getTrackids = new GetTrackIds();
        String track_ids = StringUtils.join(trackids,",");
        String hash_key = getTrackids.createHashKey(track_ids);
        String baseurl = prop.getProperty("reco_baseurl").toString().trim();
        String endpoint = "/stream/info?ids=" + track_ids + "&hash=" + hash_key;
        return baseurl+endpoint;
    }

    // @Test(priority = 1)
    public ArrayList<String> getAllStreamUrl(ArrayList<String> alltrackids) {
        trackids = alltrackids;
        String url = prepareUrl(trackids);
        log.info("Stream Url : "+url);
        RequestHandler rq = new RequestHandler();
        Response api_response = rq.createGetRequest(url);
        JSONObject response = new JSONObject(api_response.asString());
        JSONArray stream_details = response.getJSONArray("streamingDetails");
        ArrayList<String> stream_url = getStreamUrls(stream_details);
        return stream_url;
    }

    private ArrayList<String> getStreamUrls(JSONArray stream_details) {
        ArrayList<String> stream_url = new ArrayList<>();
        if(stream_details.length() > 0){
            Iterator<Object> streamUrlObject = stream_details.iterator();
            while(streamUrlObject.hasNext()){
                JSONObject track_object = (JSONObject) streamUrlObject.next();
                String track_id = track_object.getString("track_id").trim();
                if(trackids.contains(track_id)){
                    // String expiry_time = track_object.optString("expiry_time").trim();
                    stream_url.add(track_object.optString("stream_url").trim());
                }else{
                    Assert.assertEquals(trackids.contains(track_id), true, "Track id got from response not equal to queried one!");
                }
            }
        }

        if(stream_url.size() == trackids.size()){
            return stream_url;
        }else{
            log.error("Stream Url and Track ids size not matching : "+stream_url.size()+" : "+trackids.size());
        }

        return null;
    }
}
