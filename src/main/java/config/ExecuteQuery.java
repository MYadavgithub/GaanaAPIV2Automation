package config;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import io.qameta.allure.Step;

public class ExecuteQuery {

    DbConnection dc = new DbConnection();
    @Step("Executing db query : {1}")
    public JSONArray executeQuery(Connection conn, String query) {
        // Connection conn = null;
        Statement stmt = null;
        ArrayList<String> columnName = new ArrayList<String>();
        JSONArray dataArray = new JSONArray();
        try {
            // conn = dc.createConnection();
            try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();
                for(int i = 1; i<=rsmd.getColumnCount(); i++){
                    columnName.add(rsmd.getColumnLabel(i));
                }

                int datarowCount = 0;
                while(rs.next()){
                    HashMap<String, String> values = new HashMap<String, String>();
                    for(String key : columnName){
                        if(rs.getString(key) == null){
                            values.put(key, "null");
                        }else{
                            values.put(key, rs.getString(key).toString());
                        }
                    }
                    /* Object val = data(columnName, rs); */
                    dataArray.put(datarowCount, values);
                    datarowCount++;
                }
                return dataArray;
            }catch(SQLException e) {
                throw new Error("Not able to execute query! "+query);
            }finally{
                if (stmt != null){ 
                    stmt.close();
                }
            }
        }catch(SQLException e) {
            throw new Error("Connection creation failed!", e);
        }/*finally {
            try {
                if(conn != null) {
                    conn.close();
                }
            }catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }*/
    }

    public void deleteQueryExecute(String query){
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dc.createConnection();
            try {
                stmt = conn.createStatement();
                stmt.executeUpdate(query);
            }catch(SQLException e) {
                throw new Error("Not able to execute query!", e);
            }finally{
                if (stmt != null){ 
                    stmt.close();
                }
            }
        }catch(SQLException e) {
            throw new Error("Connection creation failed!", e);
        }finally {
            try {
                if(conn != null) {
                    conn.close();
                }
            }catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}