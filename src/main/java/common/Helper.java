package common;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;


public class Helper {

    private static Logger log = LoggerFactory.getLogger(Helper.class);
    
    /**
     * Get All keys in one Json object.
     * @param val
     * @return
     */
    public List<Object> keys(JSONObject val) {
        return Arrays.asList(val.keySet().toArray());
    }

    public String getKeyValue(JSONObject obj, String key){
        return obj.getString(key).trim();
    }

    /**
     * Compare two list are same or not
     * @param actual_list
     * @param ex_list
     * @return
     */
    public boolean compareList(List<Object> actual_list, List<String> ex_list){
        List<String> actual = actual_list.stream()
            .map(object -> Objects.toString(object, null))
            .collect(Collectors.toList());
        Collections.sort(actual);
        Collections.sort(ex_list);
        if(!actual.equals(ex_list)){
            if(actual.size() != ex_list.size()){
                log.error("There is key missing please compare manually and validate : \n Expected List was : \n"+ex_list+"\n Current List : \n"+actual);
                return false;
            }
        }
        return actual.equals(ex_list);
    }

    /**
     * @param keys -> keyset
     * @param data -> data to be validated JsonObject.
     * @param list -> this is the list which have sub arrays so we need to skip those.
     */
	public boolean validateEachObject(List<Object> keys, JSONObject data, List<String> list) {
        boolean isvalid = false;
        ArrayList<String> emptyKeys = new ArrayList<>();
        if(list == null)
            list = new ArrayList<>();

        if(keys.size() > 0 && !data.isEmpty()){
            for(Object key : keys){
                String key_name = key.toString();
                if(!list.contains(key_name)){
                    Object key_value = "";
                    try{
                        key_value = data.get(key_name);
                    }catch(Exception e){
                        e.printStackTrace();
                        log.info("Findings from here : "+data.keySet());
                    }
                    if(!key_value.toString().equals("null") && key_value.toString().length() != 0){
                        //log.info("Integer Type : "+ key +" : "+key_value.toString());
                        if(key_value instanceof String){
                            if(key_value.toString().length() > 0){
                                isvalid = true;
                            }else{
                                return false;
                            }
                        }else if(key_value instanceof Integer){
                            if(Integer.parseInt(key_value.toString().trim()) >= 0){
                                isvalid = true;
                            }else{
                                return false;
                            }
                            // log.info("Integer Type : "+ key +" : "+key_value.toString());
                        }else if(key_value instanceof Boolean){
                            // pass
                            // log.info("Boolean Type : "+ key +" : "+key_value.toString());
                        }else if(key_value instanceof Double){
                            // pass
                            // log.info("Double Type : "+ key +" : "+key_value.toString());
                        }
                    }else{
                        emptyKeys.add(key_name);
                    }
                }
            }
        }
        return isvalid;
    }

    /**
     * If data in json object is not mandatory then use this method
     * @param keys
     * @param data
     * @return
     */
    public boolean validateEachJsonObjectWithAllowedEmptyData(List<Object> keys, JSONObject data){
        boolean isJsonObjectValid = false;
        if(keys.size() > 0){
            for(Object key : keys){
                String key_name = key.toString();
                Object key_value = data.get(key_name);
                if(key_value instanceof String){
                    if(key_value.toString().trim().length() >= 0){
                        isJsonObjectValid = true;
                    }else{
                        return false;
                    }
                }else if(key_value instanceof Integer){
                    int val = Integer.parseInt(key_value.toString().trim());
                    if(val >= 0){
                        isJsonObjectValid = true;
                    }else{
                        return false;
                    }
                }
            }
        }
        return isJsonObjectValid;
    }

    /**
     * Vaidate two JSONObjects having same data or not
     * @param tracks
     * @param previous_tracks
     * @return
     */
    public boolean validateResDataWithOldData(String url, JSONArray previous_data, JSONArray current_data){
        int counter = 0;
        boolean result = false;
        Iterator<Object> itr = current_data.iterator();
        while (itr.hasNext()) {
            JSONObject current_obj = (JSONObject) itr.next();
            JSONObject prev_res_obj = previous_data.getJSONObject(counter);
            JsonObject current = JsonParser.parseString(current_obj.toString()).getAsJsonObject();
            JsonObject old = JsonParser.parseString(prev_res_obj.toString()).getAsJsonObject();
            if(current.equals(old)){
                result = true;
            }else{
                result = false;
                log.error("Previous data didn't matched with current response data for response of : "+url+
                    "\n current object : "+current+"\n prev object was : "+old);
                break;
            }
            counter++;
        }
        return result;
    }

    /**
     * Remove Specific key from json array from each json object.
     * @param arr
     * @param key
     * @param url -> api url
     * @return
     */
    public JSONArray removeJsonObject(JSONArray arr, String key, String url) {
        JSONArray sorted_arr = new JSONArray();

        if(arr == null){
            log.error("Passed array was null, this argument was not valid please check : "+arr);
            return arr;
        }

        int count = 0 ;
        if(arr.length() > 0){
            Iterator<Object> itr = arr.iterator();
            while(itr.hasNext()){
                JSONObject object = (JSONObject) itr.next();
                try{
                    if(object.getString(key) != null) {
                        object.remove(key);
                        sorted_arr.put(object);
                        count++;
                    }
                }catch(Exception e){
                    log.error(key+" not present in JSONObject : \n"+object);
                    break;
                }
            }
        }

        // Assertion to validate whether we are getting key in each object or not because its mandatory field.
        if(count != arr.length()){
            log.error("Not able to find expected key for each json object please validate api manually url was : \n"+url);
            Assert.assertEquals(count == arr.length(), true);
        }

        return sorted_arr;
    }
}
