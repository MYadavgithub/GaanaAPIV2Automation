package db_queries;
import java.sql.Connection;
import org.json.JSONArray;
import org.slf4j.Logger;
import config.Constants;
import config.DbConnection;
import config.ExecuteQuery;
import org.slf4j.LoggerFactory;
import common.GlobalConfigHandler;

public class AutoQueueNewLogicQuery {

    DbConnection db_conn = new DbConnection();
    ExecuteQuery eq = new ExecuteQuery();
    private static Logger log = LoggerFactory.getLogger(AutoQueueNewLogicQuery.class);

    public JSONArray getReleaseYearAndTags(String track_ids) {
        StringBuilder _query = new StringBuilder();
        _query.append("SELECT tt.id AS track_id, tt.release_year, tmm.tags FROM `tm_track` tt ");
        _query.append("INNER JOIN tm_track_items_master_val_mapping tmm ON tt.id = tmm.item_id WHERE tt.`id` IN ");
        _query.append("("+track_ids+") GROUP BY tt.id;");

        log.info("Query : "+_query.toString().trim());

        if(!GlobalConfigHandler.getEnv().equals(Constants.PROD_ENV)){
            Connection conn = db_conn.createConnection();
            return eq.executeQuery(conn, _query.toString());
        }else{
            log.info("Production Environment Db query no allowed.");
        }
        return null;
    }
}