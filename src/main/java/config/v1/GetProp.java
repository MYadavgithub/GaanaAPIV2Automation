package config.v1;
import java.util.Properties;
import common.*;
import config.Constants;

/**
 * @author umesh-shukla
 * @version v1
 */
public class GetProp {

    public Properties prop;
    public static String baseurl;
    public static String httpProtocol;
    public static String getPortalXurl;
    public static String autoSuggestv2StageUrl;
    public static String getAutoSuggestv2LiveUrl;
    public static String deviceId;
    public static String country;
    public static String xForwardedFor;
    public static String PodcastAppVersion;
    public static String iosPodcastAppVersion;
    public static String gaanaAppVersionAndroid;
    public static String deviceTypeAndroid;
    public static String appVersion;
    public static String gaanaAppVersionIos;
    public static String IosDeviceId;
    public static String deviceTypeIos;
    public static String appVersionIOS;
    public static String deviceTypeBosch;
    public static String appVersionBosch;
    public static String deviceTypeGreatWall;
    public static String appVersionGreatWall;
    public static String deviceTypeSamsungfridge;
    public static String appVersionSamsungfridge;
    public static String deviceTypeWap;
    public static String deviceIdWap;
    public static String CIP;
    public static String appVersionWapApp;
    public static String deviceTypeWebApp;
    public static String deviceIdWebApp;
    public static String CIPWebApp;
    public static String appVersionWebApp;
    public static String tracks_td;
    public static String dbbaseurl;
    public static String dburl;
    public static String dbuser;
    public static String dbpwd;
    public static String streamBaseurl;
    public static String deviceType;
    public static String recoBaseurl;
    public static String deviceTypeMx;
    public static String gaanaAppVersionMx;
    public static String deviceTypeMiko;
    public static String deviceTypeGFA;
    public static String streamStageUrl;

    public GetProp() {
        super();
        this.prop = prop();
    }

    public Properties prop() {
        String env = GlobalConfigHandler.getEnv();
        String type = GlobalConfigHandler.getType();
        if(type.equalsIgnoreCase(Constants.API_TYPE_SEARCH) || type.equalsIgnoreCase(Constants.API_TYPE_LIVE_SEARCH)) {
            if (env.equals(Constants.STAGE_ENV)) {
                prop = FileActions.readProp("local.properties");
            }
            else if(env.equals(Constants.PRE_PROD_ENV)){
                prop = FileActions.readProp("preprod.properties");
            }
            else if(env.equals(Constants.PROD_ENV)){
                prop = FileActions.readProp("prod.properties");
            }

            return prop;
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

            return prop;
        }
        else if(type.equalsIgnoreCase(Constants.API_TYPE_STREAM)){
            if(env.equals(Constants.STAGE_ENV)){
                prop = FileActions.readProp("local.properties");
            }
            else if(env.equals(Constants.PRE_PROD_ENV)){
                prop = FileActions.readProp("preprod.properties");
            }
            else if(env.equals(Constants.PROD_ENV)){
                prop = FileActions.readProp("prod.properties");
            }

            return prop;
        }

        return null;
    }

    public String baseurl(){
        if(GlobalConfigHandler.getType().equals(Constants.API_TYPE_SEARCH)){
            return prop.getProperty("search_baseurl").toString().trim();
        }else if(GlobalConfigHandler.getType().equals(Constants.API_TYPE_LIVE_SEARCH)){
            return prop.getProperty("search_live_url").toString().trim();
        }else if(GlobalConfigHandler.getType().equals(Constants.API_TYPE_STREAM)){
            return getStreamBaseurl().toString().trim();
        }else if(GlobalConfigHandler.getType().equals(Constants.API_TYPE_RECO)){
            String baseurl = "";
            if(prop != null){
                if(Constants.RECO_BASEURL == 0){
                    baseurl = prop.getProperty("reco_baseurl").toString().trim();
                }else if(Constants.RECO_BASEURL == 1){
                    baseurl = prop.getProperty("prec_baseurl").toString().trim();
                }else if(Constants.RECO_BASEURL == 2){
                    baseurl = prop.getProperty("svd_rec_baseurl").toString().trim();
                }
            }
            return baseurl;
        }
        return null;
    }

    public String getHttpProtocol() {
        return prop.getProperty("https_protocols").toString().trim();
    }

    public String getPortalXurl() {
        return prop.getProperty("portalx_url").toString().trim();
    }

    public String getAutoSuggestv2StageUrl() {
        return prop.getProperty("auto_suggest_v2_stage_url").toString().trim();
    }

    public String getAutoSuggestv2LiveUrl() {
        return prop.getProperty("auto_suggest_v2_live_url").toString().trim();
    }

    public String getDeviceId() {
        return prop.getProperty("deviceId").toString().trim();
    }

    public void setDeviceId(String deviceId) {
        GetProp.deviceId = deviceId;
    }

    public String getCountry() {
        return prop.getProperty("COUNTRY").toString().trim();
    }

    public String getxForwardedFor() {
        return prop.getProperty("X-FORWARDED-FOR").toString().trim();
    }

    public String getPodcastAppVersion() {
        return prop.getProperty("podcast_app_version").toString().trim();
    }

    public String getIosPodcastAppVersion() {
        return prop.getProperty("ios_podcast_app_version").toString().trim();
    }

    public String getGaanaAppVersionAndroid() {
        return prop.getProperty("gaanaAppVersionAndroid").toString().trim();
    }

    public String getDeviceTypeAndroid() {
        return prop.getProperty("deviceTypeAndroid").toString().trim();
    }

    public String getAppVersion() {
        return prop.getProperty("appVersion").toString().trim();
    }

    public String getIosDeviceId() {
        return prop.getProperty("IosDeviceId").toString().trim();
    }

    public String getGaanaAppVersionIos() {
        return prop.getProperty("gaanaAppVersionIos").toString().trim();
    }

    public String getDeviceTypeIos() {
        return prop.getProperty("deviceTypeIos").toString().trim();
    }

    public String getAppVersionIOS() {
        return prop.getProperty("appVersionIOS").toString().trim();
    }

    public String getDeviceTypeBosch() {
        return prop.getProperty("deviceTypeBosch").toString().trim();
    }

    public String getAppVersionBosch() {
        return prop.getProperty("appVersionBosch").toString().trim();
    }

    public String getDeviceTypeGreatWall() {
        return prop.getProperty("deviceTypeGreatWall").toString().trim();
    }

    public String getAppVersionGreatWall() {
        return prop.getProperty("appVersionGreatWall").toString().trim();
    }

    public String getDeviceTypeSamsungfridge() {
        return prop.getProperty("deviceTypeSamsungfridge").toString().trim();
    }

    public String getAppVersionSamsungfridge() {
        return prop.getProperty("appVersionSamsungfridge").toString().trim();
    }

    public String getDeviceTypeWap() {
        return prop.getProperty("deviceTypeWap").toString().trim();
    }

    public String getDeviceIdWap() {
        return prop.getProperty("deviceIdWap").toString().trim();
    }

    public String getCIP() {
        return prop.getProperty("CIP").toString().trim();
    }

    public String getAppVersionWapApp() {
        return prop.getProperty("appVersionWapApp").toString().trim();
    }

    public String getDeviceTypeWebApp() {
        return prop.getProperty("deviceTypeWebApp").toString().trim();
    }

    public String getDeviceIdWebApp() {
        return prop.getProperty("deviceIdWebApp").toString().trim();
    }

    public String getCIPWebApp() {
        return prop.getProperty("CIPWebApp").toString().trim();
    }

    public String getAppVersionWebApp() {
        return prop.getProperty("appVersionWebApp").toString().trim();
    }

    public String getTracks_td() {
        return prop.getProperty("tracks_td").toString().trim();
    }

    public String getDbbaseurl() {
        return prop.getProperty("dbbaseurl").toString().trim();
    }

    public String getDburl() {
        return prop.getProperty("dburl").toString().trim();
    }

    public String getDbuser() {
        return prop.getProperty("dbuser").toString().trim();
    }

    public String getDbpwd() {
        return prop.getProperty("dbpwd").toString().trim();
    }

    public String getStreamBaseurl() {
        return prop.getProperty("stream_baseurl").toString().trim();
    }

    public String [] getDeviceType() {
        return prop.getProperty("device_type").toString().trim().replaceAll(" ", "").split(",");
    }

    public String getRecoBaseUrl(){
        return prop.getProperty("reco_baseurl").toString().trim();
    }

    public String getDeviceTypeMx() {
        return prop.getProperty("deviceTypeMx").toString().trim();
    }

    public String getGaanaAppVersionMx() {
        return prop.getProperty("gaanaAppVersionMx").toString().trim();
    }

    public String getDeviceTypeMiko() {
        return prop.getProperty("deviceTypeMiko").toString().trim();
    }

    public String getDeviceTypeGFA() {
        return prop.getProperty("deviceTypeGFA").toString().trim();
    }

    public String getStreamStageUrl() {
        return prop.getProperty("stream_stage_url").toString().trim();
    }
}