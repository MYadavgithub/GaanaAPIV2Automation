package logic_controller;

import config.Endpoints;

public class HashTagVibesController {
    
    /**
     * Generates Hit Url
     * @param baseurl
     * @param hashtag_name
     * @return
     */
    public String prepareUrl(String baseurl, String hashtag_name){
        return baseurl+Endpoints.vibesHashTag+hashtag_name;
    }
}
