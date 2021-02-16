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

public class HashTagVibesController {

    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(HashTagVibesController.class);
    
    /**
     * Generates Hit Url
     * @param baseurl
     * @param hashtag_name
     * @return
     */
    public String prepareUrl(String baseurl, String hashtag_name){
        return baseurl+Endpoints.vibesHashTag+hashtag_name;
    }

    @Step("validating entity info for hashtag name {0}")
	public boolean validateEntityInfo(String hashtag_name, JSONArray entities) {
        boolean isEntityBasicvalidated = false;
        ArrayList<String> artworks = new ArrayList<>();
        SoftAssert softAssert = new SoftAssert();
        if(entities.length() <= 0){
            return isEntityBasicvalidated;
        }

        Iterator<Object> entityItr = entities.iterator();
        while(entityItr.hasNext()){
            JSONObject entity = (JSONObject) entityItr.next();

            String entity_id = entity.getString("entity_id").trim();
            softAssert.assertEquals(entity_id.length() > 0, true, "entity_id should not be empty!");

            String entity_type = entity.optString("entity_type").toString().trim();
            softAssert.assertEquals(entity_type.length() > 0, true, entity_id+"_"+hashtag_name+" : entity_type should not be empty!");

            String seokey = entity.optString("seokey").toString().trim();
            softAssert.assertEquals(seokey.length() > 0, true, entity_id+"_"+hashtag_name+" : Seo-key should not be empty!");

            // String name = entity.optString("name").toString().trim();
            // softAssert.assertEquals(name.length() >= 0, true, entity_id+"_"+hashtag_name+" : language should not be empty!");

            // String language = entity.optString("language").toString().trim();
            // softAssert.assertEquals(language.length() > 0, true, entity_id+"_"+hashtag_name+" : language should not be empty!");

            String artwork = entity.optString("artwork").toString().trim();
            String atw = entity.optString("atw").toString().trim();
            if(atw.equals(artwork)){
                artworks.add(atw);
            }else{
                artworks.add(artwork);
                log.warn(entity_id +" artwork and atw not matching, please check for hastag : "+hashtag_name);
            }

            int favorite_count = Integer.parseInt(entity.optString("favorite_count").toString().trim());
            softAssert.assertEquals(favorite_count >= 0, true, entity_id+"_"+hashtag_name+" : favorite_count should be greater than or equal to zero!");

            int user_favorite = Integer.parseInt(entity.optString("user_favorite").toString().trim());
            softAssert.assertEquals(user_favorite >= 0, true, entity_id+"_"+hashtag_name+" : favorite_count should be greater than or equal to zero!");
            softAssert.assertAll();
            isEntityBasicvalidated = true;
        }

        if(artworks.size() > 0){
            isEntityBasicvalidated = helper.validateActiveLinks(artworks);
        }

        return isEntityBasicvalidated;
    }

    public boolean validateEntityMapData(String hashtag_name, JSONArray entities, String key){
        boolean isShortTrackValid = false;
        if(entities.length() <= 0){
            return isShortTrackValid;
        }

        Iterator<Object> entityItr = entities.iterator();
        while(entityItr.hasNext()){
            JSONObject entity = (JSONObject) entityItr.next();
            String entity_id = entity.getString("entity_id").trim();
            JSONObject entity_map = entity.getJSONObject("entity_map");
            if(key == null){
                isShortTrackValid = validateEntityMapInfo(hashtag_name, entity_id, entity_map);
            }else{
                switch (key) {
                    case "short_track":
                        JSONArray short_track = entity_map.getJSONArray(key);
                        isShortTrackValid = validateShortTrack(hashtag_name, entity_id, key,  short_track);
                    break;

                    case "hashtags":
                        JSONArray hashtags = entity_map.getJSONArray(key);
                        isShortTrackValid = validateHastags(hashtag_name, entity_id, key, hashtags);
                    break;

                    case "artist":
                        JSONArray artists = entity_map.getJSONArray(key);
                        isShortTrackValid = validateArtist(hashtag_name, entity_id, key, artists);
                    break;

                    case "album":
                        JSONArray albums = entity_map.getJSONArray(key);
                        isShortTrackValid = validateAlbum(hashtag_name, entity_id, key, albums);
                    break;

                    case "track":
                        JSONArray tracks = entity_map.getJSONArray(key);
                        isShortTrackValid = validateTracks(hashtag_name, entity_id, key, tracks);
                    break;

                    default:
                        log.info(key+ " is not expected!");
                    break;
                }
            }
        }

        return isShortTrackValid;
    }

    private boolean validateEntityMapInfo(String hashtag_name, String entity_id, JSONObject entity_map) {
        boolean isEntityMapInfoValidated = false;
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> atw_gifs = new ArrayList<>();

        String atw_gif = entity_map.optString("atw_gif").toString().trim();
        softAssert.assertEquals(atw_gif.length() > 0, true, entity_id+"_"+hashtag_name+" : artwork should not be empty!");
        atw_gifs.add(atw_gif);

        int like_count = Integer.parseInt(entity_map.optString("like_count").toString().trim());
        softAssert.assertEquals(like_count >= 0, true, entity_id+"_"+hashtag_name+" : like_count should not be empty!");

        int download_count = Integer.parseInt(entity_map.optString("download_count").toString().trim());
        softAssert.assertEquals(download_count >= 0, true, entity_id+"_"+hashtag_name+" : download_count should not be empty!");

        int share_count = Integer.parseInt(entity_map.optString("share_count").toString().trim());
        softAssert.assertEquals(share_count >= 0, true, entity_id+"_"+hashtag_name+" : share_count should not be empty!");

        int view_count = Integer.parseInt(entity_map.optString("view_count").toString().trim());
        softAssert.assertEquals(view_count >= 0, true, entity_id+"_"+hashtag_name+" : view_count should not be empty!");

        int popularity = Integer.parseInt(entity_map.optString("popularity").toString().trim());
        softAssert.assertEquals(popularity >= 0, true, entity_id+"_"+hashtag_name+" : popularity should not be empty!");

        int duration = Integer.parseInt(entity_map.optString("duration").toString().trim());
        softAssert.assertEquals(duration > 0, true, entity_id+"_"+hashtag_name+" : duration should not be empty!");

        String vert_vd = entity_map.optString("vert_vd").toString().trim();
        softAssert.assertEquals(vert_vd.length() > 0, true, entity_id+"_"+hashtag_name+" : vert_vd should not be empty!");

        softAssert.assertAll();
        isEntityMapInfoValidated = true;

        return isEntityMapInfoValidated;
    }

    private boolean validateShortTrack(String hashtag_name, String entity_id, String key, JSONArray short_tracks) {
        boolean isShortTrackValid = false;
        SoftAssert softAssert = new SoftAssert();
        ArrayList<String> artworks = new ArrayList<>();

        if(short_tracks.length() <= 0){
            // log.warn("For : "+hashtag_name+ " entity id : "+entity_id+" and key : "+key+ " array is empty.");
            return true;
        }

        for(int i = 0; i<short_tracks.length(); i++){
            JSONObject short_track = short_tracks.getJSONObject(i);

            String track_id = short_track.getString("track_id").trim();
            softAssert.assertEquals(track_id.length() > 0, true, entity_id+"_"+hashtag_name+" : track_id should not be empty!");

            String artwork = short_track.optString("artwork").toString().trim();
            softAssert.assertEquals(artwork.length() > 0, true, entity_id+"_"+hashtag_name+" : artwork should not be empty!");

            String seokey = short_track.optString("seokey").toString().trim();
            softAssert.assertEquals(seokey.length() > 0, true, entity_id+"_"+hashtag_name+" : seokey should not be empty!");
            artworks.add(artwork);

            softAssert.assertAll();
            isShortTrackValid = true;
        }

        if(artworks.size() > 0){
            isShortTrackValid = helper.validateActiveLinks(artworks);
        }

        return isShortTrackValid;
    }

    private boolean validateHastags(String hashtag_name, String entity_id, String key, JSONArray hashtags) {
        boolean isHastagsValid = false;
        SoftAssert softAssert = new SoftAssert();

        if(hashtags.length() <= 0){
            // log.warn("For : "+hashtag_name+ " entity id : "+entity_id+" and key : "+key+ " array is empty.");
            return true;
        }

        for(int i = 0; i<hashtags.length(); i++){
            JSONObject hastag = hashtags.getJSONObject(i);
            String hashtag_id = hastag.getString("hashtag_id").trim();
            softAssert.assertEquals(hashtag_id.length() > 0, true, entity_id+"_"+hashtag_name+" : hashtag_id should not be empty!");

            String seokey = hastag.optString("seokey").toString().trim();
            softAssert.assertEquals(seokey.length() > 0, true, entity_id+"_"+hashtag_name+" : seokey should not be empty!");

            boolean dl_url = hastag.optString("dl_url").toString().trim().startsWith("gaana://view/vibeshashtag/");
            softAssert.assertEquals(dl_url, true, entity_id+"_"+hashtag_name+" : dl_url should not be empty!");

            String display_text = hastag.optString("display_text").toString().trim();
            softAssert.assertEquals(display_text.length() > 0, true, entity_id+"_"+hashtag_name+" : display_text should not be empty!");
            softAssert.assertAll();
            isHastagsValid = true;
        }

        return isHastagsValid;
    }

    private boolean validateArtist(String hashtag_name, String entity_id, String key, JSONArray artists) {
        boolean isArtistValid = false;
        ArrayList<String> atwList = new ArrayList<>();
        ArrayList<String> artworks = new ArrayList<>();
        SoftAssert softAssert = new SoftAssert();

        if(artists.length() <= 0){
            // log.warn("For : "+hashtag_name+ " entity id : "+entity_id+" and key : "+key+ " array is empty.");
            return true;
        }

        for(int i = 0; i<artists.length(); i++){
            JSONObject artist = artists.getJSONObject(i);

            String artist_id = artist.getString("artist_id").trim();
            softAssert.assertEquals(artist_id.length() > 0, true, entity_id+"_"+hashtag_name+" : artist_id should not be empty!");

            String name = artist.optString("name").trim();
            softAssert.assertEquals(name.length() > 0, true, entity_id+"_"+hashtag_name+" : name should not be empty!");

            String atw = artist.optString("atw").toString().trim();
            softAssert.assertEquals(atw.length() > 0, true, entity_id+"_"+hashtag_name+" : atw should not be empty!");
            atwList.add(atw);

            String artwork = artist.optString("artwork").toString().trim();
            softAssert.assertEquals(artwork.length() > 0, true, entity_id+"_"+hashtag_name+" : artwork should not be empty!");
            artworks.add(artwork);

            softAssert.assertAll();
            isArtistValid = true;
        }

        if(atwList.size() > 0){
            isArtistValid = helper.validateActiveLinks(atwList);
            if(isArtistValid){
                if(artworks.size() > 0){
                    isArtistValid = helper.validateActiveLinks(artworks);
                }
            }
        }

        return isArtistValid;
    }

    private boolean validateAlbum(String hashtag_name, String entity_id, String key, JSONArray albums) {
        boolean isAlbumValid = false;
        SoftAssert softAssert = new SoftAssert();

        if(albums.length() <= 0){
            // log.warn("For : "+hashtag_name+ " entity id : "+entity_id+" and key : "+key+ " array is empty.");
            return true;
        }

        for(int i = 0; i<albums.length(); i++){
            JSONObject album = albums.getJSONObject(i);

            String album_id = album.getString("album_id").trim();
            softAssert.assertEquals(album_id.length() > 0, true, entity_id+"_"+hashtag_name+" : album_id should not be empty!");

            String name = album.optString("name").trim();
            softAssert.assertEquals(name.length() > 0, true, entity_id+"_"+hashtag_name+" : name should not be empty!");

            String seokey = album.optString("seokey").toString().trim();
            softAssert.assertEquals(seokey.length() > 0, true, entity_id+"_"+hashtag_name+" : seokey should not be empty!");

            softAssert.assertAll();
            isAlbumValid = true;
        }

        return isAlbumValid;
    }

    private boolean validateTracks(String hashtag_name, String entity_id, String key, JSONArray tracks) {
        boolean isTracksValid = false;
        ArrayList<String> atwList = new ArrayList<>();
        ArrayList<String> artworks = new ArrayList<>();
        ArrayList<String> artworksWeb = new ArrayList<>();
        ArrayList<String> artworksLarge = new ArrayList<>();
        SoftAssert softAssert = new SoftAssert();

        if(tracks.length() <= 0){
            // log.warn("For : "+hashtag_name+ " entity id : "+entity_id+" and key : "+key+ " array is empty.");
            return true;
        }

        for(int i = 0; i<tracks.length(); i++){
            JSONObject track = tracks.getJSONObject(i);

            String track_id = track.getString("track_id").trim();
            softAssert.assertEquals(track_id.length() > 0, true, entity_id+"_"+hashtag_name+" : album_id should not be empty!");

            String atw = track.optString("atw").toString().trim();
            softAssert.assertEquals(atw.length() > 0, true, entity_id+"_"+hashtag_name+" : atw should not be empty!");
            atwList.add(atw);

            String artwork = track.optString("artwork").toString().trim();
            softAssert.assertEquals(artwork.length() > 0, true, entity_id+"_"+hashtag_name+" : artwork should not be empty!");
            artworks.add(artwork);

            String artwork_web = track.optString("artwork_web").toString().trim();
            softAssert.assertEquals(artwork_web.length() > 0, true, entity_id+"_"+hashtag_name+" : artwork_web should not be empty!");
            artworksWeb.add(artwork_web);

            String artwork_large = track.optString("artwork_large").toString().trim();
            softAssert.assertEquals(artwork_large.length() > 0, true, entity_id+"_"+hashtag_name+" : artwork_large should not be empty!");
            artworksLarge.add(artwork_large);

            String title = track.optString("title").trim();
            softAssert.assertEquals(title.length() > 0, true, entity_id+"_"+hashtag_name+" : title should not be empty!");


            String seokey = track.optString("seokey").toString().trim();
            softAssert.assertEquals(seokey.length() > 0, true, entity_id+"_"+hashtag_name+" : seokey should not be empty!");

            softAssert.assertAll();
            isTracksValid = true;
        }

        if(isTracksValid && atwList.size() > 0){
            isTracksValid = helper.validateActiveLinks(atwList);
        }

        if(isTracksValid && artworks.size() > 0){
            isTracksValid = helper.validateActiveLinks(artworks);
        }

        if(isTracksValid && artworksWeb.size() > 0){
            isTracksValid = helper.validateActiveLinks(artworksWeb);
        }

        if(isTracksValid && artworksLarge.size() > 0){
            isTracksValid = helper.validateActiveLinks(artworksLarge);
        }

        return isTracksValid;
    }
}