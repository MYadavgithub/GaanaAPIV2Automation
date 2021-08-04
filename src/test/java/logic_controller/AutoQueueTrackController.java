package logic_controller;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import common.Helper;
import io.restassured.response.Response;

public class AutoQueueTrackController {

    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(AutoQueueTrackController.class);

    public boolean validateStatusEntityType(String url, Response response, String ex_entity_type) {
        JSONObject response_object = helper.responseJSONObject(response);
        int status = Integer.parseInt(response_object.optString("status"));
        String entity_type = response_object.optString("entity_type").toString().trim();
        if(status == 1 && entity_type.equals(ex_entity_type)){
            return true;
        }else{
            log.error("For Url : "+url+"\nStatus and Entity Type validation failed!");
            return false;
        }
    }

    /**
     * @param flag ->  0 if JSONObject 1 if JSON Array
     * @param url
     * @param response
     * @return
     */
    public boolean validateEntityIds(int flag, String url, Response response) {
        boolean isEntityValid = false;
        JSONArray entities = null;
        if(flag == 0){
            JSONObject response_object = helper.responseJSONObject(response);
            entities = response_object.optJSONArray("entity_ids");
        }else if(flag == 1){
            entities = new JSONArray(response.asString());
        }

        for(int i = 0; i<entities.length(); i++){
            int entity_id = Integer.parseInt(entities.optString(i).toString().trim());
            if(entity_id > 0){
                isEntityValid = true;
            }else{
                isEntityValid = false;
                log.error("For Url : "+url+"\nEntities validation failed!");
                return isEntityValid;
            }
        }
        return isEntityValid;
    }
}
