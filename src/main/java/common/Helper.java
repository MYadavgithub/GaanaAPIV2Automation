package common;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
