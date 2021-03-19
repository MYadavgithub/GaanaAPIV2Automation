package config;

public class Endpoints {

    public static String recoTracks = "/recommendation/recommendedTracks/";
    public static String trendingShortTrack = "/recommendation/trendingShortTrack?viewAll=";
    public static String recoTracksPartner = "/recommendation/partnership/";
    public static String vibes = "/recommendation/vibes";
    public static String moodMix = "/for-you/moodmix";
    public static String madeForYou = "/madeforyou";
    public static String deviceConsumedLanguage = "/recommendation/deviceConsumedLanguage?deviceId=";
    public static String similarArtist = "/recommendation/similarArtist/";
    public static String similarArtistInfo = "/recommendation/similarArtistEntityInfo/";
    public static String vibesHashTag = "/recommendation/hashtag/vibes?hashtag=";

    /**Search Auto Suggest Endpoints */
    public static String searchFeed = "/searchfeed/fetch";
    public static String autoSuggestStageEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/autosuggest-lite-vltr-ro?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1");
        return endpoint.toString();
    }

    public static String autoSuggestProdEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/autosuggest-lite-vltr-ro?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1");
        return endpoint.toString();
    }

    public static String autoSuggestSolrEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/getErSolr?query=" + query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English,Punjabi&testing=1");
        return endpoint.toString();
    }

    /** Auto Suggest Mobile Endpoints */
    public static String autoSuggestMobileV2Endpoint(String keyword){
        StringBuilder endpoints = new StringBuilder();
        endpoints.append("/mobile/autocomplete-v2?query="+keyword+"&content_filter=2&geoLocation=IN&include=allItems&isRegSrch=0");
        return endpoints.toString().trim();
    }
}
