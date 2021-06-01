package logic_controller;

import config.Endpoints;

public class SimilarAlbumController {
    
    public String createUrl(String baseurl, int album_id){
        return baseurl+Endpoints.similarAlbums+album_id;
    }
}