package config;
import org.slf4j.Logger;
import common.FileActions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import common.GlobalConfigHandler;


public class DbConnection {

    Connection connection = null;
    String env = System.getProperty("env");
    String dburl = null;
    String user = null;
    String pwd = null;
    private static Logger log = LoggerFactory.getLogger(DbConnection.class);

    public Connection createConnection() {
        getDbCreds();
        String url = "jdbc:mysql://"+dburl;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pwd);
            if(connection != null)
                log.info("Db connection established successfully.");
        }catch (ClassNotFoundException e) {
            throw new Error("Error During Connection!");
        }catch (SQLException e) {
           log.error(e.getMessage());
        }
        return connection;
    }

    private void getDbCreds() {
        String prop_file_name = "";
        String env = GlobalConfigHandler.getEnv().toString().trim();
        if(env.equalsIgnoreCase(Constants.STAGE_ENV)){
            prop_file_name = "local.properties";
        }else if(env.equalsIgnoreCase(Constants.PRE_PROD_ENV)){
            prop_file_name = "preprod.properties";
        }else if(env.equalsIgnoreCase(Constants.PRE_PROD_ENV)){
            prop_file_name = "prod.properties";
        }else{
            log.error("Unknown environment!");
        }
        
        dburl = FileActions.readProp(prop_file_name).getProperty("dburl").toString();
        user = FileActions.readProp(prop_file_name).getProperty("dbuser").toString();
        pwd = FileActions.readProp(prop_file_name).getProperty("dbpwd").toString();
    }
}
