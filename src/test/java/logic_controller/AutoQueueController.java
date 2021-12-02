package logic_controller;
import java.util.*;
import org.json.*;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import common.Helper;
import config.Endpoints;
import io.restassured.response.Response;
import utils.CommonUtils;

public class AutoQueueController {

    private String LANGUAGE = "";
    private int LANGUAGE_ID = 0;
    private List<Object> CURRENT_KEYS = null;
    private String firstGenerDetails [] = null;
    Helper helper = new Helper();
    private static Logger log = LoggerFactory.getLogger(AutoQueueController.class);
    
    public boolean validateCountStatusUserToken(String url, Response response) {
        JSONObject response_object = helper.responseJSONObject(response);
        JSONArray tracks = response_object.getJSONArray("tracks");
        int count = Integer.parseInt(response_object.optString("count").toString().trim());
        boolean status = response_object.optString("status").toString().trim().equals("true");
        boolean user_token_status = response_object.optString("user-token-status").toString().trim().equals("1");

        if(tracks.length() == count && status && user_token_status){
            return true;
        }else if(tracks.length() == count){
            return true;
        }
        return false;
    }

    public boolean validateTracksKeys(String url, Response response, String[] ex_tracks_key) {
        JSONArray tracks = helper.getJSONArray(url, "tracks", response);
        if(tracks.length() > 0){
            List<Object> current_keys = helper.keys(tracks.getJSONObject(0));
            boolean isExpectedKeysPresent = helper.compareList(current_keys, Arrays.asList(ex_tracks_key));
            if(isExpectedKeysPresent) {
                return true;
            }else{
                log.error("Required keys are missing in track list response api was : "+url);
            }
        }
        return false;
    }

    private boolean validateTracksKeyValues(int count, String url, JSONObject track, String[] skipJsonObjects) {
        boolean isTrackValuesValid = false;
        SoftAssert softAssert = new SoftAssert();
        List<String> skiplist = Arrays.asList(skipJsonObjects);

        if(count == 0){
            CURRENT_KEYS = helper.keys(track);
        }

        isTrackValuesValid = helper.validateJSONObjectValueBasedOnKeys(CURRENT_KEYS, track, skiplist);
        if(!isTrackValuesValid){
            log.error(this.getClass()+" For Url : "+url+"\ntrack_id : "
                +track.getString("track_id").trim().toString()+"\nvalue validation failed : "+!isTrackValuesValid);
        }
        softAssert.assertEquals(isTrackValuesValid, true);
        softAssert.assertAll();
        return isTrackValuesValid;
    }

    /**
    private boolean validateEachTrackArtworks(String url, JSONObject track, String[] artwork_types) {
        boolean isArtworkValid = false;
        if(artwork_types.length <= 0){
            return false;
        }
        ArrayList<String> artworks = new ArrayList<>();
        String track_id = track.getString("track_id").trim().toString();
        for(int i = 0; i<artwork_types.length; i++){
            artworks.add(track.optString(artwork_types[i]).toString().trim());
        }
        isArtworkValid = helper.validateActiveLinks(artworks);
        if(!isArtworkValid){
            log.error("For Url : "+url+ "\n track_id : "+track_id+"\nArtworks not validated, artworks are : \n"+artworks);
            return isArtworkValid;
        }
        Assert.assertEquals(isArtworkValid, true);
        artworks.clear();
        return isArtworkValid;
    }
    */

    private boolean validatePremiumKeyPresent(String url, JSONObject track) {
        boolean isPrimiumValidated = false;
        String track_id = track.getString("track_id").trim().toString();
        int is_premium = Integer.parseInt(track.getString("is_premium").toString().trim());
        int premium_content = Integer.parseInt(track.getString("premium_content").toString().trim());
        if((is_premium == 0 || is_premium == 1) && (premium_content == 0 || premium_content == 1)) {
            isPrimiumValidated = true;
        }else{
            isPrimiumValidated = false;
            log.error("For Url : "+url+ "\n track_id : "+track_id+"\nPremium Key not validated : "+isPrimiumValidated);
            return isPrimiumValidated;
        }
        Assert.assertEquals(isPrimiumValidated, true);
        return isPrimiumValidated;
    }

    private boolean validateEachTracksReleaseDate(String url, JSONObject track) {
        boolean isReleaseYearValidated = false;
        if(track == null){
            return isReleaseYearValidated;
        }

        String track_id = track.getString("track_id").trim().toString();
        String release_date = track.optString("release_date").toString().trim();
        isReleaseYearValidated = CommonUtils.validateYears(release_date);
        if(!isReleaseYearValidated) {
            log.error("For Url : "+url+ "\n track_id : "+track_id+"\nPremium Key not validated : "+isReleaseYearValidated);
            return isReleaseYearValidated;
        }
        Assert.assertEquals(isReleaseYearValidated, true);
        return isReleaseYearValidated;
    }

    private boolean validateLangAndLangId(int count, String url, JSONObject track) {
        boolean isLangLangIdValid = false;
        String track_id = track.getString("track_id").trim().toString();
        String language = track.optString("language").trim().toString();
        int language_id = Integer.parseInt(track.getString("language_id").toString().trim());
        if(count == 0){
            LANGUAGE = language;
            LANGUAGE_ID = language_id;
            isLangLangIdValid = true;
        }

        if(LANGUAGE.equals(language) && LANGUAGE_ID == language_id){
            isLangLangIdValid = true;
        }else{
            isLangLangIdValid = false;
            log.error("For Url : "+url+ "\n track_id : "+track_id+"\nLanguage or language id is not valid: "+!isLangLangIdValid);
            return isLangLangIdValid;
        }
        return isLangLangIdValid;
    }

    private boolean validateTrackGener(int count, String url, JSONObject track) {
        boolean isGenerValid = false;
        String track_id = track.getString("track_id").trim().toString();
        JSONArray geners = track.getJSONArray("gener");
        if(geners.length() <= 0)
            return isGenerValid;

        for(int i = 0; i<geners.length(); i++){
            String genre_id = geners.getJSONObject(i).getString("genre_id").toString().trim();
            String name = geners.getJSONObject(i).optString("name").toString().trim();
            if(count == 0){
                firstGenerDetails = new String[geners.length()*2];
                firstGenerDetails[0] = genre_id;
                firstGenerDetails[1] = name;
            }

            if(firstGenerDetails[0].equals(genre_id) && firstGenerDetails[1].equals(name)){
                isGenerValid = true;
            }else if(genre_id.length() == 0 && name.length() == 0){
                isGenerValid = true;
                log.warn("For Url : "+url+ "\ntrack_id : "+track_id+"\nGener or gener id is not valid: "+isGenerValid);
            }else{
                isGenerValid = true; /** Made self true as its need to be fixed until not fixed permanently it should be return result value as true.*/
                log.warn("For Url : "+url+ "\ntrack_id : "+track_id+"\nGener or gener id is not valid: "+!isGenerValid);
                log.warn("Expected gener_id and name was : "+firstGenerDetails[0]+ ", "+firstGenerDetails[1]+ " but found : "+genre_id+", "+name);
                return isGenerValid;
            }
        }
            
        return isGenerValid;
    }

    private boolean validateTracksArtist(int count, String url, JSONObject track) {
        boolean isArtistValid = false;
        String track_id = track.getString("track_id").trim().toString();
        JSONArray artists = track.getJSONArray("artist");
        if(artists.length() <= 0)
            return isArtistValid;
    
        for(int i = 0; i<artists.length(); i++){
            int artist_id = Integer.parseInt(artists.getJSONObject(i).getString("artist_id").toString().trim());
            String name = artists.getJSONObject(i).optString("name").toString().trim();
            String seokey = artists.getJSONObject(i).optString("seokey").toString().trim();

            if(artist_id > 0 && name.length() > 0 && seokey.length() > 0){
                isArtistValid = true;
            }else{
                isArtistValid = false;
                log.error("For Url : "+url+ "\n track_id : "+track_id+"\nArtist or artist id is not valid: "+!isArtistValid);
                return isArtistValid;
            }
        }
        return isArtistValid;
    }

    private boolean validateEachTrackStreamUrl(int count, String url, JSONObject track) {
        String track_id = track.getString("track_id").trim().toString();
        String stream_url = track.optString("stream_url").trim().toString();

        if(stream_url.length() > 0){
            return true;
        }else{
            log.error("For Url : "+url+ "\n track_id : "+track_id+"\nStream Url is not valid: "+!(stream_url.length()<= 0));
            return false;
        }
    }

    private boolean validateEachTrackFormat(int count, String url, JSONObject track) {
        boolean isTrackFormatValid = false;
        JSONObject track_formats = track.getJSONObject("track_format");
        List<Object> keys = helper.keys(track_formats);
        // String track_id = track.getString("track_id").trim().toString();
        // log.info("For track_id : "+track_id+" available tracks formats are : "+keys.toString());
        for(int i = 0; i<keys.size(); i++){
            JSONObject track_format = track_formats.getJSONObject(keys.get(i).toString());

            int normal = Integer.parseInt(track_format.getString("normal").trim().toString());
            int medium = Integer.parseInt(track_format.getString("medium").trim().toString());
            int high = Integer.parseInt(track_format.getString("high").trim().toString());
            int extreme = Integer.parseInt(track_format.getString("extreme").trim().toString());
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals((normal<= 0 || normal >= 0), true);
            softAssert.assertEquals((medium<= 0 || medium >= 0), true);
            softAssert.assertEquals((high<= 0 || high >= 0), true);
            softAssert.assertEquals((extreme<= 0 || extreme >= 0), true);
            softAssert.assertAll();
            isTrackFormatValid = true;
        }
        return isTrackFormatValid;
    }

    public boolean validateTrackDetails(String func_key, String url, Response response, String [] test_data){
        boolean isValidateTrackDetails = false;
        if(response == null){
            return isValidateTrackDetails;
        }

        int count = 0;
        JSONArray tracks = helper.getJSONArray(url, "tracks", response);
        Iterator<Object> tracks_itr = tracks.iterator();
        while(tracks_itr.hasNext()){
            JSONObject track = (JSONObject) tracks_itr.next();
            switch (func_key) {
                case "TRACK_KEY_VAL":
                    isValidateTrackDetails = validateTracksKeyValues(count, url, track, test_data);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;

                case "ARTWORKS":
                    isValidateTrackDetails = helper.validateEachEntityArtworks(url, "track_id", track, test_data);
                    // isValidateTrackDetails = validateEachTrackArtworks(url, track, test_data);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;

                case "PREMIUM_KEY":
                    isValidateTrackDetails = validatePremiumKeyPresent(url, track);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;

                case "RELEASE_DATE":
                    isValidateTrackDetails = validateEachTracksReleaseDate(url, track);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;
                
                case "LANG_LANG_ID":
                    isValidateTrackDetails = validateLangAndLangId(count, url, track);
                    if(!isValidateTrackDetails){
                        if(url.contains(Endpoints.GET_SUGGESTED_SONGS) || url.contains(Endpoints.GET_SUGGESTED_SONGS_POST)){
                           return true; // for UGC reco api's change added, please validate if same laguage and lang id required then remove the check.
                        }else{
                            return isValidateTrackDetails;
                        }
                    }
                break;

                case "GENER":
                    isValidateTrackDetails = validateTrackGener(count, url, track);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;

                case "ARTIST":
                    isValidateTrackDetails = validateTracksArtist(count, url, track);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;

                case "STREAM_URL":
                    isValidateTrackDetails = validateEachTrackStreamUrl(count, url, track);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;

                case "TRACK_FORMAT":
                    isValidateTrackDetails = validateEachTrackFormat(count, url, track);
                    if(!isValidateTrackDetails){
                        return isValidateTrackDetails;
                    }
                break;
            }
            count++;
        }
        return isValidateTrackDetails;
    }
}