package common;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.*;
import org.testng.Assert;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.IOException;

public class Helper {

    int resCode = 200;
    HttpURLConnection uc = null;
    private static Logger log = LoggerFactory.getLogger(Helper.class);

    public static int generateRandomNumber(int minimum, int maximum){
        Random rn = new Random();
        int range = (maximum-(minimum + 1));
        return rn.nextInt(range) + minimum;
    }

    /**
     * Get All keys in one Json object.
     * @param val
     * @return
     */
    @Step("Getting keys from Json Object : {0}")
    public List<Object> keys(JSONObject val) {
        return Arrays.asList(val.keySet().toArray());
    }

    public String getKeyValue(JSONObject obj, String key){
        return obj.getString(key).trim();
    }

    public JSONObject responseJSONObject(Response response){
        return new JSONObject(response.asString());
    }

    public JSONArray responseJSONArray(Response response){
        return new JSONArray(response.asString());
    }

    public JSONArray getJSONArray(String url, String array_key, Response response){
        JSONArray entities = responseJSONObject(response).getJSONArray(array_key);
        if(entities == null || entities.length() <= 0){
            log.error(array_key+" can't be null for url : "+url);
        }
        return entities;
    }

    /**
     * Compare two list are same or not
     * @param actual_list
     * @param ex_list
     * @return
     */
    @Step("Comparing two lists, actual list : {0} expected list : {1}")
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
     * @param keys
     * @param data
     * @param skipList
     * @return
     */
    public boolean validateJSONObjectValueBasedOnKeys(List<Object> keys, JSONObject data, List<String> skiplist){
        boolean isvalid = false;
        ArrayList<String> emptyKeys = new ArrayList<>();
        if(keys == null || data == null){
            return false;
        }

        for(Object _key : keys){
            String key = _key.toString();
            Object value = "";
            if(!skiplist.contains(key)){
                value = data.get(key);
            }
            if(value != null && !value.toString().equals("null") && value.toString().length() != 0){
                if(value instanceof String){
                    if(value.toString().length() > 0){
                        isvalid = true;
                    }else{
                        return false;
                    }
                }
                else if(value instanceof Integer){
                    if(Integer.parseInt(value.toString().trim()) >= 0){
                        isvalid = true;
                    }else{
                        return false;
                    }
                }
            }else if(value.toString().length() == 0 && !skiplist.contains(key)){
                emptyKeys.add(key);
            }
        }
        // if(emptyKeys.size() > 0){
        //     log.info("Value missing for these keys : "+emptyKeys.toString());
        // }
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

    /**
     * Compare two same json array but if shorting is not proper
     * @param res
     * @param self
     * @param common_key
     * @return
     */
    public boolean matchJSONArray(JSONArray res, JSONArray self, String common_key){
        boolean result = false;
        if(res.length() == self.length()){
            Iterator<Object> itr = res.iterator();
            while(itr.hasNext()){
                JSONObject res_obj =  (JSONObject) itr.next();
                String id = res_obj.getString(common_key).trim();
                JSONObject self_obj = findById(self, common_key, id);

                List<Object> keys = keys(res_obj);
                for(Object key : keys){
                    String res_val = (String) res_obj.get(key.toString());
                    String self_val = (String) self_obj.get(key.toString());
                    if(res_val.equals(self_val)){
                        result = true;
                    }else{
                        result = false;
                        log.error("While comparing : "+res+"\nwith : "+self +"\nfor key "+key+"response value : "+res_val+
                        "and self queried value was : "+self_val);
                        break;
                    }
                }
            }
        }else{
            log.error("Compare two JSON object length mismatched!");
            return result;
        }
        return result;
    }

    /**
     * To get same json object for coparision
     * @param selfData
     * @param id_key matching common id
     * @param id
     * @return
     */
    public JSONObject findById(JSONArray selfData, String id_key, String id) {
        Iterator<Object> itr = selfData.iterator();
        while(itr.hasNext()){
            JSONObject val = (JSONObject) itr.next();
            if(val.getString(id_key).equals(id)){
                return val;
            }
        }
        return null;
    }

    /**
     * Validate Link is active or not
     */
    @Step("validating links are active or not and list values are : {0}")
    public boolean validateActiveLinks(ArrayList<String> links) {
        boolean linkActive = false;
        ArrayList<String> inactiveUrls = new ArrayList<>();

        if(links.size() <= 0){
            return linkActive;
        }

        int count = 1;
		for(String link : links) {
			if(link.contains("http")) {
                try {
                    uc = (HttpURLConnection)(new URL(link).openConnection());
                    uc.setRequestMethod("HEAD");
                    uc.connect();
                    int res = uc.getResponseCode();
                    if(res == resCode) {
                        linkActive = true;
                    }else {
                        linkActive = false;
                        inactiveUrls.add("Count : "+count+", URL :"+link);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            count++;
        }

        if(inactiveUrls.size() > 0){
            linkActive = false;
            log.error("Below given urls are inactive state, please manually validate the same : \n"+inactiveUrls);
        }
		return linkActive;
	}

    /**
     * To get verified multiple kind of artwork from same jsonObject
     * @param url
     * @param unique_key
     * @param entity
     * @param ex_artworks
     * @return
     */
    public boolean validateEachEntityArtworks(String url, String unique_key, JSONObject entity, String [] ex_artworks) {
        boolean isArtworkValid = false;
        ArrayList<String> artworks = new ArrayList<>();
        String unique_id = entity.getString(unique_key.trim().toString());
        for(int i = 0; i<ex_artworks.length; i++){
            artworks.add(entity.optString(ex_artworks[i]).toString().trim());
        }
        isArtworkValid = validateActiveLinks(artworks);
        if(!isArtworkValid){
            log.error("For Url : "+url+ "\n unique_id : "+unique_id+"\nArtworks not validated, artworks are : \n"+artworks);
            return isArtworkValid;
        }
        Assert.assertEquals(isArtworkValid, true);
        artworks.clear();
        return isArtworkValid;
    }

    /**
     * Validate Link is active or not
     * @param urls
     * @return
     */
    public static boolean validateGetUrlStatusCode(ArrayList<String> urls){
        boolean linkActive = false;
        ArrayList<String> inactiveUrls = new ArrayList<>();

        if(urls.size() <= 0){
            return linkActive;
        }

        int count = 1;
		for(String url : urls) {
			if(url.contains("http")) {
               Response response = RestAssured.given()
                    .urlEncodingEnabled(false)
                    .when().get(url);
                if(response.getStatusCode() == 200){
                    linkActive = true;
                }else{
                    linkActive = false;
                    inactiveUrls.add("Count : "+count+", URL :"+url);
                }
            }
            count++;
        }

        if(inactiveUrls.size() > 0){
            linkActive = false;
            log.error("Below given urls are inactive state, please manually validate the same : \n"+inactiveUrls);
        }
		return linkActive;
    }
}