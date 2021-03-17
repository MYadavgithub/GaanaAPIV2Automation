package utils;
import java.util.Map;
import common.RequestHandler;
import io.restassured.response.Response;

public class GenerateHashCode {
    
    private String hashcode_enpoint = "/hashcode?id=";
    RequestHandler handler = new RequestHandler();

    public String createHash(String baseurl, String tracks_id, Map<String, String> headers){
        String url = baseurl+hashcode_enpoint+tracks_id;
        Response response = handler.createGetRequestWithCustomHeaders(url, headers);
        return response.asString().trim();
    }
}
