package logic_controller;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.asserts.SoftAssert;
import common.Helper;
import config.Endpoints;
import io.qameta.allure.Step;

/**
 * @author Umesh Shukla
 */

public class ArtistController {

    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(ArtistController.class);

    /**
     * Prep Url to hit similar artist api
     * @param baseurl
     * @param artist_id
     * @return
     */
    public String prepareUrl(int flag, String baseurl, int artist_id) {
        if(flag == 0){
            return baseurl+Endpoints.similarArtist+artist_id;
        }else{
            return baseurl+Endpoints.similarArtistInfo+artist_id;
        }
    }

    /** 
     * @param url
     * @param artist
     */
    @Step("Detailed verification of url : {0}, artist : {1}")
	public boolean validateArtistData(String url, JSONArray artistList) {
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> atw = new ArrayList<>();
        ArrayList<String> artworks = new ArrayList<>();
        ArrayList<String> artwork_175x175 = new ArrayList<>();

        if(artistList.length() <= 0){
            log.warn("Artist List is empty in response!");
            return true;
        }

        Iterator<Object> artistItr = artistList.iterator();
        while(artistItr.hasNext()){
            JSONObject artist = (JSONObject) artistItr.next();

            long artist_id = Long.parseLong(artist.getString("artist_id").toString().trim());
            String name = artist.optString("name").toString().trim();
            String seokey = artist.optString("seokey").toString().trim();

            softAssert.assertEquals(artist_id > 0, true, "Artist id is not numeric value!");
            softAssert.assertEquals(name.length() > 0, true, "Artist name is not valid!");
            softAssert.assertEquals(!seokey.isEmpty(), true, "Seo Key can't be empty value!");

            atw.add(artist.optString("atw").toString().trim());
            artworks.add(artist.optString("artwork").toString().trim());
            artwork_175x175.add(artist.optString("artwork_175x175").toString().trim());

            JSONArray generList = artist.getJSONArray("gener");
            boolean isGenerValidated = validateGener(url, artist_id, name,  generList);
            softAssert.assertEquals(isGenerValidated, true, "Gener validation failed!");
            softAssert.assertAll();
        }

        boolean isAtwValidated = helper.validateActiveLinks(atw);
        boolean isArtworksValidated = helper.validateActiveLinks(artworks);
        boolean isArtwork_175x175Validated = helper.validateActiveLinks(artwork_175x175);
        if(isAtwValidated && isArtworksValidated && isArtwork_175x175Validated){
            return true;
        }else{
            softAssert.assertEquals(isAtwValidated, true, "Atw validation failed!");
            softAssert.assertEquals(isArtworksValidated, true, "Artwork validation failed!");
            softAssert.assertEquals(isArtwork_175x175Validated, true, "Artwork_175x175 validation failed!");
            softAssert.assertAll();
        }
        return false;
	}

    private boolean validateGener(String url, long artist_id,  String artist_name, JSONArray generList) {
        String notAvl = "N/A";
        boolean isGenerValid = false;
        ArrayList<String> geners = new ArrayList<>();
        if(generList.length() <= 0){
            log.warn("For artist id "+artist_id+ " no gener found!");
            return true;
        }

        for(int i = 0; i<generList.length(); i++){
            isGenerValid = false;
            long genre_id = 0L;
            String name = "";
            SoftAssert softAssert = new SoftAssert();
            JSONObject gener = generList.getJSONObject(i);
            String id = gener.getString("genre_id").toString().trim();
            
            if(id.isEmpty()){
                genre_id = 1;
                name = notAvl;
            }else{
                genre_id = Long.parseLong(id);
                name = gener.optString("name").toString().trim();
            }
            
            softAssert.assertEquals(genre_id > 0, true, "Gener id is not numeric value!");
            softAssert.assertEquals(name.length() > 0, true, "Gener name is not valid!");
            softAssert.assertAll();
            geners.add(name);
            isGenerValid = true;
        }

        String artist_geners = geners.toString().replaceAll("[\\[\\]\\(\\)]", "").trim();
        if(artist_geners.contains(notAvl)){
            // log.info("Url is : "+url);
            log.info(artist_name+ " having geners are : "+artist_geners);
        }

        return isGenerValid;
    }

    /**
     * SimilarArtistEntityInfo
     * @param url
     * @param entities
     */
	public boolean validateCommonEntityDetails(String url, JSONArray entities) {
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> atw = new ArrayList<>();
        ArrayList<String> artworks = new ArrayList<>();

        if(entities.length() <= 0){
            log.warn("Entity List is empty in response!");
            return true;
        }

        Iterator<Object> entityItr = entities.iterator();
        while(entityItr.hasNext()){
            JSONObject entity = (JSONObject) entityItr.next();

            long entity_id = Long.parseLong(entity.getString("entity_id").toString().trim());
            String entity_type = entity.optString("entity_type").toString().trim();
            String name = entity.optString("name").toString().trim();
            String seokey = entity.optString("seokey").toString().trim();

            softAssert.assertEquals(entity_id > 0, true, "Entity id is not numeric value!");
            softAssert.assertEquals(!entity_type.isEmpty(), true, "Entity type Key can't be empty value!");
            softAssert.assertEquals(name.length() > 0, true, "Artist name is not valid!");
            softAssert.assertEquals(!seokey.isEmpty(), true, "Seo Key can't be empty value!");

            atw.add(entity.optString("atw").toString().trim());
            artworks.add(entity.optString("artwork").toString().trim());
            softAssert.assertAll();
        }

        boolean isAtwValidated = helper.validateActiveLinks(atw);
        boolean isArtworksValidated = helper.validateActiveLinks(artworks);
        if(isAtwValidated && isArtworksValidated){
            return true;
        }else{
            softAssert.assertEquals(isAtwValidated, true, "Atw validation failed!");
            softAssert.assertEquals(isArtworksValidated, true, "Artwork validation failed!");
            softAssert.assertAll();
        }

        return false;
    }

    /**
     * @param url
     * @param entities
     * @return
     */
	public boolean validateEntityInfo(String url, JSONArray entities) {
        SoftAssert softAssert = new SoftAssert();
        if(entities.length() <= 0){
            log.warn("Entity List is empty in response!");
            return true;
        }

        Iterator<Object> entityItr = entities.iterator();
        while(entityItr.hasNext()){
            JSONObject entity = (JSONObject) entityItr.next();
            long entity_id = Long.parseLong(entity.getString("entity_id").toString().trim());
            String name = entity.optString("name").toString().trim();
            JSONArray entityInfo = entity.getJSONArray("entity_info");
            if(entityInfo.length() > 0){
                for(int i = 0; i<entityInfo.length(); i++){
                    JSONObject info =  (JSONObject) entityInfo.get(i);
                    String key = info.optString("key").toString().trim();
                    softAssert.assertEquals(key.length() > 0, true, "Entity Info Key can't be empty!");
                    if(i == 0){
                        JSONArray generValues = info.getJSONArray("value");
                        boolean isGenerValidated = validateGener(url, entity_id, name, generValues);
                        softAssert.assertEquals(isGenerValidated, true, "Gener validation failed!");
                    }else{
                        long value = Long.parseLong(info.optString("value").toString().trim());
                        softAssert.assertEquals(value > 0, true, "Entity Info value can't be less than 0!");
                    }
                }
            }else{
                log.error("Entity info is empty for url : "+url);
                softAssert.assertEquals(entityInfo.length() > 0, true);
            }
            softAssert.assertAll();
            return true;
        }
		return false;
	}
}
