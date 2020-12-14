package common;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.Constants;

public class Headers {
    
    public static Properties prop;
    FileActions fa = new FileActions();
    private static final Logger log = LoggerFactory.getLogger(Headers.class);

    /** 
     * @param country 0 for country name and 1 for x-forwarded-for
     * @return
     */
    public static Map<String, String> getHeaders(int country) {
        getPropFileName();
        Map<String, String> headers = new HashMap<String, String>();
        int device_type = GlobalConfigHandler.getDeviceType();

        log.info("Requesting headers for app name : "+GlobalConfigHandler.getAppName(device_type));

        if(device_type == 0){
            headers.put("gaanaAppVersion", prop.getProperty("gaanaAppVersionAndroid").toString().trim());
            headers.put("deviceType", prop.getProperty("deviceTypeAndroid").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceId").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersion").toString().trim());
        }
        else if(device_type == 1){
            headers.put("gaanaAppVersion", prop.getProperty("gaanaAppVersionIos").toString().trim());
            headers.put("deviceType", prop.getProperty("deviceTypeIos").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceId").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersionIOS").toString().trim());
        }
        else if(device_type == 2){
            headers.put("gaanaAppVersion", prop.getProperty("gaanaAppVersionAndroid").toString().trim());
            headers.put("deviceType", prop.getProperty("deviceTypeBosch").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceId").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersionBosch").toString().trim());
        }
        else if(device_type == 3){
            headers.put("gaanaAppVersion", prop.getProperty("gaanaAppVersionAndroid").toString().trim());
            headers.put("deviceType", prop.getProperty("deviceTypeGreatWall").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceId").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersionGreatWall").toString().trim());
        }
        else if(device_type == 4){
            headers.put("gaanaAppVersion", prop.getProperty("gaanaAppVersionAndroid").toString().trim());
            headers.put("deviceType", prop.getProperty("deviceTypeSamsungfridge").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceId").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersionSamsungfridge").toString().trim());
        }
        else if(device_type == 5){
            headers.put("deviceType", prop.getProperty("deviceTypeWap").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceIdWap").toString().trim());
            headers.put("CIP", prop.getProperty("CIP").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersionWapApp").toString().trim());
        }
        else if(device_type == 6){
            headers.put("deviceType", prop.getProperty("deviceTypeWebApp").toString().trim());
            headers.put("deviceId", prop.getProperty("deviceIdWebApp").toString().trim());
            headers.put("CIP", prop.getProperty("CIPWebApp").toString().trim());
            headers.put("appVersion", prop.getProperty("appVersionWebApp").toString().trim());
        }

        if(country == 0){
            headers.put("COUNTRY", prop.getProperty("COUNTRY").toString().trim());
        }else{
            headers.put("X-FORWARDED-FOR", prop.getProperty("X-FORWARDED-FOR").toString().trim());
        }
        return headers;
    }

    private static void getPropFileName(){
        String env = GlobalConfigHandler.getEnv();
        if(env != null){
            if (env.equals(Constants.STAGE_ENV)) {
                prop = FileActions.readProp("local.properties");
            }
            else if(env.equals(Constants.PRE_PROD_ENV)){
                prop = FileActions.readProp("preprod.properties");
            }
            else if(env.equals(Constants.PROD_ENV)){
                prop = FileActions.readProp("prod.properties");
            }
        }else{
            prop = FileActions.readProp("local.properties");
        }
    }
}