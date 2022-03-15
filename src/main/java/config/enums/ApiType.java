package config.enums;

public enum ApiType {
    
    SEARCH("Search"),
    LIVE_SEARCH("Search_Live"),
    RECO("Reco"),
    STREAM("Stream");

    private final String apiType;

    ApiType(String apiType){
        this.apiType = apiType;
    }

    public String getApiType() {
        return apiType;
    }

    public static ApiType valueApiType(String apiType){
        for(ApiType dt : values()){
            if(dt.apiType.equalsIgnoreCase(apiType)){
                return dt;
            }
        }
        return null;
    }
}
