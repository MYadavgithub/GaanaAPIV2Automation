package config;

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

    /** Stream Info apis */
    public static String streamInfoEndpoint(String track_ids, String hashkey){
        return "/stream/info?ids="+track_ids+"&hash="+hashkey;
    }

    /**Search Auto Suggest Endpoints */
    public static String searchFeed = "/searchfeed/fetch";
    public static String autoSuggestStageEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/entityd/mobilesuggest/search?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1");
        return endpoint.toString();
    }

    public static String autoSuggestProdEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/autosuggest-lite-vltr-ro?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1");
        return endpoint.toString();
    }

    public static String autoSuggestSolrEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/live/mobilesuggest/getErSolr?query=" + query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1&autocomplete=1");
        return endpoint.toString();
    }

    /** Auto Suggest Mobile Endpoints */
    public static String autoSuggestMobileV2Endpoint(String keyword){
        StringBuilder endpoints = new StringBuilder();
        endpoints.append("/mobile/autocomplete-v2?query="+keyword+"&content_filter=2&geoLocation=IN&include=allItems&isRegSrch=0");
        return endpoints.toString().trim();
    }
}
