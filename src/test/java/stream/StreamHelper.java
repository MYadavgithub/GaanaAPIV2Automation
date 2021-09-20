package stream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.*;
import common.Helper;
import config.Endpoints;
import config.enums.DeviceType;
import config.v1.GetProp;
import test_data.StreamTd;
import config.v1.RequestHandlerV1;
import config.v1.RequestHelper;
import config.v1.RequestHelper.ApiRequestTypes;
import config.v1.RequestHelper.ContentTypes;
import io.restassured.response.Response;
import stream.stream_pojo.GetUrlV1Response;

public class StreamHelper {

    private static String filter_type = "Track";
    private static Logger LOGGER = LoggerFactory.getLogger(StreamHelper.class);
    
    public static int[] getTrackIdsFromSearchFeed(GetProp prop, int tracks_required){
        int count = 0;
        int tracks [] = new int[tracks_required];
        String url = prop.getRecoBaseUrl()+Endpoints.searchFeed+StreamTd.SEARCHFEED_QUERY_PARAMS;
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, null, null, null);
        Helper helper = new Helper();
        JSONArray response_array = helper.responseJSONObject(response).getJSONArray("response");
        
        Iterator<Object> resp_itr = response_array.iterator();
        while(resp_itr.hasNext()){ 
            JSONObject track_object = (JSONObject) resp_itr.next();
            if(track_object.optString("ty").trim().toString().equals(filter_type)){
                tracks[count] = Integer.parseInt(track_object.getString("iid").trim());
                if(count == (tracks_required-1))
                    break;
                count++;
            }
        }
        return tracks;
    }

    public static JSONArray getHascodeAndTrackIds(String device_type, Map<String, JSONArray> generatedHashcodes) {
        String search_key = DeviceType.valueDeviceType(device_type).toString();
        for(Entry<String, JSONArray> hashcode_data : generatedHashcodes.entrySet()){
            if(hashcode_data.getKey().equals(search_key)){
                return hashcode_data.getValue();
            }
        }
        LOGGER.error("No hascodes and track id found for device type "+search_key);
        return null;
    }

    public static String validateGetUrlV1Responses(String url, Map<String, String> headers) {
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);

        // response.prettyPrint();
        Helper helper = new Helper();
        if(!helper.responseJSONObject(response).has("error_code")){
            GetUrlV1Response response_data = response.as(GetUrlV1Response.class);
            if(response_data.getEt() > 0 && response_data.getBitrate().trim().length() > 0)
                return response_data.getData().trim();
        }
        return url;
    }

    public static String[] getStreamUrl(String device_type, Map<String, String[]> streamUrls) {
        String search_key = DeviceType.valueDeviceType(device_type).toString();
        for(Entry<String, String[]> hashcode_data : streamUrls.entrySet()){
            if(hashcode_data.getKey().equals(search_key)){
                return hashcode_data.getValue();
            }
        }
        LOGGER.error("No hascodes and track id found for device type "+search_key);
        return null;
    }

    public static String createDecryptAppCall(String url, Map<String, String> headers) {
        ApiRequestTypes requestType = RequestHelper.ApiRequestTypes.GET;
        ContentTypes contentType = RequestHelper.ContentTypes.JSON;
        RequestHandlerV1 request = new RequestHandlerV1();
        Response response = request.executeRequestAndGetResponse(url, requestType, contentType, headers, null, null);
        return response.asString().trim();
    }
}
