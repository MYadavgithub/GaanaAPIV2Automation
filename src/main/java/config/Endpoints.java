package config;

import java.util.HashMap;

public class Endpoints {

    public static String RECO_TRACKS = "/recommendation/recommendedTracks/";
    public static String RECOMMENDED_TRACK_POST = "/recommendation/recommendedTracksPost/";
    public static String SIMILAR_TRACKS_RECENTY_PLAYED = "/recommendation/SimilarTracksRecentyPlayed/";
    public static String RECOMMENDED_SONGS = "/recommendation/recommendedSongs/";
    public static String SIMILAR_TRACK_GENERIC = "/recommendation/similarTrackGeneric/";
    public static String RECOMMENDED_TRACK_IDS = "/recommendation/recommendedTrackIds/";
    public static String RECOMMENDED_TRACKS_AQ = "/recommendation/recommendedTracksAQ/";
    public static String trendingShortTrack = "/recommendation/trendingShortTrack?viewAll=";
    public static String recoTracksPartner = "/recommendation/partnership/";
    public static String vibes = "/recommendation/vibes";
    public static String moodMix = "/for-you/moodmix";
    public static String madeForYou = "/madeforyou";
    public static String deviceConsumedLanguage = "/recommendation/deviceConsumedLanguage?deviceId=";
    public static String similarArtist = "/recommendation/similarArtist/";
    public static String similarArtistInfo = "/recommendation/similarArtistEntityInfo/";
    public static String vibesHashTag = "/recommendation/hashtag/vibes?hashtag=";
    public static String podcastForYou = "/for-you/podcasts?deviceId=";
    public static String trendingHashTag = "/hashtag/trendingHashtags";
    public static String SIMILAR_ALBUMS = "/recommendation/similarAlbums/";
    public static String SIMILAR_ALBUMS_ENTITY_INFO = "/recommendation/similarAlbumsEntityInfo/";
    public static String TAG_AFFINITY_30DAYS = "/tagaffinity30days?md5DeviceId=";
    public static String TRACK_RECOMMEND = "/track/recommend/";
    public static String IS_EXISTING_DEVICE = "/isExistingDevice";
    public static String DEVICE_LANGUAGE = "/recommendation/deviceLanguage?deviceId=";
    public static String SIMILAR_TOP_SHOW = "/recommendation/SimilarTopShows/";
    public static String DEVICE_TRACK_PLAYOUTS = "/recommendation/DeviceTrackPlayouts?deviceId=";
    public static String ASSOCIATED_LANGUAGES = "/recommendation/associatedLanguages?languages=";
    public static String DAILY_MIX = "/dailyMix/";
    public static String WEEKLY_MIX = "/weeklyMix/";
    public static String DEVICE_LANGUAGE_UPDATE = "/recommendation/deviceLanguageUpdate?deviceId=";
    public static String TRACKID_ENDPOINT_HASHCODE = "/hashcode?id=";
    public static String TRACKIDS_ENDPOINT_HASHCODE = "/aes/sum/ids?ids=";
    public static String GET_URL_V1 = "/getURLV1.php?";
    public static String APP_STREAM_DECRYPT = "/aes/app/decrypt?val=";
    public static String WEB_STREAM_DECRYPT = "/aes/web/decrypt?val=";


    /** Stream Info apis */
    public static String streamInfoEndpoint(String track_ids, String hashkey){
        return "/stream/info?ids="+track_ids+"&hash="+hashkey;
    }

    /**Search Auto Suggest Endpoints */
    public static String searchFeed = "/searchfeed/fetch";
    public static String autoSuggestStageEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/entityd/mobilesuggest/search?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1&indent=true");
        return endpoint.toString();
    }

    public static String autoSuggestProdEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/autosuggest-lite-vltr-ro?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1&indent=true");
        return endpoint.toString();
    }

    public static String autoSuggestSolrEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/live/mobilesuggest/getErSolr?query=" + query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1&indent=true");
        return endpoint.toString();
    }

    /** Auto Suggest Mobile Endpoints */
    public static String autoSuggestMobileV2Endpoint(String keyword){
        StringBuilder endpoints = new StringBuilder();
        endpoints.append("/mobile/autocomplete-v2?query="+keyword+"&content_filter=2&geoLocation=IN&include=allItems&isRegSrch=0");
        return endpoints.toString().trim();
    }

    /**
     * Gsearch all endpoints
     * @return 
     */
    public static HashMap<String, String> searchEndpoints(){
        HashMap<String, String> endpoint_idetifier = new HashMap<>();
        endpoint_idetifier.put("lite_v1", "autosuggest-lite-v1");
        endpoint_idetifier.put("lite_v2", "autosuggest-lite-v2");
        endpoint_idetifier.put("vltro", "autosuggest-lite-vltr");
        endpoint_idetifier.put("vltro_demo", "autosuggest-lite-vltr-demo");
        endpoint_idetifier.put("lite_vltro", "autosuggest-lite-vltr-ro");
        endpoint_idetifier.put("lite_no_syn", "autosuggest-lite-nosynonyms");
        endpoint_idetifier.put("lite_vs1", "autosuggest-lite-vs1");
        endpoint_idetifier.put("lite_v3", "autosuggest-lite-v3");
        endpoint_idetifier.put("lite_cms", "autosuggest-lite-cms");
        endpoint_idetifier.put("lite_vi1", "autosuggest-lite-vi1");
        endpoint_idetifier.put("lite_tp", "autosuggest-lite-tp");
        endpoint_idetifier.put("vltro_mini", "autosuggest-lite-vltr-ro-mini");
        return endpoint_idetifier;
    }

    /**
     * for all endpoints regression
     * @param endpoint_val
     * @param query_params
     * @return
     */
    public static String autoSuggestEndpoint(String endpoint_val, String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/"+endpoint_val+"?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0");
        endpoint.append("&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1&indent=true");
        return endpoint.toString();
    }
}
