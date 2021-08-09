package logic_controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import common.Helper;
import config.Endpoints;
import test_data.SimilarAlbumsTd;
import utils.CommonUtils;
import org.testng.asserts.SoftAssert;

public class SimilarAlbumController {

    int VALIDATE_COMMON = 1;
    int VALIDATE_TRACK_COUNT = 2;
    int VALIDATE_ARTWORKS = 3;
    int VALIDATE_CUSTOM_ARTWORKS = 4;
    int VALIDATE_PRIMARY_ARTIST = 5;
    int VALIDATE_ARTIST = 6;
    int VALIDATE_GENER = 7;
    int RELEASE_YEAR_AND_PREMIUM_CONTENT = 8;
    Helper helper = new Helper();
    CommonUtils util = new CommonUtils();
    private static Logger log = LoggerFactory.getLogger(SimilarAlbumController.class);
    
    public String createUrl(String baseurl, int album_id){
        return baseurl+Endpoints.SIMILAR_ALBUMS+album_id;
    }

    public boolean validateResponseHavingData(JSONObject response){
        JSONArray album = response.getJSONArray("album");
        int count = Integer.parseInt(response.optString("count").toString().trim());
        if(count == SimilarAlbumsTd.ALBUM_COUNT && album.length() == SimilarAlbumsTd.ALBUM_COUNT){
            return true;
        }
        return false;
    }

    private boolean switchCasesForValidations(int flag, JSONObject response){
        boolean isStepValidated = false;
        JSONArray albums_arr =  response.getJSONArray("album");
        Iterator<Object> albums = albums_arr.iterator();
        while(albums.hasNext()){
            JSONObject album = (JSONObject) albums.next();
            switch (flag) {
                case 1:
                    isStepValidated = validateBasicsData(album);
                    if(!isStepValidated){
                        break;
                    }
                break;

                case 2:
                    isStepValidated = validateTrackCountAndTrackids(album);
                break;

                case 3:
                    isStepValidated = validateArtwork(album);
                break;

                case 4:
                    isStepValidated = validateCartworks(album);
                break;

                case 5:
                    isStepValidated = validatePrimaryArtists(album);
                break;

                case 6:
                    isStepValidated = validatePrimaryArtists(album);
                break;

                case 7:
                    isStepValidated = validateGeners(album);
                break;

                case 8:
                    isStepValidated = validateReleaseYearAndPremiumContents(album);
                break;
            }
        }
        return isStepValidated;
    }

    public boolean validateBasics(JSONObject response) {
        return switchCasesForValidations(VALIDATE_COMMON, response);
    }

    private boolean validateBasicsData(JSONObject album) {
        SoftAssert softAssert = new SoftAssert();
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        softAssert.assertEquals(album_id > 0, true, "album_id validation got failed!");

        String seokey = album.optString("seokey").toString().trim();
        softAssert.assertEquals(seokey.length() > 0, true, "seokey validation got failed for album id : "+album_id);

        String title = album.optString("title").toString().trim();
        softAssert.assertEquals(title.length() > 0, true, "title validation got failed for album id : "+album_id);

        String language = album.optString("language").toString().trim();
        softAssert.assertEquals(language.length() > 0, true, "language validation got failed for album id : "+album_id);

        int favorite_count = Integer.parseInt(album.getString("favorite_count").toString().trim());
        softAssert.assertEquals(favorite_count > 0, true, "favorite_count validation got failed for album id : "+album_id);

        int status = Integer.parseInt(album.getString("status").toString().trim());
        softAssert.assertEquals(status == 1, true, "status validation got failed for album id : "+album_id);

        softAssert.assertAll();

        return true;
    }

    public boolean validatetrackIds(JSONObject response) {
        return switchCasesForValidations(VALIDATE_TRACK_COUNT, response);
    }

    private boolean validateTrackCountAndTrackids(JSONObject album) {
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        int trackcount = Integer.parseInt(album.getString("trackcount").toString().trim());
        String trackids = album.getString("trackids").toString().trim();
        List<String> trackList = Arrays.asList(trackids.split(","));
        if(trackcount != trackList.size()){
            log.error("Trackcount value not matching with trackids value for album_id : "+album_id);
            return false;
        }
        return true;
    }

    public boolean validateArtworks(JSONObject response) {
        return switchCasesForValidations(VALIDATE_ARTWORKS, response);
    }

    private boolean validateArtwork(JSONObject album) {
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        ArrayList<String> artworks = new ArrayList<>();
        String atw = album.optString("atw").toString().trim();
        artworks.add(atw);
        String artwork = album.optString("artwork").toString().trim();
        artworks.add(artwork);
        if(artworks.size() == 2){
            boolean result = helper.validateActiveLinks(artworks);
            artworks.clear();
            return result;
        }else{
            Assert.assertEquals(artworks.size() == 2, true, "Artwork or atw not found in album_id "+album_id);
        }
        return false;
    }

    public boolean validateCustomArtworks(JSONObject response) {
        return switchCasesForValidations(VALIDATE_CUSTOM_ARTWORKS, response);
    }

    private boolean validateCartworks(JSONObject album) {
        ArrayList<String> artworks = new ArrayList<>();
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        JSONObject custom_artworks = album.getJSONObject("custom_artworks");
        String x_40 = custom_artworks.optString("40x40").toString().trim();
        artworks.add(x_40);
        String x_80 = custom_artworks.optString("80x80").toString().trim();
        artworks.add(x_80);
        String x_110 = custom_artworks.optString("110x110").toString().trim();
        artworks.add(x_110);
        String x_175 = custom_artworks.optString("175x175").toString().trim();
        artworks.add(x_175);
        String x_480 = custom_artworks.optString("480x480").toString().trim();
        artworks.add(x_480);

        if(artworks.size() == 5){
            boolean result = helper.validateActiveLinks(artworks);
            artworks.clear();
            return result;
        }else{
            Assert.assertEquals(artworks.size() == 2, true, "Custom Artwork or atw not found in album_id "+album_id);
        }
        return false;
    }

    public boolean validatePrimaryArtist(JSONObject response) {
        return switchCasesForValidations(VALIDATE_PRIMARY_ARTIST, response);
    }

    private boolean validatePrimaryArtists(JSONObject album) {
        boolean isArtistValid = false;
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        JSONArray primaryartists = album.getJSONArray("primaryartist");
        if(primaryartists.length() <= 0){
            Assert.assertEquals(primaryartists.length() > 0, true, "Primary artist empty in album_id : "+album_id);
            return false;
        }

        SoftAssert softAssert = new SoftAssert();
        for(int i = 0; i<= primaryartists.length()-1; i++){

            JSONObject artist = primaryartists.getJSONObject(i);
            String artist_id = artist.getString("artist_id").toString().trim();
            softAssert.assertEquals(artist_id.length() > 0, true, "artist_id validation got failed for album id : "+album_id);

            String name = artist.optString("name").toString().trim();
            softAssert.assertEquals(name.length() > 0, true, "artist name validation got failed for album id : "+album_id);

            String seokey = artist.optString("seokey").toString().trim();
            softAssert.assertEquals(seokey.length() > 0, true, "artist seokey validation got failed for album id : "+album_id);

            softAssert.assertAll();
            isArtistValid = true;
        }

        return isArtistValid;
    }

    public boolean validateArtist(JSONObject response) {
        return switchCasesForValidations(VALIDATE_ARTIST, response);
    }

    public boolean validateGener(JSONObject response) {
        return switchCasesForValidations(VALIDATE_GENER, response);
    }

    private boolean validateGeners(JSONObject album) {
        boolean isGenerValid = false;
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        JSONArray geners = album.getJSONArray("gener");
        if(geners.length() <= 0){
            Assert.assertEquals(geners.length() > 0, true, "gener empty in album_id : "+album_id);
            return false;
        }

        SoftAssert softAssert = new SoftAssert();
        for(int i = 0; i<= geners.length()-1; i++){
            int genre_id = 0;
            String name = "";
            JSONObject gener = geners.getJSONObject(i);
            String str_gener_id = gener.getString("genre_id").toString().trim();
            String str_name = gener.optString("name").toString().trim();
            if(str_gener_id.length() > 0){
                genre_id = Integer.parseInt(str_gener_id);
                name = str_name;
            }else{
                genre_id = 111;
                name = "N/A";
                log.info("Album_id : "+album_id+" Not having Gener Data!");
            }

            softAssert.assertEquals(genre_id > 0, true, "genre_id validation got failed for album id : "+album_id);
            softAssert.assertEquals(name.length() > 0, true, "Gener name validation got failed for album id : "+album_id);

            softAssert.assertAll();
            isGenerValid = true;
        }
        return isGenerValid;
    }

    public boolean validateReleaseYearAndPremiumContent(JSONObject response) {
        return switchCasesForValidations(RELEASE_YEAR_AND_PREMIUM_CONTENT, response);
    }

    private boolean validateReleaseYearAndPremiumContents(JSONObject album) {
        int album_id = Integer.parseInt(album.getString("album_id").toString().trim());
        String release_date = album.optString("release_date").toString().trim();
        int premium_content = Integer.parseInt(album.getString("premium_content").toString().trim());

        if(premium_content == 1){
            log.info("Album id : "+album_id+ " is premium content.");
        }

        boolean isRecordValidated = CommonUtils.validateYears(release_date);
        Assert.assertEquals(isRecordValidated, true, "Release year is not valid for album_id : "+album_id);
        return isRecordValidated;
    }
}