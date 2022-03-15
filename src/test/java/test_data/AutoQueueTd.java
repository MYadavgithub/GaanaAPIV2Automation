package test_data;
import com.google.gson.JsonObject;
import org.json.JSONObject;

public class AutoQueueTd {
    
    public final static int INVOCATION = 3;
    public static String [] TRACK_IDS = {"1540383", "810", "1014", "1535815", "17444240"};
    public static String TD_FILE_NAME = "autoqueue.json";
    public static String [] VALIDATOR_KEYS ={"TRACK_KEY_VAL", "ARTWORKS", "PREMIUM_KEY", "RELEASE_DATE", "LANG_LANG_ID" , "GENER", "ARTIST",
        "STREAM_URL", "TRACK_FORMAT"};

    public static String EX_TRACKS_KEYS []= {"album_id", "album_title", "albumseokey", "artist", "artwork", "artwork_large", 
        "artwork_web", "atw", "content_source", "country", "display_global", "duration", "gener", "http", "https", "is_local", 
        "is_most_popular", "is_premium", "is_sonos_playable", "isrc", "language", "language_id", "lyrics_type", "lyrics_url",
        "mobile", "parental_warning", "popularity", "premium_content", "rating", "release_date", "rtmp", "rtsp", "sap_id",
        "secondary_language", "seokey", "stream_type", "stream_url", "total_favourite_count", "track_format", "track_id",
        "track_title", "user_favorite", "user_rating", "vendor"
    };

    public static String RTP_REMOVE_FROM_VALUE_VALIDATION [] = {"artist", "track_format", "gener"};
    public static String ARTWORK_TYPES [] = {"atw", "artwork", "artwork_web", "artwork_large"};

    /** SimilarTrackGeneric TD */
    public static String EX_ENTITY_TYPE = "Track";

    /** getSuggestedSongs Td */
    public final static int SS_INVOCATION = 4;
    public static String DEVICE_ID = "Redmi Y3_aa8f0ef7c2354e12";
    public static String TRACKS = "1,66701,480,1014";
    public static int [] TYPE = {1, 1, 2, 3}; // one to times to handle type calls in api

    public static int trackCount(String type){
        JsonObject obj = new JsonObject();
        obj.addProperty("1", 20);
        obj.addProperty("2", 3);
        obj.addProperty("3", 10);
        return Integer.parseInt(obj.get(type).toString().trim());
    }

    public static String GSSPPostData(int type){
        JSONObject obj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        dataObj.put("trackIds", TRACKS);
        dataObj.put("type", type);
        obj.put("data", dataObj);
        return obj.toString();
    }
}