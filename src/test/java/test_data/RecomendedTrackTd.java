package test_data;
import java.util.Arrays;
import java.util.List;

public class RecomendedTrackTd {

    public static int track_count = 30;

    public static List<String> exTrackObjectKeys() {
        String ex_keys [] = {"country","artist","rating","language","language_id","premium_content","sap_id",
        "duration","rtmp","parental_warning","content_source","albumseokey","vendor","popularity","stream_type",
        "lyrics_type","https","is_local","lyrics_url","artwork_large","user_rating","is_sonos_playable","seokey",
        "track_format","atw","gener","track_title","mobile","secondary_language","isrc","rtsp","artwork",
        "display_global","total_favourite_count","is_premium","is_most_popular","release_date","user_favorite",
        "track_id","http","album_id","artwork_web","stream_url","album_title"};
        return Arrays.asList(ex_keys);
    }

    public static List<String> exArtistObjectKeys() {
        String ex_keys [] = {"seokey", "name", "artist_id"};
        return Arrays.asList(ex_keys);
    }

    public static List<String> exTrackFromatKeys() {
		String ex_keys [] = {"normal", "high", "medium", "extreme"};
        return Arrays.asList(ex_keys);
	}

    public static List<String> removeObjectValidationKeys(){
        String ex_value [] = {"artist", "track_format", "gener"};
        return Arrays.asList(ex_value);
    }

}
