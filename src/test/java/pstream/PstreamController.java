package pstream;
import java.util.Map;
import common.Headers;
import test_data.PstreamTd;

public class PstreamController {
    
    public Map<String, String> generateHeaders(String device_type){
        Map<String, String> headers = Headers.getHeaders(0, null);
        headers.replace("deviceType", device_type);
        if(!device_type.equals(PstreamTd.device_type[0])){
            headers.replace("appVersion", "V11");
        }
        return headers;
    }
}
