package stream.tencent.pstream;
import java.util.Map;
import org.json.JSONObject;
import common.RequestHandler;
import io.restassured.response.Response;

public class DecryptStreamUrl {
    
    RequestHandler handler = new RequestHandler();
    String get_stream_url_data_endpoint = "/getURLV1.php?track_id=";
    String decrypt_streamUrl_endpoint = "/aes/app/decrypt?val=";

    public JSONObject getStreamUrlData(String baseurl, Map<String, String> headers, String track_id, String hashcode){
        String url = baseurl+get_stream_url_data_endpoint+track_id+"&hashcode="+hashcode;
        Response response = handler.createGetRequestWithCustomHeaders(url, headers);
        JSONObject responseObj = new JSONObject(response.asString());
        JSONObject resultObj = new JSONObject();
        resultObj.put("data", responseObj.optString("data").toString().trim());
        resultObj.put("content_source", responseObj.optString("content_source").toString().trim());
        return resultObj;
    }

    public String decryptAndGetStreamUrl(String baseurl, Map<String, String> headers, String stream_data){
        String url = baseurl+decrypt_streamUrl_endpoint+stream_data;
        Response response = handler.createGetRequestWithCustomHeaders(url, headers);
        return response.asString();
    }
}