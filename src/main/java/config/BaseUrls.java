package config;
import java.util.Properties;
import common.FileActions;
import common.GlobalConfigHandler;

public class BaseUrls {

    public static Properties prop;

    public static String baseurl() {

        String env = GlobalConfigHandler.getEnv();
        String type = GlobalConfigHandler.getType();

        if (type.equalsIgnoreCase(Constants.API_TYPE_SEARCH)) {
            if (env.equals(Constants.STAGE_ENV)) {
                prop = FileActions.readProp("local.properties");
            }
            else if(env.equals(Constants.PRE_PROD_ENV)){
                prop = FileActions.readProp("preprod.properties");
            }
            else if(env.equals(Constants.PROD_ENV)){
                prop = FileActions.readProp("prod.properties");
            }

            return prop.getProperty("search_baseurl").toString().trim();
        }
        else if(type.equalsIgnoreCase(Constants.API_TYPE_RECO)){
            if(env.equals(Constants.STAGE_ENV)){
                prop = FileActions.readProp("local.properties");
            }
            else if(env.equals(Constants.PRE_PROD_ENV)){
                prop = FileActions.readProp("preprod.properties");
            }
            else if(env.equals(Constants.PROD_ENV)){
                prop = FileActions.readProp("prod.properties");
            }

            return prop.getProperty("reco_baseurl").toString().trim();
        }
        return null;
    }
}
