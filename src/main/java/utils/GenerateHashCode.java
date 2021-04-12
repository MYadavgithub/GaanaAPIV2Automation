package utils;
import java.util.Map;
import common.RequestHandler;
import io.restassured.response.Response;

public class GenerateHashCode {
    
    private String hashcode_enpoint = "/hashcode?id=";
    private String hashsum_enpoint = "/aes/sum/ids?ids=";
     RequestHandler handler = new RequestHandler();

    public String createHash(String baseurl, String tracks_id, Map<String, String> headers){
        String url = baseurl+hashcode_enpoint+tracks_id;
        Response response = handler.createGetRequestWithCustomHeaders(url, headers);
        return response.asString().trim();
    }

    public String getHashSum(String baseurl, String track_ids, Map<String, String> headers){
        String url = baseurl+hashsum_enpoint+track_ids;
        Response response = handler.createGetRequestWithCustomHeaders(url, headers);
        return response.asString().trim();
    }
}
