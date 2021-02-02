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
    public String prepareUrl(String baseurl, int artist_id) {
        return baseurl+Endpoints.similarArtist+artist_id;
    }

    /** 
     * @param url
     * @param artist
     */
	public boolean validateArtistData(String url, JSONArray artistList) {
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> atw = new ArrayList<>();
        ArrayList<String> artworks = new ArrayList<>();
        ArrayList<String> artwork_175x175 = new ArrayList<>();
        Iterator<Object> artistItr = artistList.iterator();

        if(artistList.length() <= 0){
            log.warn("Artist List is empty in response!");
            return true;
        }

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
            log.info("Url is : "+url);
            log.info(artist_name+ " having geners are : "+artist_geners);
        }

        return isGenerValid;
    }
}
