package config;

public class Endpoints {

    public static String recoTracks = "/recommendation/recommendedTracks/";
    public static String recoTracksPartner = "/recommendation/partnership/";
    public static String vibes = "/recommendation/vibes";
    public static String moodMix = "/for-you/moodmix";

    /**Search Auto Suggest Endpoints */
    public static String autoSuggestStageEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-ss/mobilesuggest/autosuggest-lite-vltr-ro?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English&testing=1&autocomplete=1");
        return endpoint.toString();
    }

    public static String autoSuggestProdEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/autosuggest-lite-vltr-ro?query="+query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English&testing=1&autocomplete=1");
        return endpoint.toString();
    }

    public static String autoSuggestSolrEndpoint(String query_params) {
        StringBuilder endpoint = new StringBuilder();
        endpoint.append("/gaanasearch-api/mobilesuggest/getErSolr?query=" + query_params);
        endpoint.append("&UserType=0&geoLocation=IN&content_filter=2&include=allItems&isRegSrch=0&usrLang=Hindi,English&testing=1&autocomplete=1");
        return endpoint.toString();
    }
}
