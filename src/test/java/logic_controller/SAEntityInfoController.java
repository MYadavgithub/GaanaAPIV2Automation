package logic_controller;
import java.util.*;
import org.json.*;
import org.slf4j.*;
import common.Helper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import test_data.SimilarAlbumsTd;
import utils.CommonUtils;
import org.testng.Assert;

public class SAEntityInfoController {

    Helper helper = new Helper();
    CommonUtils util = new CommonUtils();
    private List<Object> CURRENT_KEYS = null;
    private static Logger log = LoggerFactory.getLogger(SAEntityInfoController.class);
    
    public boolean validateResponses(String key, String url, Response response) {
        boolean isResponsevalidated = false;
        JSONObject response_object = null;
        JSONArray generic_entities = null;
        if(response == null){
            log.error("Response Body can't be null!");
            return isResponsevalidated;
        }

        if(key.equals(SimilarAlbumsTd.VALIDATOR_KEYS[0])){
            response_object = helper.responseJSONObject(response);
            return validateCommons(url, response_object);
        }else{
            generic_entities = helper.getJSONArray(url, "generic_entities", response);
        }

        if(generic_entities != null){
            int count = 0;
            Iterator<Object> entity_itr = generic_entities.iterator();
            while(entity_itr.hasNext()){
                JSONObject entity = (JSONObject) entity_itr.next();
                switch (key) {
                    case "Entity_keys":
                        isResponsevalidated = validateExEntityObjectKeys(url, entity);
                        if(!isResponsevalidated){
                            return isResponsevalidated;
                        }
                    break;

                    case "Entity_Values":
                        isResponsevalidated = validateExEntityObjectKeyValues(count, url, entity);
                        if(!isResponsevalidated){
                            return isResponsevalidated;
                        }
                    break;

                    case "Artworks":
                        isResponsevalidated = helper.validateEachEntityArtworks(url, "entity_id", entity, SimilarAlbumsTd.ARTWORK_TYPES);
                        if(!isResponsevalidated){
                            return isResponsevalidated;
                        }
                    break;

                    case "Primary Artist":
                        isResponsevalidated = validateEachArtist(url, entity);
                        if(!isResponsevalidated){
                            return isResponsevalidated;
                        }
                    break;
                
                    default:
                        log.info("for key "+key+ " no validation executor found!");
                    break;
                }
                count++;
            }
        }
        return isResponsevalidated;
    }

    private boolean validateEachArtist(String url, JSONObject entity) {
        boolean isArtistValid = false;
        int entity_id = Integer.parseInt(entity.getString("entity_id").trim().toString());
        if(entity_id <= 0){
            log.error("entity id unexpected : "+entity_id);
            return false;
        }
        int primary_artist_count = Integer.parseInt(entity.getJSONObject("generic_entity_info").optString("primary_artist_count").toString().trim());
        JSONArray artists = entity.getJSONObject("generic_entity_info").getJSONArray("primaryartist");
        if(artists.length() <= 0)
            return isArtistValid;
        
        if(artists.length() == primary_artist_count){
            for(int i = 0; i<artists.length(); i++){
                int artist_id = 0;
                String _artist_id = "";
                try{
                    artist_id = Integer.parseInt(artists.getJSONObject(i).getString("artist_id").toString().trim());
                }catch(NumberFormatException e){
                    _artist_id = artists.getJSONObject(i).getString("artist_id").toString().trim();
                    e.printStackTrace();
                    log.error("For entity_id "+entity_id+" artist expected behavior failed!");
                }
                String name = artists.getJSONObject(i).optString("name").toString().trim();
                String seokey = artists.getJSONObject(i).optString("seokey").toString().trim();
    
                if(artist_id > 0 && name.length() > 0 && seokey.length() > 0){
                    isArtistValid = true;
                }else if(_artist_id.length() > 0 && artist_id == 0 && name.length() > 0 && seokey.length() > 0){
                    isArtistValid = true;
                }else{
                    isArtistValid = false;
                    log.error("For Url : "+url+ "\n entity_id : "+entity_id+"\nArtist or artist id is not valid: "+!isArtistValid);
                    return isArtistValid;
                }
            }
        }else{
            log.error("For entity id : "+entity_id+ " artist validation failed, Ex artist count : "+primary_artist_count+" current count : "+artists.length());
        }
        return isArtistValid;
    }

    private boolean validateExEntityObjectKeyValues(int count, String url, JSONObject entity) {
        boolean isEntityValuesValid = false;
        List<String> skiplist = Arrays.asList(SimilarAlbumsTd.SKIPLIST);
        if(count == 0)
            CURRENT_KEYS = helper.keys(entity);

        isEntityValuesValid = helper.validateJSONObjectValueBasedOnKeys(CURRENT_KEYS, entity, skiplist);
        if(!isEntityValuesValid){
            log.error("\nFor Url : "+url+"\ntentity_id : "+entity.getString("entity_id").trim().toString()+"\nvalue validation failed : "+!isEntityValuesValid);
        }
        Assert.assertEquals(isEntityValuesValid, true);
        return isEntityValuesValid;
    }

    private boolean validateExEntityObjectKeys(String url, JSONObject entity) {
        List<Object> current_obj_keys = helper.keys(entity);
        List<String> exList = Arrays.asList(SimilarAlbumsTd.EX_ENTITY_KEY);
        return helper.compareList(current_obj_keys, exList);
    }

    @Step("validating basics for url {0}, and response {1}")
    private boolean validateCommons(String url, JSONObject response) {
        int count = Integer.parseInt(response.getString("count").toString().trim());
        int status = Integer.parseInt(response.optString("status").toString().trim());
        int entities = response.getJSONArray("generic_entities").length();
        
        if(status == 1 && count == entities && entities == SimilarAlbumsTd.ALBUM_COUNT){
            return true;
        }else{
            log.error("count  "+count+"\nstatus : "+status+"\nentities : "+entities+"\nexpected count : "+SimilarAlbumsTd.ALBUM_COUNT);
        }
        return false;
    }
}