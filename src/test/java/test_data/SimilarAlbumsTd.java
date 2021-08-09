package test_data;

public class SimilarAlbumsTd {
    public static int ALBUM_COUNT = 15;
    public final static int SA_INVOCATION = 4; // depends on number of album id variable
    public static int ALBUM_IDS []  = {1627, 1628, 1629, 1630};

    /**
     * Similar Album Entity Info Test Data.
     */
    public final static int SAEI_INVOCATION = 3; // depends on ALBUM_IDS
    public static String [] VALIDATOR_KEYS = {"Basic", "Entity_keys", "Entity_Values", "Artworks", "Primary Artist"};
    public static String [] EX_ENTITY_KEY = {"seokey","entity_type","atw","user_favorite","name","generic_entity_info","language",
        "favorite_count","artwork","entity_id","artwork_medium","premium_content"};

    public static String SKIPLIST [] = {"generic_entity_info"};
    public static String ARTWORK_TYPES [] = {"atw", "artwork", "artwork_medium"};

}
