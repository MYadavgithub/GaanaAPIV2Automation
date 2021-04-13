package logic_controller;
import config.Endpoints;

/**
 * @author Umesh Shukla
 */
public class PodcastConroller {
    
    public String prepareUrlPFY(String baseurl, String device_id){
        return baseurl+Endpoints.podcastForYou+device_id;
    }
}
