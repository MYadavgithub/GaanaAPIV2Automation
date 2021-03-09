package logic_controller;

import config.Endpoints;

public class RecoTrackController {
    
    public String prepRecoTrackIdsUrl(String baseurl, String track_id){
        return baseurl+Endpoints.recommendedTrackIds+track_id;
    }

    public String prepRecoTracksAqUrl(String baseurl, String track_id) {
        return baseurl+Endpoints.recommendedTracksAq+track_id;
    }
}
