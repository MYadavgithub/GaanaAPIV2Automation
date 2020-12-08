package recomendation.tencent_cdn;
import config.BaseUrls;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import common.RequestHandler;
import org.slf4j.LoggerFactory;
import io.restassured.response.Response;

/**
 * @author Umesh Shukla
 */

public class GetTrackIds extends BaseUrls{

    int content_source = 12;
    RequestHandler handler = new RequestHandler();
    private static Logger log = LoggerFactory.getLogger(GetTrackIds.class);

    private String prepareRequest(){
        baseurl();
        String baseurl = prop.getProperty("portalx_url").toString().trim();
        String endpoint = "/playlist/entity/detail?playlist_id=1001849&token=3bb7894a29e3ba26a6dd6c89d6a09317";
        return baseurl+endpoint;
    }


    // @Test(priority = 1)
    public ArrayList<String> getAllTracks() {
        String url = prepareRequest();
        log.info("Get tracks URL : "+url);
        Response response = handler.createGetRequestWithoutHeader(url);
        ArrayList<String> tracklist = getAllTrackIds(response);
        return tracklist;
    }

    private ArrayList<String> getAllTrackIds(Response response_data) {
        ArrayList<String> trackList = new ArrayList<String>();     
        JSONObject response = new JSONObject(response_data.asString());
        JSONArray tracklist = response.getJSONArray("section_data").getJSONObject(0).getJSONArray("tracks");

        Iterator<Object> tracks = tracklist.iterator();
        while(tracks.hasNext()){
            JSONObject track = (JSONObject) tracks.next();
            int content_source_value = Integer.parseInt(track.getString("content_source").trim());
            if(content_source_value == content_source){
                trackList.add(track.getString("track_id").trim());
            }
        }

        return trackList;
    }

    private String createHashGenratorUrl(String string) {
        String baseurl = baseurl();
        String end_point = "/aes/sum/ids?ids=" + string;
        return baseurl+end_point;
    }

    public String createHashKey(String string){
        String url = createHashGenratorUrl(string);
        RequestHandler rq = new RequestHandler();
        Response response = rq.createGetRequestCall(prop, url);
        String hash = response.asString();
        if(hash.length() > 0)
            return hash;
        return null;
    }
}