package db_queries;
import java.sql.Connection;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import config.DbConnection;
import config.ExecuteQuery;
import io.qameta.allure.Step;

public class RecommendedTrack {

    DbConnection db_conn = new DbConnection();
    ExecuteQuery eq = new ExecuteQuery();
    @Step("Feting data from databse for track ids : {0}")
    public JSONObject getTracksInfo(ArrayList<String> track_ids) {
        Connection conn = null;
        JSONObject response_data = new JSONObject();
        JSONArray tracks_data = new JSONArray();
        conn = db_conn.createConnection();

        if(track_ids.size() > 0){

            for(String track_id : track_ids){

                StringBuilder track_query = new StringBuilder();
                track_query.append("SELECT `id` as `track_id`, `seokey`, `albumseokey`, `title` as `track_title`, `album` as `album_id`, ");
                track_query.append("`album_title`, `language`, `language_id`, `release_date`, `total_favourite_count`, `pc_rule_id` as `is_premium`, ");
                track_query.append("`popall` as `popularity` FROM `tm_track` WHERE id = "+track_id+";");
                
                String artist_ids_query = "SELECT artists from tm_track where id = "+track_id+";";
                JSONArray artist_ids = eq.executeQuery(conn, artist_ids_query);
                String artists = artist_ids.getJSONObject(0).getString("artists").toString().trim();
                String artist_query = "";
                if(artists.length() > 0){
                    StringBuilder artist_query_create = new StringBuilder();
                    artist_query_create.append("SELECT `id` as `artist_id`, `name` ,`seokey`  FROM `tm_artist` ");
                    artist_query_create.append("WHERE `id` IN ("+artists+")");
                    artist_query = artist_query_create.toString();
                }
        
                String genre_list_query = "SELECT `genere` FROM `tm_track` WHERE `id` = "+track_id+";";
                JSONArray genre_ids = eq.executeQuery(conn, genre_list_query);
                String genres = genre_ids.getJSONObject(0).getString("genere").toString().trim();
        
                StringBuilder genre_query = new StringBuilder();
                genre_query.append("SELECT `id` as `genre_id`, `name` FROM `tm_generes` WHERE `id` IN ("+genres+");");
                
                JSONArray track_val = eq.executeQuery(conn, track_query.toString());
                JSONArray artist_val = new JSONArray();
                if(artist_query.length() > 0){
                    artist_val = eq.executeQuery(conn, artist_query.toString());
                }
                JSONArray genre_val = eq.executeQuery(conn, genre_query.toString());

                track_val.getJSONObject(0).put("artist", artist_val);
                track_val.getJSONObject(0).put("gener", genre_val);
                tracks_data.put(track_val.getJSONObject(0));
            }
        }
        
        response_data.put("tracks", tracks_data);
        db_conn.closeConnection(conn);
        return response_data;
    }
}