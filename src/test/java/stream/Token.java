package stream;
import java.util.Map;
import config.Endpoints;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.response.Response;

/**
 * @author [umesh.shukla]
 * @email [umesh.shukla@gaana.com]
 * @create date 2021-09-20 18:45:10
 * @modify date 2021-09-20 18:45:10
 * @desc [description]
 */

public class Token {

    public static String hashcodeId(String baseurl, Map<String, String> headers, int track_id){
        String url = baseurl+Endpoints.TRACKID_ENDPOINT_HASHCODE+track_id;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        String token = response.asString().trim();
        return token;
    }

    public static String hashcodeIds(String baseurl, Map<String, String> headers, String ids){
        String url = baseurl+Endpoints.TRACKIDS_ENDPOINT_HASHCODE+ids;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        String token = response.asString().trim();
        return token;
    }
}
