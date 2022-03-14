package test_data;

public class AutoQueueTd {
    
    public final static int INVOCATION = 3;
    public static String [] TRACK_IDS = { "810", "1014", "1535815", "17444240","1550000"};
    //"1540384",
    public static String TD_FILE_NAME = "autoqueue.json";
    public static String [] VALIDATOR_KEYS ={"TRACK_KEY_VAL", "ARTWORKS", "PREMIUM_KEY", "RELEASE_DATE", "LANG_LANG_ID" , "GENER", "ARTIST",
        "STREAM_URL", "TRACK_FORMAT"};

    public static String EX_TRACKS_KEYS []= {"country", "artist", "rating", "language", "language_id", "premium_content", "sap_id", 
        "duration", "rtmp", "parental_warning","lyrics_url", "total_favourite_count","lyrics_type","content_source", "albumseokey", "vendor", "popularity", "stream_type", "stream_url", 
        "is_local", "artwork_large", "user_rating", "is_sonos_playable", "seokey", "track_format", "atw", "gener", 
        "track_title", "mobile", "secondary_language", "isrc", "rtsp", "artwork", "display_global",  
        "is_most_popular", "release_date", "user_favorite", "track_id", "album_id", "artwork_web", "album_title", 
        "is_premium", "http", "https"
    };
    
    public static String RTP_REMOVE_FROM_VALUE_VALIDATION [] = {"artist", "track_format", "gener", "lyrics_type","lyrics_url"};
    public static String ARTWORK_TYPES [] = {"atw", "artwork", "artwork_web", "artwork_large", "lyrics_type"};

    /** SimilarTrackGeneric TD */
    public static String EX_ENTITY_TYPE = "Track";

}