package config;
import java.util.Properties;
import common.FileActions;
import com.github.cliftonlabs.json_simple.JsonObject;

public class DbConfig {

    /**Currently not In use */

    public static JsonObject dbCredentials(int db_type) {
        JsonObject db = new JsonObject();
        Properties prop = FileActions.readProp(BaseUrls.baseurl());
        String db_base_url = prop.getProperty("dbbaseurl").toString().trim();
        if(db_type == 0){
            db.put("dburl", db_base_url+"/music");
        }
        else if(db_type == 1){
            db.put("dburl", db_base_url+"/music_x1");
        }else if(db_type == 1){
            db.put("dburl", db_base_url+"/music_logs");
        }
        else if(db_type == 1){
            db.put("dburl", db_base_url+"/gaana_svd");
        }
        else if(db_type == 1){
            db.put("dburl", db_base_url+"/gaana_users");
        }
        else if(db_type == 1){
            db.put("dburl", db_base_url+"/gaana_favourite");
        }
        else if(db_type == 1){
            db.put("dburl", db_base_url+"/gaana_recommendation");
        }
        db.put("dbuser", "mirchi");
        db.put("dbpwd", "appcmsmirchi");
        return db;
    }

    public static void main(String[] args) {
        JsonObject vaulue = dbCredentials(1);
        System.out.println(vaulue);
    }
}
